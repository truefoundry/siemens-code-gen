import os
from dotenv import load_dotenv
from langchain import LLMChain, PromptTemplate
from langchain.llms import OpenAI
from langchain.chains.base import Chain
from utils import load_config

load_dotenv()

class TestCaseChain(Chain):
    """
    A LangChain-based chain that sequentially:
      1. Generates Java import statements.
      2. Outlines test steps.
      3. Combines them into a complete Java test case.
      4. Refactors/improves the test case.
      
    All steps are executed in one chain call.
    """
    input_keys = []      # No external inputs needed
    output_keys = ["final_code"]

    def __init__(self, config: dict, **kwargs):
        super().__init__(**kwargs)
        self.config = config
        
        # Initialize the LLM; adjust parameters as needed.
        self.llm = OpenAI(api_key=config.get("openai_api_key"), temperature=0)
        
        # Define prompt templates.
        self.imports_template = PromptTemplate(
            input_variables=[],
            template=(
                "Based on the test case requirement for a Java Selenium test, "
                "please generate only the necessary Java import statements. "
                "Output only the import statements without additional commentary."
            )
        )
        self.steps_template = PromptTemplate(
            input_variables=[],
            template=(
                "Based on the test case requirement for a Java Selenium test, "
                "please outline the test steps in the form of method implementations "
                "and/or comments. Output only the Java code representing these steps without extra explanation."
            )
        )
        self.combine_template = PromptTemplate(
            input_variables=["imports", "steps"],
            template=(
                "Given the following Java import statements and test steps, "
                "combine them into a complete, valid Java test case class with an appropriate class definition. "
                "Ensure proper structure and syntax, and do not include any commentary.\n\n"
                "Imports:\n{imports}\n\n"
                "Test Steps:\n{steps}"
            )
        )
        self.edit_template = PromptTemplate(
            input_variables=["combined"],
            template=(
                "The following Java test case code has been generated:\n{combined}\n\n"
                "Please refactor and improve this code so that it adheres to best practices, includes robust error handling, "
                "and follows proper naming conventions. Output only the final, improved Java code without additional commentary."
            )
        )
        
        # Create LLM chains for each step.
        self.imports_chain = LLMChain(llm=self.llm, prompt=self.imports_template)
        self.steps_chain = LLMChain(llm=self.llm, prompt=self.steps_template)
        self.combine_chain = LLMChain(llm=self.llm, prompt=self.combine_template)
        self.edit_chain = LLMChain(llm=self.llm, prompt=self.edit_template)

    def _call(self, inputs: dict) -> dict:
        # Execute the full pipeline in one chain call.
        imports = self.imports_chain.run({})
        steps = self.steps_chain.run({})
        combined = self.combine_chain.run({"imports": imports, "steps": steps})
        final_code = self.edit_chain.run({"combined": combined})
        return {"final_code": final_code}

    def predict(self) -> str:
        """Helper method to execute the chain and return the final edited code."""
        result = self.run({})
        return result["final_code"]

if __name__ == "__main__":
    config = load_config()
    chain = TestCaseChain(config)
    final_code = chain.predict()
    print("Final edited test case generated:")
    print(final_code) 