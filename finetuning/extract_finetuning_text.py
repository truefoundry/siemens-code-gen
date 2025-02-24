import json
import argparse  
import os

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
    # parser = argparse.ArgumentParser(description='Process some files.')
    # parser.add_argument('-o', '--output', type=str, required=True,
    #                    help='The output JSON file name')

    # args = parser.parse_args()

    output_file_path = "all_data.json"

    steps_folder = '/Users/jitender/Documents/GitHub/siemens-code-gen/Formatted_data/Text_files'  # Specify the folder path for steps files
    java_folder = '/Users/jitender/Documents/GitHub/siemens-code-gen/Formatted_data/Ground_truths'  # Specify the folder path for Java files

    # if os.path.exists(steps_folder):
    #     print("Contents of steps folder:", os.listdir(steps_folder))
    # else:
    #     print("Steps folder does not exist.")

    steps_files = [os.path.join(steps_folder, f) for f in os.listdir(steps_folder)]
    java_files = [os.path.join(java_folder, f) for f in os.listdir(java_folder)]

    
    json_output = []
    
    for steps_file, java_file in zip(steps_files, java_files):
        try:
            steps = read_steps(steps_file)
            java_code = read_java_code(java_file)
            code_segments = extract_code_segments(java_code, steps)
            json_output.extend(generate_json(steps, code_segments)) 
        except:
            print(steps_file) 
            

    with open(output_file_path, 'w') as f:
        json.dump(json_output, f, indent=4)

if __name__ == "__main__":
    main()