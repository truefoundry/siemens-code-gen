package Upload_Document;

import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.DirectoryControl;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class Upload_Document
{
    @Test
    void Upload_Document()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.UPLOAD_DOCUMENT, "43992", tc ->
        {
            String supplierID = "SHS0132";
            String attached_file = "SLIM_TEST.pdf";
            String attached_file_2 = "SLIM_DOC.docx";
            String attached_file_3 = "SLIM_2.pdf";
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);

            //STEP 1
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(10));
            tc.menu.select(EMenu.CREATE, EMenu.UPLOAD_DOCUMENTS);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.addStepInfo("'Choose QMS' screen opens", true, tc.browser.getPageHeaders().contains("Choose QMS"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            tc.combo.select(EComboBox.SELECT_A_QMS, "LD_POC");
            tc.button.click(EButton.SUBMIT);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.button.exists(EButton.SEARCH_FOR_SUPPLIERS));
            tc.addStepInfo("Search for Supplier screen opens", true, tc.browser.getPageHeaders().contains("Search for Supplier"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            WaitFor.condition(() -> tc.node.getValues(ENode.CASE_HEADER).contains("UD"), Duration.ofSeconds(5));
            String UD = tc.node.getValues(ENode.CASE_HEADER).get(0);
            tc.edit.sendKeys(EEdit.SUPPLIER_ID,supplierID,true);
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()-> tc.node.getParagraph(ENode.SEARCH_FOR_SUPPLIER)
                    .contains("Supplier search is now complete"), Duration.ofSeconds(10));
            tc.addStepInfo("A list off suppliers (Name beginning with 'Alten') is shown", true,
                    tc.node.getParagraph(ENode.SEARCH_FOR_SUPPLIER).contains("Supplier search is now complete"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.table.selectCheckBox(ETable.byIndex(0), EColumn.SELECT, 0);
            tc.button.click(EButton.CONTINUE);
            WaitFor.condition(() -> tc.browser.getPageHeaders().contains("Upload Documents"));
            tc.addStepInfo("The 'Upload Documents' screen opens", true, tc.browser.getPageHeaders()
                    .contains("Upload Documents"), new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.browser.getAlertMessage().contains("Please correct flagged fields"));
            WaitFor.specificTime(Duration.ofMillis(1500));
            String alert = tc.browser.getAlertMessage();
            tc.browser.closeAlert(true);
            WaitFor.condition(() -> !tc.browser.alertExists());
            tc.addStepInfo("Warning message 'Please correct flagged fields before submitting the form!' appears.", alert,
                    "Please correct flagged fields before submitting the form!", new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            tc.button.click(EButton.ACTIONS);
            tc.addStepInfo("\"Cancel case\" is available", true, tc.button.isDropDownItemPresent(EButton.ACTIONS, EDropDown.CANCEL_CASE),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            List<String> expectedValues = Arrays.asList("Select", "Basic Information", "Categorization", "Certificate",
                    "Contract", "Cyber Security", "Data Protection", "Development", "EHS", "MonthlyEvaluation", "Others",
                    "Performance Evaluation", "Qualification", "Quality Audit", "Risk Evaluation", "Sourcing Decision", "Tax/ECC");
            List<String> actualValues = tc.combo.getAvailableOptions(EComboBox.DOCUMENT_CATEGORY);
            tc.addStepInfo("""
                    The following document categories will appear in alphabetical order  - Basic Information- Categorization
                    - Certificate
                    - Contract- Cyber Security
                    - Data Protection
                    - Development
                    - EHS
                    - Others
                    - Performance Evaluation
                    - Qualification
                    - Quality Audit
                    - Risk Evaluation
                    - Sourcing Decision
                    - Tax/ECC""", true, actualValues.equals(expectedValues),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 8
            tc.stepEvaluator.reset();
            String validFrom = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String validTo = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            tc.combo.select(EComboBox.DOCUMENT_CATEGORY, "Certificate");
            tc.button.click(EButton.ADD);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.CERTIFICATE_TYPE));
            tc.stepEvaluator
                    .add(() -> tc.combo.exists(EComboBox.CERTIFICATE_TYPE), "Certificate type is not visible")
                    .add(() -> tc.edit.exists(EEdit.VALID_FROM), "Valid from is not visible")
                    .add(() -> tc.edit.exists(EEdit.VALID_TO), "Valid to is not visble");
            tc.addStepInfo("Fields to specify Certificate will appear (Certificate Type, Valid From, Valid To)", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 9
            tc.combo.select(EComboBox.CERTIFICATE_TYPE, "ISO 9001:2015");
            tc.addStepInfo("ISO 9001:2015' is populated in 'Certificate Type' field", true, tc.combo.
                    getSelected(EComboBox.CERTIFICATE_TYPE).equalsIgnoreCase("ISO 9001:2015"), new ComparerOptions().takeScreenShotPlatform());

            //Step 10
            tc.edit.sendKeys(EEdit.VALID_FROM, validFrom);
            tc.addStepInfo("The selected date is populated in 'Valid From' field", true, tc.edit.getText(EEdit.VALID_FROM).
                    equalsIgnoreCase(validFrom), new ComparerOptions().takeScreenShotPlatform());

            //Step 11
            tc.edit.sendKeys(EEdit.VALID_TO, validTo);
            tc.addStepInfo("The selected date is populated in 'Valid To' field", true, tc.edit.getText(EEdit.VALID_TO)
                    .equalsIgnoreCase(validTo), new ComparerOptions().takeScreenShotPlatform());

            //Step 12
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.button.click(EButton.UPLOAD_FILE);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Attach file(s)' popup opens", true, tc.modal.getTitle().equalsIgnoreCase
                    ("Attach file(s)"), new ComparerOptions().takeScreenShotPlatform());

            //Step 13
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.button.sendKeys(EButton.SELECT_FILES, String.valueOf(DirectoryControl.getPathOfTestFile(attached_file)));
            tc.addStepInfo("Open File' Window opens", true, tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 14
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.byIndex(1)).contains(attached_file),
                    Duration.ofSeconds(7));
            tc.addStepInfo("Name of the selected file will appear in 'Attach file(s)' screen", true,
                    tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.byIndex(1)).contains(attached_file),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 15
            tc.button.click(EButton.ATTACH);
            WaitFor.condition(() -> !tc.node.getValues(ENode.ATTACHMENTS).isEmpty());
            List<String> attachedFile = tc.node.getValues(ENode.ATTACHMENTS);
            tc.addStepInfo("File will appear below the 'upload file' button of certificate", true,
                    attachedFile.contains("SLIM_TEST"), new ComparerOptions().takeScreenShotPlatform());

            //Step 16
            tc.stepEvaluator.reset();
            tc.combo.select(EComboBox.DOCUMENT_CATEGORY, "Contract");
            tc.button.click(EButton.ADD);
            WaitFor.condition(() -> tc.edit.exists(EEdit.DOCUMENT_TITLE));
            tc.stepEvaluator
                    .add(() -> tc.combo.exists(EComboBox.CONTRACT_TYPE), "Contract type is not visible")
                    .add(() -> tc.edit.exists(EEdit.DOCUMENT_TITLE), "Document Title is not visible")
                    .add(() -> tc.edit.exists(EEdit.URL_TO_CLM_MODULE_OF_SCM_STAR), "URL to CLM module of SCM STAR is not visble");
            tc.addStepInfo("Fields to specify Certificate will appear (Certificate Type, Valid From, Valid To)", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 17
            tc.edit.sendKeys(EEdit.DOCUMENT_TITLE, "QAA for ALTEN 2023-03-20");
            tc.addStepInfo("QAA for ALTEN 2023-03-20' is populated in 'Certificate Title' field", true,
                    tc.edit.getText(EEdit.DOCUMENT_TITLE).equalsIgnoreCase("QAA for ALTEN 2023-03-20"), new ComparerOptions().takeScreenShotPlatform());

            //Step 18
            tc.edit.sendKeys(EEdit.URL_TO_CLM_MODULE_OF_SCM_STAR, "www.siemens.com/qaa");
            tc.addStepInfo(" URL will be prepopulated in URL field", true,
                    tc.edit.getText(EEdit.DOCUMENT_TITLE).equalsIgnoreCase("QAA for ALTEN 2023-03-20"), new ComparerOptions().takeScreenShotPlatform());

            //Step 19
            List<String> dropdownItems = List.of("Select", "QAA", "MPA", "NDA", "CoC", "EDI", "BOM", "Others");
            List<String> contractOptions = tc.combo.getAvailableOptions(EComboBox.CONTRACT_TYPE);
            tc.addStepInfo("""
                    The following options appear- QAA - Qality Assurance Agreement
                    - MPA - Master Purchase Agreement
                    - NDA - Non Disclosure Agreement
                    - CoC - Code of Conduct
                    - EDI - Electronic Data Interchange
                    - BOM - BOM Check Agreement
                    - Others""", true, contractOptions.equals(dropdownItems));

            //Step 20
            tc.combo.select(EComboBox.CONTRACT_TYPE, "QAA");
            tc.addStepInfo("QAA' is populated in 'Contract Type' field", true, tc.combo.getSelected(EComboBox.CONTRACT_TYPE).
                    equalsIgnoreCase("QAA"), new ComparerOptions().takeScreenShotPlatform());

            //Step 21
            tc.combo.select(EComboBox.DOCUMENT_CATEGORY, "EHS");
            tc.button.click(EButton.ADD);
            WaitFor.condition(() -> tc.browser.getPageSubHeaders().contains("EHS"));
            tc.addStepInfo("EHS' is is appearing as newo document entry on the bottom of the list", true,
                    tc.browser.getPageSubHeaders().contains("EHS"), new ComparerOptions().takeScreenShotPlatform());

            //Step 22
            tc.button.click(EButton.byIndex(10));
            WaitFor.condition(() -> !tc.browser.getPageSubHeaders().contains("EHS"));
            tc.addStepInfo("EHS' is removed", true,
                    !tc.browser.getPageSubHeaders().contains("EHS"), new ComparerOptions().takeScreenShotPlatform());

            //Step 23
            tc.combo.select(EComboBox.DOCUMENT_CATEGORY, "Data Protection");
            this.addDocument();
            WaitFor.condition(() -> !tc.browser.getPageSubHeaders().contains("Data Protection"));
            tc.addStepInfo("EHS' is removed", true, !tc.browser.getPageSubHeaders().contains("Data Protection"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 24
            tc.button.click(EButton.byIndex(9));
            WaitFor.condition(tc.modal::exists);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.button.sendKeys(EButton.SELECT_FILES, String.valueOf(DirectoryControl.getPathOfTestFile(attached_file_2)));
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.byIndex(1)).contains(attached_file_2),Duration.ofSeconds(7));
            tc.button.click(EButton.ATTACH);
            WaitFor.condition(() -> !tc.modal.getErrorBody().isEmpty());
            tc.addStepInfo("Warning message appears", true, tc.modal.getErrorBody().contains("Only PDF files are allowed to upload.")
            , new ComparerOptions().takeScreenShotPlatform());

            //Step 25
            tc.button.click(EButton.CLOSE_MODAL);
            WaitFor.condition(() -> !tc.modal.exists(), Duration.ofSeconds(10));
            tc.addStepInfo("Popup closes", true, !tc.modal.getErrorBody().contains("Only PDF files are allowed to upload."), new ComparerOptions().takeScreenShotPlatform());

            //Step 26
            tc.button.click(EButton.byIndex(9));
            WaitFor.condition(tc.modal::exists);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.button.sendKeys(EButton.SELECT_FILES, String.valueOf(DirectoryControl.getPathOfTestFile(attached_file_3)));
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.byIndex(1)).contains(attached_file_3),Duration.ofSeconds(7));
            tc.button.click(EButton.ATTACH);
            WaitFor.condition(() -> !tc.node.getValues(ENode.ATTACHMENTS).isEmpty());
            List<String> attachedFile2 = tc.node.getValues(ENode.ATTACHMENTS);
            tc.addStepInfo("File is accepted", true, attachedFile2.contains("SLIM_TEST")
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 27
            tc.button.click(EButton.SUBMIT);
            String warning = "Please enter a valid URL";
            WaitFor.condition(() -> tc.edit.getEditWarning().contains(warning));
            tc.addStepInfo("Warning message will appear because of invalid SCM STAR Link", true, tc.edit.
                            getEditWarning().contains(warning), new ComparerOptions().takeScreenShotPlatform());

            //Step 28
            tc.edit.sendKeys(EEdit.URL_TO_CLM_MODULE_OF_SCM_STAR, "https://s2c.siemens.com/go/QAA", true);
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.node.getValues(ENode.CASE_DETAILS).contains("CREATING RECORDS"), Duration.ofMinutes(1));
            tc.addStepInfo("To do screen opens. Creating Quality Records is displayed.", true,
                    tc.node.getValues(ENode.CASE_DETAILS).contains("CREATING RECORDS"), new ComparerOptions().takeScreenShotPlatform());

            //Step 29
            tc.browser.waitForStateChange(() -> tc.node.getValues(ENode.CASE_DETAILS).contains("PENDING-AWAITINGRESPONSE"), Duration.ofMinutes(4),
                    Duration.ofSeconds(10));
            tc.menu.selectWithOutExpand(EMenu.SUPPLIER);
            tc.button.click(EButton.SHOW_360_DREGEE_VIEW);
            WaitFor.specificTime(Duration.ofSeconds(4));
            tc.browser.switchToWindow(1);
            WaitFor.condition(()->tc.browser.getTitleOfActiveWindow().contains("Supplier360ViewDetails"), Duration.ofSeconds(10));
            tc.addStepInfo("Supplier 360° View opens", true, tc.browser.getTitleOfActiveWindow().
                    contains("Supplier360ViewDetails"), new ComparerOptions().takeScreenShotPlatform());

            //Step 30
            tc.stepEvaluator.reset();
            tc.node.expandSection(ENode.DOCUMENTS_CONTRACTS_CASES);
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.DOCUMENTS), Duration.ofMinutes(1));
            tc.stepEvaluator.add(() -> tc.tab.isTabPresent(ETab.DOCUMENTS), "Documents tab is not present")
                    .add(() -> tc.tab.isTabPresent(ETab.CONTRACTS), "Contracts tab is not present")
                    .add(() -> tc.tab.isTabPresent(ETab.CASE_HISTORY), "Case History tab is not present");
            tc.addStepInfo("'Documents', 'Contracts' and 'Case History' tabs are shown", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 31
            tc.tab.select(ETab.CONTRACTS);
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(1)));
            List<String> val = tc.table.getItemsFromColumn(ETable.byIndex(1), EColumn.TITLE);
            tc.addStepInfo("Uploaded document is displayed under 'Documents' section and uploaded Contract is displayed under'Contracts' section",
                    true, val.contains("QAA for ALTEN 2023-03-20"));

            //Step 32
            tc.stepEvaluator.reset();
            tc.tab.select(ETab.DOCUMENTS);
            tc.table.filter(ETable.DOCUMENTS,EColumn.byIndex(0),supplierID+"_"+UD+"_Audit Trail");
            WaitFor.condition(()->tc.table.getRowsCount(ETable.DOCUMENTS)==1,Duration.ofSeconds(5));
            WaitFor.specificTime(Duration.ofSeconds(3));
            File pdf = tc.table.downloadFileFromTable(ETable.DOCUMENTS, EColumn.byIndex(6), "#0");
            tc.stepEvaluator
                    .add(() -> tc.table.checkContentInPdf(pdf, supplierID+"_"+UD+"_Audit Trail"), "Ud is not present")
                    .add(() -> tc.table.checkContentInPdf(pdf, "Certificate"), "Certificate type is not present")
                    .add(() -> tc.table.checkContentInPdf(pdf, "Document Title"), "Certificate type is not present")
                    .add(() -> tc.table.checkContentInPdf(pdf, "Uploaded by"), "Uploaded by is not present")
                    .add(() -> tc.table.checkContentInPdf(pdf, "Uploaded on"), "Uploaded on is not present");
            tc.addStepInfo("""
                            Audit trail displays
                             document title (not for Certificate),
                             Certificate type (only for Certificate),
                             file name,
                             uploaded by,
                             and upoaded on.
                             Audit Trail section with user actions is available but no field audit history section""",
                    "ok",tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());



        });
    }

    private void addDocument()
    {
        CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
        WebDriver drv = IocBuilder.getContainer().getComponent(WebDriver.class);
        try
        {
            WebElement ele = css.findControl(By.xpath("//button[text()='Add']"));
            if(ele != null)
            {
                new Actions(drv).scrollToElement(ele).perform();
                ele.click();
                TcLog.info("Added attachment");
            }
        }
        catch (StaleElementReferenceException e)
        {
            TcLog.info("Element staled, Retrying...");
            WaitFor.specificTime(Duration.ofSeconds(2));
            this.addDocument();
        }
        catch (NullPointerException | NotFoundException | ElementClickInterceptedException e)
        {
            TcLog.error("Unable to perform addDocument");
        }
    }
}
