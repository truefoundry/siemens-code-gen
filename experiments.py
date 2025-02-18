from rag_llamaindex import create_index, generate_response
from llama_index.core import VectorStoreIndex
from prompt_inference import run_prompt_inference
from evals import evaluate_code
import os
import glob 
import yaml
from utils import (
    load_config, 
    load_prompt, 
    get_test_case_files, 
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
    prompt_generated_code, prompt_results = run_prompt_inference(config)
    rag_generated_code, rag_source_texts, rag_source_names = generate_response(index, config)
    rag_results = evaluate_code(config["paths"]["output_file_rag"], config["paths"]["ground_truth_file"])
    return prompt_results, rag_results, prompt_generated_code, rag_generated_code

def run_experiments(config: dict, test_cases: dict, index: VectorStoreIndex) -> tuple:
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
    
    for test_case_name, test_case_path in test_cases.items():
        # Create config copy for this test case
        test_config = config.copy()
        test_config["paths"]["input_prompt_path"] = test_case_path
        test_config["paths"]["output_file_rag"] = f"data/rag/{test_case_name}_predictions_RAG.java"
        test_config["paths"]["output_file_prompt"] = f"data/prompt_inference/{test_case_name}_predictions_prompt.java"
        #test_config["paths"]["ground_truth_file"] = f"data/ground_truth/{test_case_name}_predictions_prompt.java"
        prompt_results, rag_results, prompt_generated_code, rag_generated_code = run_experiment(test_config, index)
        prompt_inference_results[test_case_name] = prompt_results
        rag_results[test_case_name] = rag_results
    
    return prompt_inference_results, rag_results

def load_config(config_path: str = "config/config.yaml") -> dict:
    """Load configuration from YAML file"""
    with open(config_path, "r") as f:
        return yaml.safe_load(f)

if __name__ == "__main__":
    # Load configuration from YAML
    config = load_config()
    
    # Get test cases
    test_case_files = get_test_case_files(config["paths"]["test_case_files"])
    
    # Create index once for all experiments
    index = create_index(config)
    
    # Run experiments
    prompt_results, rag_results = run_experiments(config, test_case_files, index)

    print(prompt_results)
    print(rag_results)
