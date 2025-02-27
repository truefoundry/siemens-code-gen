### Action Steps
1. Parse the HTML files(docs) to text using BeautifulSoup - done
2. Get the number of tokens in the docs, steps, code snippets using tiktoken - done
3. Create a prompt for the LLM to generate the code based on the docs, steps, code snippets provided context window is in the range of max tokens
4. Use evals to evaluate the generated code and the expected code
5. Execute code and get the expected output
6. Use evals to evaluate the output of the code
7. Index the docs and create embeddings
8. Create a RAG pipeline taking the docs for context and the steps, code snippets for the prompt
9. Evaluate the RAG pipeline using evals for code generation and code execution
10. Create data for LLM Finetuning with steps, code pair
11. Finetune the LLM and evaluate the finetuned model using evals
12. Compare finetuned, RAG and LLM generated code 
13. Create a pipeline to generate code using the finetuned model and RAG pipeline together
14. Evaluate the pipeline using evals

Major Steps
Data Prep
Evals
RAG
LLM Finetuning

Add documentation for sample for rag pipeline

I tried a basic prompt engg approach, where I injected 4 excel files (converted to markdown) and their corresponding java test files and asked Qwen-32B-Coder model to predict test case for the 5th file.
The output generated asked the same LLM to compare in terms of functionality. We get around 90% similarity. But need to get it checked from someone.