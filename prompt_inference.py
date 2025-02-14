from langchain.schema import HumanMessage, SystemMessage
from langchain_community.chat_models import ChatOpenAI
from dotenv import load_dotenv
import os
load_dotenv(".env")
from evals import evaluate_code


prompt = open("data/prompt_865_.txt", "r").read()
# Add all files from exctracted text reference
for file in os.listdir("extracted_text_reference"):
    prompt += open(f"extracted_text_reference/{file}", "r").read()


llm = ChatOpenAI(
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

messages = [
    SystemMessage(content="You are an expert in automated testing. You are given a prompt and a set of steps. You need to generate a java code for the given steps for automated testing."),
    HumanMessage(content=prompt),
]

if __name__ == "__main__":
    # Log the stream output to a file
    with open("data/TC01_Internal_User_Landing_Page_Layout_Check_prompt_inference.java", "w") as file:
        file.write(llm.invoke(messages).content)

    evaluate_code("data/TC01_Internal_User_Landing_Page_Layout_Check.java", "data/TC01_Internal_User_Landing_Page_Layout_Check_prompt_inference.java")


