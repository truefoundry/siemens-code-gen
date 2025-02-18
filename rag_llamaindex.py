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

def run_rag(config: RAGConfig = RAGConfig()):
    """
    Run RAG pipeline with given configuration
    
    Args:
        config: RAGConfig object with pipeline settings
    """
    # Setup LLM and embedding models
    Settings.llm = OpenAI(model_name=config.llm_model, 
                            api_key=os.getenv("TF_API_KEY"), 
                            api_base=os.getenv("TF_API_BASE"))
    Settings.embed_model = OpenAIEmbedding(
        model_name=config.embedding_model, 
        api_key=os.getenv("TF_API_KEY"),
        api_base=os.getenv("TF_API_BASE")
    )
    
    # Load and index documents
    documents = SimpleDirectoryReader(config.data_dir).load_data()
    index = VectorStoreIndex.from_documents(documents)
    
    # Load prompt
    prompt = load_prompt(config.base_prompt_path, config.input_prompt_path)
    
    # Get token count
    # token_count = len(tiktoken.encoding_for_model(config.llm_model).encode(prompt))
    # print(f"Prompt token count: {token_count}")
    
    # Query and get response
    query_engine = index.as_query_engine(similarity_top_k=config.similarity_top_k)
    response = query_engine.query(prompt)
    
    # Extract source information
    source_nodes = response.source_nodes
    source_names = [node.metadata["file_path"] for node in source_nodes]
    source_texts = [node.node.text for node in source_nodes]
    
    print(f"Response generated with {len(source_texts)} source documents")
    print(f"Source documents: {source_names}")
    
    # Save response
    with open(config.output_file, "w") as f:
        f.write(str(response))
    
    # Evaluate
    evaluate_code(config.ground_truth_file, config.output_file)

    return response, source_texts, source_names

if __name__ == "__main__":
    # Example usage with custom config
    custom_config = RAGConfig(
        llm_model="openai-main/gpt-4o",
        embedding_model="text-embedding-3-large",
        similarity_top_k=8,
        data_dir="extracted_texts",
        base_prompt_path="data/base_prompt.txt",
        input_prompt_path="MDLA/Admin/TC_01_838.txt",
        ground_truth_file="data/ground_truth/TC01_Create_Request.java",
        output_file="data/rag/TC01_Create_Request_predictions_RAG.java"
    )
    response, source_texts, source_names = run_rag(custom_config)

    print(response)
    print(source_texts)
    print(source_names)

