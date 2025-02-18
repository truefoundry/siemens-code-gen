from rag_llamaindex import run_rag, RAGConfig
from prompt_inference import load_prompt, generate_code, create_llm_client, get_messages
from evals import evaluate_code
import os
import glob 
import yaml

def run_experiment(config: dict) -> tuple:
    """
    Run a single experiment comparing prompt-based and RAG-based code generation
    
    Args:
        config: Dictionary containing all configuration parameters
    
    Returns:
        tuple: (prompt_inference_results, rag_results)
    """
    # Prompt-based inference
    prompt = load_prompt(config["paths"]["base_prompt_path"], 
                        config["paths"]["input_prompt_path"])
    
    llm = create_llm_client(
        model=config["llm"]["model"],
        temperature=config["system"]["temperature"],
        max_tokens=config["system"]["max_tokens"]
    )
    
    messages = get_messages(prompt, config["system"]["prompt"])
    generated_code = generate_code(llm, messages, config["paths"]["output_file"])
    prompt_inference_results = evaluate_code(generated_code, config["paths"]["ground_truth_file"])
    
    # RAG-based inference
    rag_config = RAGConfig(
        llm_model=config["llm"]["model"],
        embedding_model=config["llm"]["embedding_model"],
        similarity_top_k=config["llm"]["similarity_top_k"],
        data_dir=config["paths"]["data_dir"],
        base_prompt_path=config["paths"]["base_prompt_path"],
        input_prompt_path=config["paths"]["input_prompt_path"],
        output_file=config["paths"]["output_file"],
        ground_truth_file=config["paths"]["ground_truth_file"]
    )
    
    rag_generated_code, rag_source_texts, rag_source_names = run_rag(rag_config)
    rag_results = evaluate_code(rag_generated_code, config["paths"]["ground_truth_file"])
    
    return prompt_inference_results, rag_results

def run_experiments(config: dict, test_cases: list) -> tuple:
    """
    Run experiments for multiple test cases
    
    Args:
        config: Dictionary containing all configuration parameters
        test_cases: List of test case file paths
    
    Returns:
        tuple: (prompt_inference_results, rag_results) dictionaries
    """
    prompt_inference_results = {}
    rag_results = {}
    
    for test_case in test_cases:
        # Create config copy for this test case
        test_config = config.copy()
        test_config["paths"]["input_prompt_path"] = test_case
        test_config["paths"]["output_file"] = f"data/rag/{os.path.basename(test_case).split('.')[0]}_predictions_RAG.java"
        
        results = run_experiment(test_config)
        test_name = os.path.basename(test_case).split('.')[0]
        prompt_inference_results[test_name] = results[0]
        rag_results[test_name] = results[1]
    
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
    
    # Run experiments
    prompt_results, rag_results = run_experiments(config, test_case_files)