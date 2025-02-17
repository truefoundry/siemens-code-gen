from rag_llamaindex import run_rag, custom_config
from prompt_inference import generate_code
from evals import evaluate_code
import os
import glob 

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

if __name__ == "__main__":
    system_prompt = "You are an expert in automated testing. You are given a prompt and a set of steps. You need to generate a java code for the given steps for automated testing."
    test_case_files = get_test_case_files("data")
    test_case_details, test_case_names = get_test_case_details(test_case_files)
    config = {
        "llm_model": "gpt-4o",
        "embedding_model": "text-embedding-3-large",
        "similarity_top_k": 8,
        "ground_truth_file": "data/TC01_Internal_User_Landing_Page_Layout_Check.java",
        "prompt_file": "data/prompt_865_.txt",
        "output_file": "data/TC01_Internal_User_Landing_Page_Layout_Check_RAG_llamaindex_8.java"
    }

    run_experiments(custom_config, system_prompt, test_case_details, test_case_names)