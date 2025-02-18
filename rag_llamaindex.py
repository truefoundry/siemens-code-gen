from dataclasses import dataclass
from llama_index.core import VectorStoreIndex, SimpleDirectoryReader, StorageContext
from llama_index.core.settings import Settings
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
import tiktoken
from evals import evaluate_code
import os
from prompt_inference import load_prompt

@dataclass
class RAGConfig:
    """Configuration for RAG pipeline"""
    llm_model: str = "gpt-4o"
    embedding_model: str = "text-embedding-3-large"
    similarity_top_k: int = 8
    data_dir: str = "extracted_texts"
    base_prompt_path: str = "data/base_prompt.txt"
    input_prompt_path: str = "data/prompt_compressed.txt"
    output_file: str = "data/TC01_Create_Request_predictions_RAG_llamaindex.java"
    ground_truth_file: str = "data/ground_truth.java"
    system_prompt: str = "You are a helpful AI assistant specialized in Java programming."
    temperature: float = 0.3
    max_tokens: int = 8192
    top_p: float = 0.8
    presence_penalty: float = 0
    frequency_penalty: float = 0

def create_index(config: RAGConfig = RAGConfig()):
    """
    Create vector index from documents
    
    Args:
        config: RAGConfig object with indexing settings
    Returns:
        VectorStoreIndex: Created index
    """
    # Setup embedding model
    Settings.embed_model = OpenAIEmbedding(
        model_name=config.embedding_model, 
    )
    
    # Load and index documents
    documents = SimpleDirectoryReader(config.data_dir).load_data()
    index = VectorStoreIndex.from_documents(documents)
    
    return index

def generate_response(index: VectorStoreIndex, config: RAGConfig = RAGConfig()):
    """
    Generate response using RAG with given index and configuration
    
    Args:
        index: VectorStoreIndex to query from
        config: RAGConfig object with generation settings
    Returns:
        tuple: (response, source_texts, source_names)
    """
    # Setup LLM with system prompt
    Settings.llm = OpenAI(
        model=config.llm_model,
        # api_key=os.getenv("TF_API_KEY"),
        # api_base=os.getenv("TF_API_BASE"),
        system_prompt=config.system_prompt,
        temperature=config.temperature,
        top_p=config.top_p,
        presence_penalty=config.presence_penalty,
        frequency_penalty=config.frequency_penalty,
        max_tokens=config.max_tokens
    )
    
    # Load and format prompt
    base_prompt = load_prompt(config.base_prompt_path, config.input_prompt_path)
    formatted_prompt = f"""Based on the following context, generate a complete Java test case:

        {base_prompt}

        Generate ONLY the Java code without any explanations or markdown formatting. The response should start directly with the Java code:
        """
    
    # Query and get response
    query_engine = index.as_query_engine(
        similarity_top_k=config.similarity_top_k,
        verbose=True
    )
    response = query_engine.query(formatted_prompt)
    
    # Extract source information
    source_nodes = response.source_nodes
    source_names = [os.path.basename(node.metadata["file_path"]) for node in source_nodes]
    source_texts = [node.node.text for node in source_nodes]
    
    print(f"Response generated with {len(source_texts)} source documents")
    print(f"Source documents: {source_names}")
    
    # Save response
    with open(config.output_file, "w") as f:
        f.write(str(response))

    return response, source_texts, source_names

def run_rag(config: RAGConfig = RAGConfig()):
    """
    Run complete RAG pipeline with given configuration
    
    Args:
        config: RAGConfig object with pipeline settings
    Returns:
        tuple: (response, source_texts, source_names)
    """
    index = create_index(config)
    return generate_response(index, config)

if __name__ == "__main__":
    # Example usage with custom config
    config = RAGConfig(
        llm_model="gpt-4o",
        embedding_model="text-embedding-3-large",
        similarity_top_k=8,
        data_dir="extracted_texts",
        base_prompt_path="data/base_prompt.txt",
        input_prompt_path="MDLA/Admin/TC_01_838.txt",
        ground_truth_file="data/ground_truth/TC01_Create_Request.java",
        output_file="data/rag/TC01_Create_Request_predictions_RAG.java"
    )
    index = create_index(config)
    response, source_texts, source_names = generate_response(index, config)
    #response, source_texts, source_names = run_rag(custom_config)

    evaluate_code(config.ground_truth_file, config.output_file)

    print(response)
    print(source_texts)
    print(source_names)


