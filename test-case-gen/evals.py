# Code eval from Huggingface - generates code, executes it and compares the output with test cases
from evaluate import load
import os
os.environ["HF_ALLOW_CODE_EVAL"] = "1"
code_eval = load("code_eval")
test_cases = ["assert add(2,3)==5"]
candidates = [["def add(a, b): return a+b"]]
pass_at_k, results = code_eval.compute(references=test_cases, predictions=candidates, k=[1])
print(pass_at_k)


# CodeBLEU - measures the similarity between the generated code and the expected 
# - Limited keywords vs. millions of words.

# - Tree structure vs. sequential structure.

# - Unique instructions vs. ambiguous semantic.


# n a nutshell, CodeBLEU is a weighted combination of n-gram match (BLEU), weighted n-gram match (BLEU-weighted), AST match and data-flow match scores.

# The metric has shown higher correlation with human evaluation than BLEU and accuracy metrics.


from codebleu import calc_codebleu
from codebleu import AVAILABLE_LANGS

prediction = "def add(a, b):\n    return a + b"
reference = "def sum(first, second):\n    return second + first"

# Update the calc_codebleu call with proper parameters
result = calc_codebleu(
    references=[reference], 
    predictions=[prediction], 
    lang="python",
    weights=(0.25, 0.25, 0.25, 0.25),
    tokenizer=None
)
print(f"CodeBLEU score: {result}")

# weights are for n-gram match 
# tokenizer defaults to s.split(), can use code specific tokenizer

import evaluate

# Load the CodeBLEU metric
metric = evaluate.load("dvitel/codebleu")

prediction = "def add ( a , b ) :\n return a + b"
reference = "def sum ( first , second ) :\n return second + first"

kwargs = {
        #"lang": "python",
        #"weights": (0.25, 0.25, 0.25, 0.25),
        #"tokenizer": None
    }

# Fix: Pass strings directly without splitting
result = metric.compute(
    predictions=[prediction],  # List of string predictions
    references=[reference], 
)
print(result)

          
import evaluate
from typing import Dict, Union, List, Any
import Levenshtein


def calculate_code_metrics(reference_code: str, generated_code: str) -> Dict[str, Any]:
    # Initialize metrics
    bleu = evaluate.load('bleu')
    rouge = evaluate.load('rouge')
    meteor = evaluate.load('meteor')
    chrf = evaluate.load('chrf')
    
    results = {}
    
    # # Compute BLEU score
    # try:
    #     bleu_result = bleu.compute(
    #         predictions=[generated_code.split()],
    #         references=[[reference_code.split()]],
    #     )
    #     results['bleu'] = float(bleu_result['bleu'])
    # except Exception as e:
    #     print(f"Error computing bleu: {e}")
    #     results['bleu'] = None
    
    # Compute ROUGE scores
    try:
        rouge_result = rouge.compute(
            predictions=[generated_code],
            references=[reference_code]
        )
        results['rouge'] = {k: float(v) for k, v in rouge_result.items()}
    except Exception as e:
        print(f"Error computing rouge: {e}")
        results['rouge'] = None
    
    # Compute METEOR score
    try:
        meteor_result = meteor.compute(
            predictions=[generated_code],
            references=[reference_code]
        )
        results['meteor'] = meteor_result
    except Exception as e:
        print(f"Error computing meteor: {e}")
        results['meteor'] = None
    
    # Compute CHRF score
    try:
        chrf_result = chrf.compute(
            predictions=[generated_code],
            references=[reference_code]
        )
        results['chrf'] = chrf_result
    except Exception as e:
        print(f"Error computing chrf: {e}")
        results['chrf'] = None


    # Add Levenshtein distance
    try:
        distance = Levenshtein.distance(reference_code, generated_code)
        similarity = 1 - (distance / max(len(reference_code), len(generated_code)))
        results['levenshtein_similarity'] = float(similarity)
    except Exception as e:
        print(f"Error computing Levenshtein similarity: {e}")
        results['levenshtein_similarity'] = None
    
    return results


results = calculate_code_metrics(reference, prediction)
for metric_name, score in results.items():
    print(f"{metric_name}: {score}")

import code_bert_score


if __name__ == "__main__":
    # Evaluate test case gen code
    generated_code = open("llm_output.txt", "r").read()
    reference_code = open("ground_truth.txt", "r").read()

    # Other metrics
    results = calculate_code_metrics(reference_code, generated_code)
    for metric_name, score in results.items():
        print(f"{metric_name}: {score}")

    # CodeBLEU score
    result = calc_codebleu(
        references=[reference_code], 
        predictions=[generated_code], 
        lang="java",
        weights=(0.25, 0.25, 0.25, 0.25),
        tokenizer=None
    )
    print(f"CodeBLEU score: {result}")


    # CodeBERT score
    pred_results = code_bert_score.score(cands=[generated_code], refs=[reference_code], lang='java')
    print(pred_results)

    precision, recall, f1, f3 = pred_results
    print(f"Precision: {precision}, Recall: {recall}, F1: {f1}, F3: {f3}")

    # f3 is when recall is weighted more heavily (9:1)

    reference_code = open("ground_truth.txt", "r").read()
    generated_code = open("TC01_Create_Request_predictions_context.txt", "r").read()
    #print(f"Reference code: {reference_code}")
    #print(f"Generated code: {generated_code}")

    results = calculate_code_metrics(reference_code, generated_code)
    for metric_name, score in results.items():
        print(f"{metric_name}: {score}")
        
    # Print metrics in formatted output
    print(f"Levenshtein Similarity: {results['levenshtein_similarity']:.4f}")
    print(f"ROUGE-L: {results['rouge']['rougeL']:.4f}")
    print(f"ROUGE-1/2: {results['rouge']['rouge1']:.4f} / {results['rouge']['rouge2']:.4f}")
    print(f"METEOR: {results['meteor']['meteor']:.4f}")
    print(f"CHRF: {results['chrf']['score']:.2f}")
    

    # CodeBLEU score
    result = calc_codebleu(
        references=[reference_code], 
        predictions=[generated_code], 
        lang="java",
        weights=(0.25, 0.25, 0.25, 0.25),
        tokenizer=None
    )
    print(f"CodeBLEU score: {result['codebleu']:.4f}")
    print(f"ngram_match_score: {result['ngram_match_score']:.4f}")
    print(f"weighted_ngram_match_score: {result['weighted_ngram_match_score']:.4f}")
    print(f"syntax_match_score: {result['syntax_match_score']:.4f}")
    print(f"dataflow_match_score: {result['dataflow_match_score']:.4f}")

    # CodeBERT score
    pred_results = code_bert_score.score(cands=[generated_code], refs=[reference_code], lang='java')
    print(pred_results)

    precision, recall, f1, f3 = pred_results
    print(f"Precision: {precision.item():.4f}, Recall: {recall.item():.4f}, F1: {f1.item():.4f}, F3: {f3.item():.4f}")

