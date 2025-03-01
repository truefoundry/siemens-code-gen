# Predict the imports 
# Use those imports as reference for RAG 
# Predict each step of the test case 
# Combine all the steps into a single test case class
# Edit and improve the test case to make it more robust

# Each step would use an LLM, may or may not include rag context. Using functions from rag_llamaindex.py and prompt_inference.py

import os
from prompt_inference import create_llm_client, get_messages, generate_code
from typing import List
from utils import load_config
from dotenv import load_dotenv

load_dotenv()

class TestCasePredictor:
    def __init__(self, config: dict):
        """
        Initialize the predictor with configuration and LLM client.
        Also creates a directory for intermediate generated files.
        """
        self.config = config
        self.llm_client = create_llm_client(config)
        # Variables to store intermediate steps
        self.imports = ""
        self.steps = ""
        self.combined = ""
        self.edited = ""
        # Directory for saving generated code parts
        self.generated_dir = "data/generated"
        self.rag = False
        os.makedirs(self.generated_dir, exist_ok=True)

    def get_steps(self, file_path: str) -> List[dict]:
        """
        Read and parse test case steps from the given markdown file path.
        
        Args:
            file_path (str): Path to the file containing test case steps in markdown table format.
            
        Returns:
            List[dict]: List of dictionaries containing parsed steps with 'step', 'action', and 'expected_result' keys.
        """
        with open(file_path, 'r') as file:
            lines = file.readlines()
        
        # Skip header rows (first 2 lines containing table header and separator)
        steps = []
        for line in lines[2:]:
            if '|' not in line:  # Skip empty lines
                continue
            
            # Split by | and remove empty strings
            parts = [part.strip() for part in line.split('|') if part.strip()]
            if len(parts) >= 3:
                steps.append({
                    'step': parts[0],
                    'action': parts[1].replace('<br>', '\n'),  # Convert HTML linebreaks
                    'expected_result': parts[2].replace('<br>', '\n')
                })
        
        return steps

    def predict_imports(self) -> str:
        """
        Use the LLM to predict the necessary Java import statements based on 
        the test case requirements and provided documentation.
        
        Returns:
            str: Generated Java import statements.
        """
        prompt = (
            "Based on the test case requirement for a Java Selenium test, "
            "please generate only the necessary Java import statements. "
            "Output only the import statements without additional commentary."
        )
        output_path = os.path.join(self.generated_dir, "predicted_imports.java")
        messages = get_messages(prompt, self.config['system']['prompt'])
        self.imports = generate_code(self.llm_client, messages, output_path)
        return self.imports

    def predict_steps(self) -> str:
        """
        Use the LLM to predict the detailed test steps (as method stubs and/or comments)
        required to implement the Java Selenium test case.
        
        Returns:
            str: Generated Java code representing the test steps.
        """
        prompt = (
            "Based on the test case requirement for a Java Selenium test, "
            "please outline the test steps in the form of method implementations and/or comments. "
            "Output only the Java code representing these steps without extra explanation."
        )
        output_path = os.path.join(self.generated_dir, "predicted_steps.java")
        messages = get_messages(prompt, self.config['system']['prompt'])
        self.steps = generate_code(self.llm_client, messages, output_path)
        return self.steps

    def combine_steps(self) -> str:
        """
        Combine the predicted imports and test steps into a complete, valid Java test case class.
        
        Returns:
            str: The complete Java test case code.
            
        Raises:
            ValueError: If either the imports or steps have not been generated.
        """
        if not self.imports or not self.steps:
            raise ValueError("Either imports or steps are missing. Please run predict_imports and predict_steps first.")
        
        prompt = (
            f"Given the following Java import statements and test steps, "
            "combine them into a complete, valid Java test case class with an appropriate class definition. "
            "Ensure proper structure and syntax, and do not include any commentary.\n\n"
            f"Imports:\n{self.imports}\n\n"
            f"Test Steps:\n{self.steps}\n"
        )
        output_path = os.path.join(self.generated_dir, "combined_test_case.java")
        messages = get_messages(prompt, self.config['system']['prompt'])
        self.combined = generate_code(self.llm_client, messages, output_path)
        return self.combined

    def edit_test_case(self) -> str:
        """
        Refactor and improve the combined Java test case to adhere to best practices.
        This includes enhancing robustness, ensuring proper formatting, and adding error handling where applicable.
        
        Returns:
            str: The refined and improved Java test case code.
        
        Raises:
            ValueError: If the combined test case code is not available.
        """
        if not self.combined:
            raise ValueError("Combined test case code is missing. Please run combine_steps first.")
        
        prompt = (
            f"The following Java test case code has been generated:\n{self.combined}\n\n"
            "Please refactor and improve this code so that it adheres to best practices, "
            "includes robust error handling, and follows proper naming conventions. "
            "Output only the final, improved Java code without additional commentary."
        )
        output_path = os.path.join(self.generated_dir, "edited_test_case.java")
        messages = get_messages(prompt, self.config['system']['prompt'])
        self.edited = generate_code(self.llm_client, messages, output_path)
        return self.edited

    def run_test_case(self) -> str:
        """
        Execute the complete pipeline:
          1. Predict the necessary Java import statements.
          2. Predict the detailed test steps.
          3. Combine the two into a complete test case class.
          4. Refactor and improve the test case for robustness.
          
        Returns:
            str: The final refined Java test case code.
        """
        print("Predicting necessary imports...")
        self.predict_imports()
        print("Predicting test steps...")
        self.predict_steps()
        print("Combining imports and test steps into a complete test case class...")
        self.combine_steps()
        print("Editing and improving the test case class...")
        final_code = self.edit_test_case()
        print("Final edited test case generated.")
        return final_code

if __name__ == "__main__":
    config = load_config()
    predictor = TestCasePredictor(config)
    # test each method in the predictor class
    steps = predictor.get_steps("Formatted_data/Text_files/838.txt")
    print(steps)
    predictor.predict_imports()
    predictor.predict_steps()
    predictor.combine_steps()
    predictor.edit_test_case()
    predictor.run_test_case()
