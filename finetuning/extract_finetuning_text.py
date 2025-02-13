import json
import argparse  # Import argparse to handle command-line arguments

# Read steps from steps.txt
def read_steps(file_path):
    steps = []
    with open(file_path, 'r') as file:
        lines = file.readlines()[2:]  
        for line in lines:
            parts = line.strip().split('|')
            # print(parts)
            if len(parts) >= 3:
                action = parts[2].strip()
                expected_result = parts[3].strip()
                steps.append((action, expected_result))
    return steps

# Read Java code from output.java
def read_java_code(file_path):
    with open(file_path, 'r') as file:
        return file.read()

# Extract relevant code segments based on steps
def extract_code_segments(java_code, steps):
    code_segments = []
    # Split the Java code into lines
    lines = java_code.splitlines()
    # Initialize a variable to keep track of the current line index
    current_line = 0
    capturing = False  # Flag to indicate if we are capturing lines

    for action, expected in steps:
        segment = []
        while current_line < len(lines):
            line = lines[current_line].strip()
            if line.startswith("//Step"):
                if capturing:  # If we were capturing, save the segment
                    code_segments.append("\n".join(segment).strip())
                    segment = []  # Clear the segment for the next step
                capturing = True  # Start capturing after the first step
            elif capturing:  # Only capture lines if we are in a capturing state
                segment.append(line)
            current_line += 1
        
        # Append the last segment if capturing was active
        if segment:
            code_segments.append("\n".join(segment).strip())

    return code_segments

# Generate JSON format
def generate_json(steps, code_segments):
    json_output = []
    for (action, expected), code in zip(steps, code_segments):
        json_output.append({
            "prompt": f"{action} - {expected}",
            "completion": code
        })
    return json_output

# Main function
def main():
    parser = argparse.ArgumentParser(description='Process some files.')
    parser.add_argument('-o', '--output', type=str, required=True,
                       help='The output JSON file name')

    args = parser.parse_args()

    # Fixed steps and Java files
    steps_files = ['steps1.txt', 'steps2.txt']  # Example fixed steps files
    java_files = ['output1.java', 'output2.java']  # Example fixed Java files

    json_output = []
    
    for steps_file, java_file in zip(steps_files, java_files):
        steps = read_steps(steps_file)
        java_code = read_java_code(java_file)
        code_segments = extract_code_segments(java_code, steps)
        json_output.extend(generate_json(steps, code_segments))  # Use extend to combine outputs

    with open(args.output, 'w') as f:
        json.dump(json_output, f, indent=4)

if __name__ == "__main__":
    main()