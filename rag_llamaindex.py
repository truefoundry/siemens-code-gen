from llama_index.core import VectorStoreIndex, SimpleDirectoryReader, StorageContext
from llama_index.core.settings import Settings
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
import tiktoken
from evals import evaluate_code
import os
from utils import load_config, load_prompt, format_java_prompt

def create_index(config: dict):
    """
    Create vector index from documents
    
    Args:
        config: Dictionary containing indexing settings
    Returns:
        VectorStoreIndex: Created index
    """
    # Setup embedding model
    Settings.embed_model = OpenAIEmbedding(
        model_name=config["llm"]["embedding_model"], 
    )
    
    # Load and index documents
    documents = SimpleDirectoryReader(config["paths"]["data_dir"]).load_data()
    index = VectorStoreIndex.from_documents(documents)
    
    return index

def generate_response(index: VectorStoreIndex, config: dict):
    """
    Generate response using RAG with given index and configuration
    
    Args:
        index: VectorStoreIndex to query from
        config: Dictionary containing generation settings
    Returns:
        tuple: (response, source_texts, source_names)
    """
    # Setup LLM with system prompt
    Settings.llm = OpenAI(
        model=config["llm"]["model"],
        system_prompt=config["system"]["prompt"],
        temperature=config["system"]["temperature"],
        top_p=config["system"]["top_p"],
        presence_penalty=config["system"]["presence_penalty"],
        frequency_penalty=config["system"]["frequency_penalty"],
        max_tokens=config["system"]["max_tokens"],
        # api_base=os.getenv("TFY_BASE_URL"),
        # api_key=os.getenv("TFY_API_KEY")
    )
    
    # Load and format prompt
    base_prompt = load_prompt(config["paths"]["base_prompt_path"], 
                            config["paths"]["input_prompt_path"])
    formatted_prompt = format_java_prompt(base_prompt)
    
    # Query and get response
    query_engine = index.as_query_engine(
        similarity_top_k=config["llm"]["similarity_top_k"],
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
    with open(config["paths"]["output_file_rag"], "w") as f:
        f.write(str(response))

    return response, source_texts, source_names

def run_rag(config: dict):
    """
    Run complete RAG pipeline with given configuration
    
    Args:
        config: Dictionary containing pipeline settings
    Returns:
        tuple: (response, source_texts, source_names)
    """
    index = create_index(config)
    return generate_response(index, config)

if __name__ == "__main__":
    # Load configuration from YAML
    config = load_config()
    index = create_index(config)
    response, source_texts, source_names = generate_response(index, config)
    
    evaluate_code(config["paths"]["ground_truth_file"], 
                 config["paths"]["output_file_rag"])

    print(response)
    print(source_texts)
    print(source_names)


