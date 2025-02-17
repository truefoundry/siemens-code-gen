from rag_llamaindex import run_rag, custom_config
from prompt_inference import generate_code
from evals import evaluate_code
import os
import glob 
import yaml

def run_experiment(system_prompt: str, test_case_detail: str, test_case_name: str, config: dict):
    prompt = load_prompt(BASE_PROMPT_PATH, REFERENCE_DIR)
    llm = create_llm_client()
    messages = get_messages(prompt, system_prompt)
    generated_code = generate_code(llm, messages)
    prompt_inference_results = evaluate_code(generated_code, test_case_name)
    rag_generated_code, rag_source_texts, rag_source_names = run_rag(test_case_detail, custom_config)
    rag_results = evaluate_code(rag_generated_code, test_case_name)
    return prompt_inference_results, rag_results

def run_experiments(config: dict, system_prompt: str, test_case_details: list, test_case_names: list):
    prompt_inference_results = {}
    rag_results = {}
    for test_case_detail, test_case_name in zip(test_case_details, test_case_names):
        prompt_inference_results, rag_results = run_experiment(system_prompt, test_case_detail, test_case_name, config)
        prompt_inference_results[test_case_name] = prompt_inference_results
        rag_results[test_case_name] = rag_results
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
    
    # Get test cases using config
    test_case_files = get_test_case_files(config["paths"]["data_dir"])
    test_case_details, test_case_names = get_test_case_details(test_case_files)
    
    # Run experiments with config
    run_experiments(config, config["system"]["prompt"], test_case_details, test_case_names)