from langchain.schema import HumanMessage, SystemMessage
from langchain_community.chat_models import ChatOpenAI
from dotenv import load_dotenv
import os
load_dotenv(".env")
from evals import evaluate_code


def load_prompt(base_prompt_path: str, input_prompt_path: str) -> str:
    """
    Load the base prompt and concatenate with reference texts.
    
    Args:
        base_prompt_path: Path to the main prompt file
        reference_dir: Directory containing reference text files
    
    Returns:
        str: Combined prompt text
    """
    with open(base_prompt_path, "r") as f:
        prompt = f.read()
    with open(input_prompt_path, "r") as f:
        input_prompt = f.read()
    prompt += input_prompt
    prompt += """```

    ```Output:

    ```"""
    return prompt

def create_llm_client(model: str, temperature: float, max_tokens: int) -> ChatOpenAI:
    """
    Create and configure the LLM client.
    
    Returns:
        ChatOpenAI: Configured LLM client
    """
    return ChatOpenAI(
        model="openai-main/gpt-4o",
        temperature=0.7,
        max_tokens=8192,
        model_kwargs={
            "top_p": 0.8,
            "presence_penalty": 0,
            "frequency_penalty": 0
        },
        streaming=True,
        api_key=os.getenv("TFY_API_KEY"),
        base_url=os.getenv("TFY_BASE_URL"),
        extra_headers={
            "X-TFY-METADATA": '{"tfy_log_request":"true"}',
        }
    )

def get_messages(prompt: str, system_prompt: str) -> list:
    """
    Create the message list for the LLM.
    
    Args:
        prompt: The prepared prompt text
    
    Returns:
        list: List of messages for the LLM
    """
    return [
        SystemMessage(content=system_prompt),
        HumanMessage(content=prompt),
    ]
def generate_code(llm: ChatOpenAI, messages: list, output_path: str) -> str:
    """
    Generate code using LLM.
    
    Args:
        llm: LLM client
        messages: List of messages for the LLM
        
    Returns:
        str: Generated code content
    """
    generated_code = llm.invoke(messages).content
    with open(output_path, "w") as file:
        file.write(generated_code)
    return generated_code


if __name__ == "__main__":
    # Configuration
    BASE_PROMPT_PATH = "data/base_prompt.txt"
    INPUT_PROMPT_PATH = "MDLA/Admin/TC_01_838.txt"
    GENERATED_CODE_PATH = "data/prompt_inference/TC01_Create_Request_prompt_inference.java"
    REFERENCE_PATH = "data/ground_truth/TC01_Create_Request_predictions.java"
    system_prompt = "You are an expert in writing java-selenium tests. You will be provided with examples of test case requirements and generated java-selenium tests. You will be then asked to write a test case for a new requirement that is provided to you."
    # Execute pipeline
    prompt = load_prompt(BASE_PROMPT_PATH, INPUT_PROMPT_PATH)
    llm = create_llm_client(model="openai-main/gpt-4o", temperature=0.7, max_tokens=8192)
    messages = get_messages(prompt, system_prompt)
    generated_code = generate_code(llm, messages, GENERATED_CODE_PATH)
    results = evaluate_code(GENERATED_CODE_PATH, REFERENCE_PATH)

