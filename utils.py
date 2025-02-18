import yaml
import glob
import os
from typing import Tuple, List, Dict

def load_config(config_path: str = "config/config.yaml") -> Dict:
    """Load configuration from YAML file"""
    with open(config_path, "r") as f:
        return yaml.safe_load(f)

def load_prompt(base_prompt_path: str, input_prompt_path: str) -> str:
    """
    Load the base prompt and concatenate with input prompt.
    
    Args:
        base_prompt_path: Path to the main prompt file
        input_prompt_path: Path to the input prompt file
    
    Returns:
        str: Combined prompt text
    """
    with open(base_prompt_path, "r") as f:
        prompt = f.read()
    with open(input_prompt_path, "r") as f:
        input_prompt = f.read()
    prompt += input_prompt
    prompt += "\n\n```Output: java\n\n```"
    return prompt

def get_test_case_files(directory: str) -> List[str]:
    """Get all test case file paths recursively from directory"""
    return glob.glob(directory + "/**/*.txt", recursive=True)

def get_test_case_details(files: List[str]) -> Tuple[List[str], List[str]]:
    """
    Get content of all test case files
    
    Returns:
        Tuple containing (test_case_contents, test_case_names)
    """
    test_case_details = []
    test_case_names = []
    for file in files:
        with open(file, "r") as f:
            test_case_details.append(f.read())
            test_case_names.append(os.path.basename(file).split(".")[0])
    return test_case_details, test_case_names

def format_java_prompt(base_prompt: str) -> str:
    """Format prompt for Java test case generation"""
    return f"""Based on the following context, generate a complete Java test case:

    {base_prompt}

    Generate ONLY the Java code without any explanations or markdown formatting. The response should start directly with the Java code:
    """ 