from rag_llamaindex import create_index, generate_response
from prompt_inference import load_prompt, generate_code, create_llm_client, get_messages
from evals import evaluate_code
import os
import glob 
import yaml
from llama_index.core.index import VectorStoreIndex
from utils import (
    load_config, 
    load_prompt, 
    get_test_case_files, 
    get_test_case_details,
    format_java_prompt
)


def run_experiment(config: dict, index: VectorStoreIndex) -> tuple:
    """
    Run a single experiment comparing prompt-based and RAG-based code generation
    
    Args:
        config: Dictionary containing all configuration parameters
        index: VectorStoreIndex for RAG-based generation
    
    Returns:
        tuple: (prompt_inference_results, rag_results, prompt_generated_code, rag_generated_code)
    """
    # Prompt-based inference
    base_prompt = load_prompt(config["paths"]["base_prompt_path"], 
                        config["paths"]["input_prompt_path"])
    
    formatted_prompt = format_java_prompt(base_prompt)

    llm = create_llm_client(
        model=config["llm"]["model"],
        temperature=config["system"]["temperature"],
        max_tokens=config["system"]["max_tokens"]
    )
    
    messages = get_messages(formatted_prompt, config["system"]["prompt"])
    prompt_generated_code = generate_code(llm, messages, config["paths"]["output_file_prompt"])
    prompt_results = evaluate_code(config["paths"]["output_file_prompt"], config["paths"]["ground_truth_file"])
    
    rag_generated_code, rag_source_texts, rag_source_names = generate_response(index, config)
    rag_results = evaluate_code(config["paths"]["output_file_rag"], config["paths"]["ground_truth_file"])
    
    return prompt_results, rag_results, prompt_generated_code, rag_generated_code

def run_experiments(config: dict, test_cases: list, index: VectorStoreIndex) -> tuple:
    """
    Run experiments for multiple test cases
    
    Args:
        config: Dictionary containing all configuration parameters
        test_cases: List of test case file paths
        index: VectorStoreIndex for RAG-based generation
    
    Returns:
        tuple: (prompt_inference_results, rag_results) dictionaries
    """
    prompt_inference_results = {}
    rag_results = {}
    
    for test_case in test_cases:
        # Create config copy for this test case
        test_config = config.copy()
        test_config["paths"]["input_prompt_path"] = test_case
        test_config["paths"]["output_file_rag"] = f"data/rag/{os.path.basename(test_case).split('.')[0]}_predictions_RAG.java"
        test_config["paths"]["output_file_prompt"] = f"data/prompt/{os.path.basename(test_case).split('.')[0]}_predictions_prompt.txt"
        
        prompt_results, rag_results, prompt_generated_code, rag_generated_code = run_experiment(test_config, index)
        test_name = os.path.basename(test_case).split('.')[0]
        prompt_inference_results[test_name] = prompt_results
        rag_results[test_name] = rag_results
    
    return prompt_inference_results, rag_results

def get_test_case_files(directory: str) -> list:
    """Get all test case file paths recursively from directory"""
    files = glob.glob(directory + "/**/*.txt", recursive=True)
    return files

def get_test_case_details(files: list) -> list:
    """Get content of all test case files"""
    test_case_details = []
    test_case_names = []
    for file in files:
        with open(file, "r") as f:
            test_case_details.append(f.read())
            test_case_names.append(os.path.basename(file).split(".")[0])
    return test_case_details, test_case_names

def load_config(config_path: str = "config/config.yaml") -> dict:
    """Load configuration from YAML file"""
    with open(config_path, "r") as f:
        return yaml.safe_load(f)

if __name__ == "__main__":
    # Load configuration from YAML
    config = load_config()
    
    # Get test cases
    test_case_files = get_test_case_files(config["paths"]["data_dir"])
    
    # Create index once for all experiments
    index = create_index(config)
    
    # Run experiments
    prompt_results, rag_results = run_experiments(config, test_case_files, index)