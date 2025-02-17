from rag_llamaindex import run_rag, custom_config
from prompt_inference import generate_code
from evals import evaluate_code
import os
import glob 

def run_experiment(system_prompt: str, test_case_detail: str, test_case_name: str, config: dict):
    rag_result = run_rag(test_case_detail, custom_config)
    generated_code = generate_code(rag_result)
    evaluate_code(generated_code, test_case_name)

def run_experiments(config: dict, system_prompt: str, test_case_details: list, test_case_names: list):
    for test_case_detail, test_case_name in zip(test_case_details, test_case_names):
        run_experiment(system_prompt, test_case_detail, test_case_name, config)

def get_test_case_details(directory: str):
    # Do a recursive search for all files in the directory and subdirectories
    files = glob.glob(directory + "/**/*.txt", recursive=True)
    # Get content of all files
    test_case_details = []
    for file in files:
        with open(file, "r") as f:
            test_case_details.append(f.read())
    return test_case_details

if __name__ == "__main__":
    system_prompt = "You are an expert in automated testing. You are given a prompt and a set of steps. You need to generate a java code for the given steps for automated testing."
    test_case_details = [get_test_case_details("data/TC01_Internal_User_Landing_Page_Layout_Check.txt")]
    test_case_names = ["TC01_Internal_User_Landing_Page_Layout_Check"]
    config = {
        "llm_model": "gpt-4o",
        "embedding_model": "text-embedding-3-large",
        "similarity_top_k": 8,
        "ground_truth_file": "data/TC01_Internal_User_Landing_Page_Layout_Check.java",
        "prompt_file": "data/prompt_865_.txt",
        "output_file": "data/TC01_Internal_User_Landing_Page_Layout_Check_RAG_llamaindex_8.java"
    }

    run_experiments(custom_config, system_prompt, test_case_details, test_case_names)