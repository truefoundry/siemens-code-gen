package Edit_Manual_Supplier;

import CompositionRoot.IocBuilder;
import CompositionRoot.ProjectHandler;
import ControlImplementations.BrowserControl;
import Enums.*;
import TestUtils.ExcelControl;
import fate.core.ControlImplementations.DirectoryControl;
import fate.core.ControlImplementations.Generator;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class TC03_Check_Supplier_Info_KU
{
    @Test
    void TC03_Check_Supplier_Info_KU()
    {
        IocBuilder.execute(Duration.ofMinutes(35), EResultData.SLIM_EDIT_MANUAL_SUPPLIER_FS, "44046", tc ->
        {
            if (!ExcelControl.isDataAvailable(List.of("M_ID1", "SupplierID", "UD_ID", "VD_ID", "Street", "City", "SelectedStreet",
                    "SelectedCity")))
            {
                tc.addStepInfo("Failed due to data absence in Excel Sheet", true, false,
                        new ComparerOptions().abort());
            }

            String attached_file = "SLIM_TEST.pdf";
            final String manual_supplier = ExcelControl.getValueUnderHeader("M_ID1");
            final String supplierId = ExcelControl.getValueUnderHeader("SupplierID");
            final String uploadDocument = ExcelControl.getValueUnderHeader("UD_ID");
            final String validateDocument = ExcelControl.getValueUnderHeader("VD_ID");
            final String oldStreet = ExcelControl.getValueUnderHeader("Street");
            final String oldCity = ExcelControl.getValueUnderHeader("City");
            final String newStreet = ExcelControl.getValueUnderHeader("SelectedStreet");
            final String newCity = ExcelControl.getValueUnderHeader("SelectedCity");


            //Pre-condition
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(20));

//          Step 1
            tc.menu.search(manual_supplier);
            WaitFor.condition(() -> !tc.node.getValues(ENode.CASE_INFORMATION).isEmpty());
            tc.stepEvaluator
                    .add(() -> tc.node.getValues(ENode.CASE_INFORMATION).contains(newCity), "Invalid Street Name in Manual Supplier")
                    .add(() -> tc.node.getValues(ENode.CASE_INFORMATION).contains(newStreet), "Invalid City Name in Manual Supplier");
            tc.addStepInfo("The case shows now the updated information and the internal vendor description given " +
                    "during the qualification of the second QMS appears in the supplier Tab inside the case", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            tc.stepEvaluator.reset();
            tc.menu.search(uploadDocument);
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.menu.exists(EMenu.SUPPLIER));
            tc.menu.selectWithOutExpand(EMenu.SUPPLIER);
            WaitFor.condition(()->tc.combo.exists(EComboBox.DOCUMENT_CATEGORY));
            tc.combo.select(EComboBox.DOCUMENT_CATEGORY, "Basic Information");
            tc.button.click(EButton.ADD);
            WaitFor.condition(() -> tc.button.exists(EButton.UPLOAD_FILE));
            tc.button.click(EButton.UPLOAD_FILE);
            WaitFor.condition((tc.modal::exists));
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.button.sendKeys(EButton.SELECT_FILES, String.valueOf(DirectoryControl.getPathOfTestFile(attached_file)));
            tc.button.click(EButton.ATTACH);
            WaitFor.condition((() -> !tc.modal.exists()));
            tc.button.click(EButton.SUBMIT);
            tc.stepEvaluator.add(tc.modal::exists, "Message is not displayed for upload document");
            tc.addStepInfo("""
                    A message Appears that
                    Supplier data has been updated by the following case(s):
                    <list of cases>
                    listing M case performed, since SQ was done for another QMS.""", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            tc.stepEvaluator.reset();
            if(tc.modal.exists()) tc.button.click(EButton.SUBMIT);
            WaitFor.specificTime(Duration.ofMillis(3000));
            tc.stepEvaluator
                    .add(() -> tc.node.getValues(ENode.SUPPLIER_OVERVIEW).contains(newStreet), "Invalid Street Name in upload documents")
                    .add(() -> tc.node.getValues(ENode.SUPPLIER_OVERVIEW).contains(newCity), "Invalid City Name in upload documents");
            tc.addStepInfo("Information provided in the Manual case is displayed since SQ case was performed for" +
                    " another QMS. So no changes on Commodities for instance", "ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.browser.waitForStateChange(() -> tc.node.getValues(ENode.CASE_DETAILS).contains("RESOLVED-COMPLETED"),
                    Duration.ofMinutes(15), Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            String validFrom = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String validTo = LocalDate.now().plusDays(1).format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            tc.menu.search(validateDocument);
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.menu.exists(EMenu.SUPPLIER));
            tc.menu.selectWithOutExpand(EMenu.SUPPLIER);
            WaitFor.condition(() -> tc.button.exists(EButton.SUBMIT));
            tc.table.selectCheckBox(ETable.DOCUMENTS, EColumn.SELECT, 0);
            tc.table.setTextToEditBox(ETable.DOCUMENTS, EColumn.VALID_FROM, 0, validFrom);
            tc.table.setTextToEditBox(ETable.DOCUMENTS, EColumn.VALID_TO, 0, validTo);
            tc.table.selectCheckBox(ETable.DOCUMENTS, EColumn.VALID, 0);
            tc.editor.sendKeys(EEditor.COMMENTS, Generator.getHashedName("Comment_"));
            tc.button.click(EButton.SUBMIT);
            tc.stepEvaluator
                    .add(tc.modal::exists, "Message is not displayed for Validity Document");
            tc.addStepInfo("A message Appears that \"Supplier data has been updated by the following case(s):\n" +
                    "<list of cases>\" listing M case performed, since SQ was done for another QMS.", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            tc.stepEvaluator.reset();
            if (tc.modal.exists()) tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.node.getValues(ENode.SUPPLIER_OVERVIEW).contains(newStreet));
            tc.stepEvaluator
                    .add(() -> tc.node.getValues(ENode.SUPPLIER_OVERVIEW).contains(newStreet), "Invalid Street Name in validity documents")
                    .add(() -> tc.node.getValues(ENode.SUPPLIER_OVERVIEW).contains(newCity), "Invalid City Name in validity documents");
            tc.addStepInfo("Information provided in the Manual case is displayed since SQ case was performed for " +
                    "another QMS. So no changes on Commodities for instance, but change on Internal vendor description and" +
                    " address information\n", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            tc.browser.waitForStateChange(() -> tc.node.getValues(ENode.CASE_DETAILS).contains("RESOLVED-COMPLETED"),
                    Duration.ofMinutes(15), Duration.ofSeconds(10));
            tc.menu.expand();
            tc.menu.select(EMenu.FIND_MY_SUPPLIER);
            WaitFor.condition(() -> tc.browser.getTitleOfActiveWindow().equalsIgnoreCase("Find My Supplier"));
            tc.addStepInfo("The Find my supplier screen opens", true, tc.browser.getTitleOfActiveWindow()
                    .equalsIgnoreCase("Find My Supplier"), new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            tc.combo.select(EComboBox.SELECT_A_QMS, "Czech Republic");
            tc.edit.sendKeys(EEdit.SUPPLIER_ID, supplierId);
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            WaitFor.specificTime(Duration.ofSeconds(3)); //TODO: need to remove later
            WaitFor.condition(() -> !tc.table.getValuesFromRow(ETable.SUPPLIER_DETAILS, 0).isEmpty());
            tc.addStepInfo("Supplier is displayed in the botton section 'Supplier details", true,
                    tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS, EColumn.NAME).contains("SHS 2002"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 8
            tc.stepEvaluator.reset();
            tc.table.doubleClickOnRow(ETable.SUPPLIER_DETAILS, 0);
            WaitFor.condition(()->tc.browser.getTitlesOfExistingWindows().contains("Supplier360ViewDetails"),Duration.ofSeconds(10));
            tc.browser.switchToWindow(1);
            tc.browser.maximize();
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.node.expandSection(ENode.STRATEGIC_EVALUATION_RESULTS);
            tc.stepEvaluator
                    .add(() -> tc.browser.getTitlesOfExistingWindows().contains("Supplier360ViewDetails"), "360 is not opened")
                    .add(() -> tc.node.getValues(ENode.ADDRESS_INFORMATION).contains(newStreet), "Invalid Street Name in 360")
                    .add(() -> tc.node.getValues(ENode.ADDRESS_INFORMATION).contains(newCity), "Invalid City Name in 360");
            tc.addStepInfo("The supplier 360° View opens in a new window.\n" +
                    "If more than one QMS qualified the supplier a second tab for the QMS will be visible.", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 9
            tc.stepEvaluator.reset();
            List<String> functionalAreaValues= tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.FUNCTIONAL_AREA);
            List<String> usernames = tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.USERNAME);
            List<String> qmsLocation = tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.QMS_LOCATION);
            tc.stepEvaluator
                    .add(() -> usernames.stream().anyMatch(x -> x.contains("BA")), "Username error for Procurement")
                    .add(() -> usernames.stream().anyMatch(x -> x.contains("KU")), "Username error for Quality")
                    .add(() -> functionalAreaValues.stream().anyMatch(x -> x.contains("Procurement")), "Different Functional Area 1")
                    .add(() -> functionalAreaValues.stream().anyMatch(x -> x.contains("Quality")), "Different Functional Area 2")
                    .add(() -> qmsLocation.stream().anyMatch(x -> x.contains("Czech Republic")), "different QMS Location");

            tc.addStepInfo("The given information matches with the case and the supplier status as well", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 10
            tc.stepEvaluator.reset();
            tc.node.expandSection(ENode.DOCUMENTS_CONTRACTS_CASES);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.DOCUMENTS),Duration.ofSeconds(5));
            tc.tab.select(ETab.DOCUMENTS);
            WaitFor.condition(()->tc.table.exists(ETable.DOCUMENTS),Duration.ofSeconds(10));
            tc.table.filter(ETable.DOCUMENTS,EColumn.byIndex(0),supplierId+"_"+manual_supplier+"_Audit Trail");
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.table.getRowsCount(ETable.DOCUMENTS)==1,Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator.add(()->(!tc.table.getItemsFromColumn(ETable.DOCUMENTS, EColumn.byIndex(0)).isEmpty()),"There are no related Audit trails in the table");
            File pdf1 = tc.table.downloadFileFromTable(ETable.DOCUMENTS, EColumn.byIndex(6), "#0");
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdf1, newStreet), "Incorrect new address 1");
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdf1, newCity), "Incorrect new address 2");
            tc.addStepInfo("The audit trail should contain the new address information and the internal vendor description." +
                            "Check the field audit history for the changes, when they were done and by whom. All information" +
                            " should match what was done in the case","ok",tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 11
            tc.stepEvaluator.reset();
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdf1, oldStreet), "Incorrect old address 1");
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdf1, oldCity), "Incorrect old address 2");
            tc.addStepInfo("The audit trail should contain still the old address information and the internal" +
                    " vendor description added.", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 12
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.CASE_HISTORY),Duration.ofSeconds(5));
            tc.table.filter(ETable.DOCUMENTS,EColumn.byIndex(0),supplierId+"_"+uploadDocument+"_Audit Trail");
            WaitFor.condition(()->tc.table.getRowsCount(ETable.DOCUMENTS)==1,Duration.ofSeconds(5));
            WaitFor.specificTime(Duration.ofSeconds(3)); //TODO: need to remove later
            tc.stepEvaluator.reset();
            File pdf2 = tc.table.downloadFileFromTable(ETable.DOCUMENTS, EColumn.byIndex(6), "#0");
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdf2, newCity), "Incorrect new address for UD");
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdf2, newStreet), "Incorrect new address for UD");
            tc.addStepInfo("The audit trail should contain the new address information.", "ok",tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 13
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.CASE_HISTORY),Duration.ofSeconds(5));
            tc.table.filter(ETable.DOCUMENTS,EColumn.byIndex(0),supplierId+"_"+validateDocument+"_Audit Trail");
            WaitFor.condition(()->tc.table.getRowsCount(ETable.DOCUMENTS)==1,Duration.ofSeconds(5));
            WaitFor.specificTime(Duration.ofSeconds(3)); //TODO: need to remove later
            tc.stepEvaluator.reset();
            File pdf3 = tc.table.downloadFileFromTable(ETable.DOCUMENTS, EColumn.byIndex(6), "#0");
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdf3, newStreet), "Incorrect new address for VD");
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdf3, newCity), "Incorrect new address for VD");
            tc.addStepInfo("The audit trail should contain the new address information.", "ok",tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

        });
    }
}
