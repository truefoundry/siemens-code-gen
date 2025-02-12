from llama_index.core import VectorStoreIndex, SimpleDirectoryReader
from llama_index.llms.openai import OpenAI
from llama_index.embeddings.openai import OpenAIEmbedding
# Change settings for embeddings and llm in llamaindex
from llama_index.core import Settings
from dotenv import load_dotenv
import os
from prompt_inference import prompt
load_dotenv(".env")

llm = OpenAI(model="openai-main/gpt-4o", temperature=0.5, max_tokens=4096, 
api_base=os.getenv("TFY_BASE_URL"), api_key=os.getenv("TFY_API_KEY"))

embed_model = OpenAIEmbedding(model="openai-main/text-embedding-3-small", api_base=os.getenv("TFY_BASE_URL"), api_key=os.getenv("TFY_API_KEY"))

Settings.llm = llm
Settings.embed_model = embed_model


documents = SimpleDirectoryReader("extracted_text_reference").load_data()
index = VectorStoreIndex.from_documents(documents)

query_engine = index.as_query_engine()

response = query_engine.query(prompt)
print(response)