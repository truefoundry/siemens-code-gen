from langchain.schema import HumanMessage, SystemMessage
from langchain_openai.chat_models import ChatOpenAI

llm = ChatOpenAI(
    model="openai-main/gpt-4o",
    temperature=0.3,
    max_tokens=8192,
    model_kwargs={
        "top_p": 1,
        "presence_penalty": 0,
        "frequency_penalty": 0
    },
    streaming=True,
    api_key="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImpJTkY3bXJ2RjA3cWJNUzllelhYeU5GYTBWVSJ9.eyJhdWQiOiI2OTZlNzQ2NS03MjZlLTYxNmMtM2EzOS02MTM4Mzg2NTYxNjEiLCJleHAiOjM2ODc4NDcxODEsImlhdCI6MTcyODI5NTE4MSwiaXNzIjoidHJ1ZWZvdW5kcnkuY29tIiwic3ViIjoiY20xeXViczM4c2l3YzAxcXQ5d2hzNzA3eCIsImp0aSI6Ijg1MjFkZWQxLTExMTQtNGM3YS05ZWJkLTg1NGJjNDUxMDI1ZSIsInVzZXJuYW1lIjoiaW50ZXJuYWx0b2tlbiIsInVzZXJUeXBlIjoic2VydmljZWFjY291bnQiLCJ0ZW5hbnROYW1lIjoiaW50ZXJuYWwiLCJyb2xlcyI6WyJ0ZW5hbnQtbWVtYmVyIl0sImFwcGxpY2F0aW9uSWQiOiI2OTZlNzQ2NS03MjZlLTYxNmMtM2EzOS02MTM4Mzg2NTYxNjEifQ.auxiXDusKgjIBPfaC4VNNaTpFgYBjPFKYPnbcEdzZyF2HmpJ8paqJqWAgMETdj7JmoHTOuiuAQKTAu76JgkzTYU1Kwu6mDH4B6vUuZO6SiWr99Z3thmmnoyvD15Y1E-bCFo_JqSSCxbq-oNKOIEWJ7w3bR3U7jQ-orVAXuR7PkNfFP2-YFBuW-1gYkWizWfFYtAQTQQ8ZBIgC7X9KdNNeWyr0KMxNGXmvYZ-Q4Q9HFgzAIX91DFCO4_3QtB3F4AKWuCQs4V1_Wy9J8gUZAN587TP--CwFqstzo7nlj5pKX6UH4dgwVJ1M6LAaVGouQ_PvzmvRnB9UKUpmluaCS9zIg",
    base_url="https://llm-gateway.truefoundry.com/api/inference/openai"
)

with open("data/prompt_compressed.txt", "r") as f:
    prompt = f.read()

messages = [
    HumanMessage(content="You are expert in selenium testing. You will be provided with a prompt and you need to write a test case for the prompt."),
    SystemMessage(content=prompt),
]

response_content = ""
stream = llm.stream(input=messages)
for chunk in stream:
    chunk_content = chunk.content
    print(chunk_content, end="")
    response_content += chunk_content

# Save the response to a file
with open("data/generated/response_qwen.java", "w") as f:
    f.write(response_content)

from llama_index.llms.openai import OpenAI
from utils import load_config
config = load_config()
llm = OpenAI(
        model=config["llm"]["model"],
        api_key="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImpJTkY3bXJ2RjA3cWJNUzllelhYeU5GYTBWVSJ9.eyJhdWQiOiI2OTZlNzQ2NS03MjZlLTYxNmMtM2EzOS02MTM4Mzg2NTYxNjEiLCJleHAiOjM2ODc4NDcxODEsImlhdCI6MTcyODI5NTE4MSwiaXNzIjoidHJ1ZWZvdW5kcnkuY29tIiwic3ViIjoiY20xeXViczM4c2l3YzAxcXQ5d2hzNzA3eCIsImp0aSI6Ijg1MjFkZWQxLTExMTQtNGM3YS05ZWJkLTg1NGJjNDUxMDI1ZSIsInVzZXJuYW1lIjoiaW50ZXJuYWx0b2tlbiIsInVzZXJUeXBlIjoic2VydmljZWFjY291bnQiLCJ0ZW5hbnROYW1lIjoiaW50ZXJuYWwiLCJyb2xlcyI6WyJ0ZW5hbnQtbWVtYmVyIl0sImFwcGxpY2F0aW9uSWQiOiI2OTZlNzQ2NS03MjZlLTYxNmMtM2EzOS02MTM4Mzg2NTYxNjEifQ.auxiXDusKgjIBPfaC4VNNaTpFgYBjPFKYPnbcEdzZyF2HmpJ8paqJqWAgMETdj7JmoHTOuiuAQKTAu76JgkzTYU1Kwu6mDH4B6vUuZO6SiWr99Z3thmmnoyvD15Y1E-bCFo_JqSSCxbq-oNKOIEWJ7w3bR3U7jQ-orVAXuR7PkNfFP2-YFBuW-1gYkWizWfFYtAQTQQ8ZBIgC7X9KdNNeWyr0KMxNGXmvYZ-Q4Q9HFgzAIX91DFCO4_3QtB3F4AKWuCQs4V1_Wy9J8gUZAN587TP--CwFqstzo7nlj5pKX6UH4dgwVJ1M6LAaVGouQ_PvzmvRnB9UKUpmluaCS9zIg",
        api_base="https://llm-gateway.truefoundry.com/api/inference/openai",
        system_prompt=config["system"]["prompt"],
        temperature=config["system"]["temperature"],
        # top_p=config["system"]["top_p"],
        # presence_penalty=config["system"]["presence_penalty"],
        # frequency_penalty=config["system"]["frequency_penalty"],
        # max_tokens=config["system"]["max_tokens"],
    )

response = llm.complete(input="Hello")
print(response)

from llama_index.llms.openai import OpenAI
from llama_index.llms.openai_like import OpenAILike
from langchain.schema import ChatMessage


messages = [
    ChatMessage(
        role="system", content="You are a pirate with a colorful personality"
    ),
    ChatMessage(role="user", content="What is your name"),
]



response = OpenAI(model="openai-main/gpt-4o",
        api_key="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImpJTkY3bXJ2RjA3cWJNUzllelhYeU5GYTBWVSJ9.eyJhdWQiOiI2OTZlNzQ2NS03MjZlLTYxNmMtM2EzOS02MTM4Mzg2NTYxNjEiLCJleHAiOjM2ODc4NDcxODEsImlhdCI6MTcyODI5NTE4MSwiaXNzIjoidHJ1ZWZvdW5kcnkuY29tIiwic3ViIjoiY20xeXViczM4c2l3YzAxcXQ5d2hzNzA3eCIsImp0aSI6Ijg1MjFkZWQxLTExMTQtNGM3YS05ZWJkLTg1NGJjNDUxMDI1ZSIsInVzZXJuYW1lIjoiaW50ZXJuYWx0b2tlbiIsInVzZXJUeXBlIjoic2VydmljZWFjY291bnQiLCJ0ZW5hbnROYW1lIjoiaW50ZXJuYWwiLCJyb2xlcyI6WyJ0ZW5hbnQtbWVtYmVyIl0sImFwcGxpY2F0aW9uSWQiOiI2OTZlNzQ2NS03MjZlLTYxNmMtM2EzOS02MTM4Mzg2NTYxNjEifQ.auxiXDusKgjIBPfaC4VNNaTpFgYBjPFKYPnbcEdzZyF2HmpJ8paqJqWAgMETdj7JmoHTOuiuAQKTAu76JgkzTYU1Kwu6mDH4B6vUuZO6SiWr99Z3thmmnoyvD15Y1E-bCFo_JqSSCxbq-oNKOIEWJ7w3bR3U7jQ-orVAXuR7PkNfFP2-YFBuW-1gYkWizWfFYtAQTQQ8ZBIgC7X9KdNNeWyr0KMxNGXmvYZ-Q4Q9HFgzAIX91DFCO4_3QtB3F4AKWuCQs4V1_Wy9J8gUZAN587TP--CwFqstzo7nlj5pKX6UH4dgwVJ1M6LAaVGouQ_PvzmvRnB9UKUpmluaCS9zIg",
        api_base="https://llm-gateway.truefoundry.com/api/inference/openai").chat(messages)
print(response)

from llama_index.llms.openai_like import OpenAILike
from llama_index.core.llms import ChatMessage, MessageRole
response = OpenAILike(model="openai-main/gpt-4o-mini", is_chat_model=True,
        api_key="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImpJTkY3bXJ2RjA3cWJNUzllelhYeU5GYTBWVSJ9.eyJhdWQiOiI2OTZlNzQ2NS03MjZlLTYxNmMtM2EzOS02MTM4Mzg2NTYxNjEiLCJleHAiOjM2ODc4NDcxODEsImlhdCI6MTcyODI5NTE4MSwiaXNzIjoidHJ1ZWZvdW5kcnkuY29tIiwic3ViIjoiY20xeXViczM4c2l3YzAxcXQ5d2hzNzA3eCIsImp0aSI6Ijg1MjFkZWQxLTExMTQtNGM3YS05ZWJkLTg1NGJjNDUxMDI1ZSIsInVzZXJuYW1lIjoiaW50ZXJuYWx0b2tlbiIsInVzZXJUeXBlIjoic2VydmljZWFjY291bnQiLCJ0ZW5hbnROYW1lIjoiaW50ZXJuYWwiLCJyb2xlcyI6WyJ0ZW5hbnQtbWVtYmVyIl0sImFwcGxpY2F0aW9uSWQiOiI2OTZlNzQ2NS03MjZlLTYxNmMtM2EzOS02MTM4Mzg2NTYxNjEifQ.auxiXDusKgjIBPfaC4VNNaTpFgYBjPFKYPnbcEdzZyF2HmpJ8paqJqWAgMETdj7JmoHTOuiuAQKTAu76JgkzTYU1Kwu6mDH4B6vUuZO6SiWr99Z3thmmnoyvD15Y1E-bCFo_JqSSCxbq-oNKOIEWJ7w3bR3U7jQ-orVAXuR7PkNfFP2-YFBuW-1gYkWizWfFYtAQTQQ8ZBIgC7X9KdNNeWyr0KMxNGXmvYZ-Q4Q9HFgzAIX91DFCO4_3QtB3F4AKWuCQs4V1_Wy9J8gUZAN587TP--CwFqstzo7nlj5pKX6UH4dgwVJ1M6LAaVGouQ_PvzmvRnB9UKUpmluaCS9zIg",
        api_base="https://llm-gateway.truefoundry.com/api/inference/openai").chat(messages=[ChatMessage(role=MessageRole.USER, content="Hello")])
print(response)