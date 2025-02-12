import getpass
import os
from typing import List, Optional
from dataclasses import dataclass
import tiktoken
import bs4
from langchain.chat_models import init_chat_model
from langchain_openai import OpenAIEmbeddings
from langchain_core.vectorstores import InMemoryVectorStore
from langchain import hub
from langchain_community.document_loaders import WebBaseLoader, TextLoader
from langchain_core.documents import Document
from langchain_community.document_loaders import DirectoryLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_experimental.text_splitter import SemanticChunker
from langgraph.graph import START, StateGraph
from typing_extensions import TypedDict
from dotenv import load_dotenv

load_dotenv()

@dataclass
class RAGConfig:
    """Configuration for RAG pipeline"""
    chunk_size: int = 1000
    chunk_overlap: int = 200
    similarity_top_k: int = 3
    embedding_model: str = "text-embedding-3-large"
    llm_model: str = "gpt-4o-mini"
    prompt_template: str = "rlm/rag-prompt"
    splitter: str = "RecursiveCharacterTextSplitter"

class DocumentProcessor:
    """Handles document loading and processing"""
    def __init__(self, config: RAGConfig):
        self.config = config
        if config.splitter == "SemanticChunker":
            self.text_splitter = SemanticChunker(
                OpenAIEmbeddings(model=config.embedding_model)
            )
        else:
            self.text_splitter = RecursiveCharacterTextSplitter(
                chunk_size=config.chunk_size,
                chunk_overlap=config.chunk_overlap
            )

    def load_local_content(self, directory: str) -> List[Document]:
        """Load and split local content"""
        loader = DirectoryLoader(directory, glob="*.txt")
        docs = loader.load()
        return self.text_splitter.split_documents(docs)

    def load_web_content(self, url: str) -> List[Document]:
        """Load and split web content"""
        loader = WebBaseLoader(
            web_paths=(url,),
            bs_kwargs=dict(
                parse_only=bs4.SoupStrainer(
                    class_=("post-content", "post-title", "post-header")
                )
            )
        )
        docs = loader.load()
        return self.text_splitter.split_documents(docs)

class RAGPipeline:
    """Main RAG pipeline implementation"""
    def __init__(self, config: RAGConfig):
        self.config = config
        self._initialize_components()
        self._setup_graph()
    
    def _initialize_components(self):
        """Initialize LLM, embeddings, and vector store"""
        if not os.environ.get("OPENAI_API_KEY"):
            os.environ["OPENAI_API_KEY"] = getpass.getpass("Enter API key for OpenAI: ")
        
        self.llm = init_chat_model(self.config.llm_model, model_provider="openai")
        self.embeddings = OpenAIEmbeddings(model=self.config.embedding_model)
        self.vector_store = InMemoryVectorStore(self.embeddings)
        self.prompt = hub.pull(self.config.prompt_template)
    
    def _setup_graph(self):
        """Setup the RAG processing graph"""
        class State(TypedDict):
            question: str
            context: List[Document]
            answer: str
        
        def retrieve(state: State):
            retrieved_docs = self.vector_store.similarity_search(state["question"], k=self.config.similarity_top_k)
            return {"context": retrieved_docs}
        
        def generate(state: State):
            docs_content = "\n\n".join(doc.page_content for doc in state["context"])
            messages = self.prompt.invoke({"question": state["question"], "context": docs_content})
            response = self.llm.invoke(messages)
            return {"answer": response.content}
        
        graph_builder = StateGraph(State).add_sequence([retrieve, generate])
        graph_builder.add_edge(START, "retrieve")
        self.graph = graph_builder.compile()
    
    def add_documents(self, documents: List[Document]):
        """Add documents to vector store"""
        self.vector_store.add_documents(documents=documents)
    
    def query(self, question: str) -> str:
        """Query the RAG pipeline"""
        response = self.graph.invoke({"question": question})
        return response["answer"]

def count_tokens(file_path: str) -> int:
    """Count the number of tokens in a file using tiktoken"""
    with open(file_path, "r") as file:
        text = file.read()
    return len(tiktoken.encoding_for_model("gpt-4o").encode(text))


def analyze_token_statistics(directory: str) -> List[int]:
    """Analyze token statistics for files in a directory.
    
    Args:
        directory (str): Path to directory containing text files
        
    Prints:
        - Average token count across files
        - Standard deviation of token counts
        - List of (filename, token count) tuples
    """
    token_counts = []
    for file in os.listdir(directory):
        token_count = count_tokens(f"{directory}/{file}")
        token_counts.append(token_count)
        
    # Average token count and standard deviation
    total_tokens = sum(token_counts)
    average_tokens = total_tokens / len(token_counts)
    variance = sum((token - average_tokens) ** 2 for token in token_counts) / len(token_counts)
    std_deviation = variance ** 0.5
    
    print(f"Average token count: {average_tokens:.2f}")
    print(f"Standard deviation: {std_deviation:.2f}") 
    #print(f"Token counts: {token_counts}")
    print(f"Max token count: {max(token_counts)}")
    print(f"Min token count: {min(token_counts)}")
    print(f"Total tokens: {total_tokens}")

    return token_counts


def main():
    # Initialize pipeline
    config = RAGConfig()
    processor = DocumentProcessor(config)
    pipeline = RAGPipeline(config)
    
    # Load and index documents
    #docs = processor.load_web_content("https://lilianweng.github.io/posts/2023-06-23-agent/")
    documentation_directory = "extracted_texts"
    docs = processor.load_local_content(documentation_directory)
    pipeline.add_documents(docs)
    

    prompt = open("data/prompt.txt", "r").read()

    # Example query
    question = "What is Core Browser Control in Java?"
    answer = pipeline.query(prompt)
    print(answer)

    with open("data/TC01_Create_Request_predictions_RAG.txt", "w") as file:
        file.write(answer)

    token_counts = analyze_token_statistics(directory="extracted_texts")
    
    # Sort the token counts in descending order
    token_counts.sort(reverse=True)
    print(token_counts)





if __name__ == "__main__":
    main()


