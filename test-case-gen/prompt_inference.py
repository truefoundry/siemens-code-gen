from langchain.schema import HumanMessage, SystemMessage
from langchain_community.chat_models import ChatOpenAI
from dotenv import load_dotenv
import os
load_dotenv(".env")



steps = f"""
        Step Actions:
        1. Log in to Digital Customer Portal
        2. Click on tile "Report an issue with an order or delivery"
        3. Enter data in to following fields:- Name - name of logged user, not possible to modify
        - E-Mail - email of logged user, not possible to modify
        - @Mobile
        - @Country
        - @State
        - Preferred @language
        For reference check attached screenshot"
        4. Click "Next" button
        5. Enter data in to following fields:- Request type - Possible to select one from the list(""Order Issue"", ""Question about Account"", ""Invoice inquiry"", ""Question about Order"")
        - Subject
        - Reason
        - Classification
        - Siemens Healthineers Team
        - Sold To
        - Ship to
        - Puchase Order or Order Number - add multiple entries by clicking on ""+ Add Order Number""
        - Additional E-Mail - add multiple entries by clicking on ""+ Add E-Mail""
        For reference check attached screenshot
        6. In section Attachments click on icon "Browse files", select files that will be attached to this issue and click "Open"
        7. Drag and drop file from your computer in to section Attachments
        8. Click "Next" button
        9. Verify that the content of fields on the screenFor reference check attached screenshot
        10. Check the checkboxes:- "Informational Only" or "Refer Out Request adter Submission"
        11. Click "Save + Submit" button
        12. Click "Yes, submit" button
        13. Click "Go back to the dashboard" button
        14. Verify that notification was not sent to email addresses menitoned in fields E-Mail and Additional E-Mail . For reference check attached screenshot .
        15. Click on tile "Show me my Requests"
        16. Click on tab "Created Requests"
        17. Enter request ID or part of Subject in to search field
        18. Click on filtered request
        19. "Verify content of fields on the screenFor reference check attached screenshot
        20. Enter text, add attachment and submit new comment by clicking ">" icon
        21. Select tab ""Request Information"" and verify content of fields on the screenFor reference check attached screenshot
        22. Select tab ""Requestor Information"" and verify content of fields on the screenFor reference check attached screenshot
        23. Select tab ""Request Overview"" and verify content of fields on the screenFor reference check attached screenshot
        24. Click on button "Back"
        25. Click on tab "Created Requests" and click on your request created in previous steps
        26. Select tab "Request Information" and click on the button "Delete"
        27. Click "Confirm" button
        28. Click on tile "Show me my Requests" and open tab "Created Requests"
        """

instructions = """
You are an AI bot that follows the steps to create a java code for the given steps for automated testing. Here are the steps:
"""

prompt = f"""
{instructions}
{steps}
"""

prompt = open("prompt.txt", "r").read()
# Add all files from exctracted text reference
for file in os.listdir("extracted_text_reference"):
    prompt += open(f"extracted_text_reference/{file}", "r").read()


llm = ChatOpenAI(
    model="openai-main/gpt-4o",
    temperature=0.7,
    max_tokens=4096,
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
    SystemMessage(content=""),
    HumanMessage(content=prompt),
]

if __name__ == "__main__":
    # Log the stream output to a file
    with open("TC01_Create_Request_predictions_context.java", "w") as file:
        file.write(llm.invoke(messages).content)

    # Save the java file as a .txt file reading from java files
    with open("TC01_Create_Request_predictions_context.txt", "w") as file:
        file.write(open("TC01_Create_Request_predictions_context.java", "r").read())

