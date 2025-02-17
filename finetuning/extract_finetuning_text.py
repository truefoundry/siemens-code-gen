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
    steps_files = [
                   'MDLA/Admin/TC_01_838.txt', 
                   'MDLA/Admin/TC_02_839.txt',
                   'MDLA/Admin/TC_03_840.txt', 
                   'MDLA/Admin/TC_04_841.txt',
                   'MDLA/Admin/TC_05_842.txt',
                   'MDLA/E2E/TC_01_1199.txt',
                   'MDLA/External_User/TC_01_854.txt',
                   'MDLA/External_User/TC_02_855.txt',
                   'MDLA/External_User/TC_03_856.txt',
                   'MDLA/External_User/TC_04_857.txt',
                   'MDLA/External_User/TC_05_863.txt',
                   'MDLA/Internal_User/TC_01_865.txt',
                   'MDLA/Internal_User/TC_02_866.txt',
                   'MDLA/Internal_User/TC_03_867.txt',
                   'MDLA/Internal_User/TC_04_868.txt',
                   'MDLA/Internal_User/TC_05_869.txt']
    java_files = ['/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/Admin/TC01_Landing_Page_Layout_check.java', 
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/Admin/TC02_Top_Ribbon_Functionality_Check.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/Admin/TC03_Create_Request.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/Admin/TC04_Show_Me_All_Requests_Layout_Check.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/Admin/TC05_Admin_Menu_User_Management.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/E2E/E2E.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/External_User/TC01_External_User_Landing_Page_Layout_Check.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/External_User/TC02_External_User_Top_Ribbon_Functionality_Check.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/External_User/TC03_External_User_Issue_with_an_order_or_deliver.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/External_User/TC04_External_User_Question_About_An_Order_Or_eSupport_Assistance.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/External_User/TC05_External_User_Question_About_My_Account.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/Internal_User/TC01_Internal_User_Landing_Page_Layout_Check.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/Internal_User/TC02_Internal_User_Top_Ribbon_Functionality_Check.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/Internal_User/TC03_Internal_User_Issue_with_an_order_or_deliver.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/Internal_User/TC04_Internal_User_Question_About_An_Order_Or_eSupport_Assistance.java',
                  '/Users/jitender/Documents/GitHub/siemens-code-gen/DataSets/MDLA/src/test/java/Internal_User/TC05_Internal_User_Question_About_My_Account.java']  # Example fixed Java files

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