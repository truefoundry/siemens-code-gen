package Edit_Manual_Supplier;

import CompositionRoot.IocBuilder;
import CompositionRoot.ProjectHandler;
import ControlImplementations.BrowserControl;
import Enums.*;
import TestUtils.ExcelControl;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.DirectoryControl;
import fate.core.ControlImplementations.Generator;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.text.Collator;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TC01_Edit_MS_Create_SQ
{
    @Test
    void TC_01_Edit_MS_Create_SQ()
    {
        IocBuilder.execute(Duration.ofMinutes(15), EResultData.SLIM_EDIT_MANUAL_SUPPLIER_FS, "44042", tc ->
        {
            String KU_User = "rishith koppula.";
            String BA_User = "Rishith koppula";
            String supplierID = "SHS0136";
            String supplier_name = "SHS 2002";
            ExcelControl.createAndAppendDataToExcel(List.of("SupplierID"), List.of(supplierID));
            ExcelControl.createAndAppendDataToExcel(List.of("Supplier Name"), List.of(supplier_name));
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);

            //STEP 1
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(20));
            tc.menu.select(EMenu.MANUAL_SUPPLIER_LIST);
            WaitFor.condition(() -> tc.button.exists(EButton.CREATE_SUPPLIER));
            tc.addStepInfo("The Manual supplier screen opens",true,
                    tc.table.getColumnNames(ETable.byIndex(0)).containsAll(List.of("SHSID","Name","Street","City","Postal Code","Country","Edit")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            tc.table.filter(ETable.byIndex(0), EColumn.SHSID, supplierID);
            WaitFor.condition(() -> tc.table.getRowsCount(ETable.byIndex(0))==1,Duration.ofSeconds(5));
            tc.table.buttonClick(ETable.byIndex(0), EColumn.EDIT,0, EButton.byIndex(0));
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS),Duration.ofSeconds(10));
            tc.addStepInfo("The Choose QMS screen opens",true, tc.combo.exists(EComboBox.SELECT_A_QMS),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            String M_ID1 = tc.node.getValues(ENode.CASE_HEADER).get(0);
            ExcelControl.createAndAppendDataToExcel(List.of("M_ID1"), List.of(M_ID1));
            tc.combo.select(EComboBox.SELECT_A_QMS,"LD_POC");
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(()->tc.edit.exists(EEdit.STREET),Duration.ofSeconds(5));
            tc.addStepInfo("The Define Manual Supplier screen opens",true, tc.edit.exists(EEdit.STREET),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            String street = tc.edit.getText(EEdit.STREET);
            String city = tc.edit.getText(EEdit.CITY);
            String country = tc.combo.getSelected(EComboBox.COUNTRY);
            String region = tc.combo.getSelected(EComboBox.REGION);
            tc.edit.sendKeys(EEdit.STREET,"MG Street",true);
            tc.edit.sendKeys(EEdit.CITY,"Bangalore",true);
            List<String> countryItems = tc.combo.getAvailableOptions(EComboBox.COUNTRY).stream().map(entry -> entry.split("\n")[1]).toList();
            tc.combo.select(EComboBox.COUNTRY, "IE");
            // Saving the changes and then checking the combo value because in DOM value is getting updated only after saving.
            tc.button.click(EButton.SAVE);
            WaitFor.specificTime(Duration.ofSeconds(3));//TODO: Temporary fix
            BrowserControl.waitForLoadingIndicator();
            List<String> regionItems = tc.combo.getAvailableOptions(EComboBox.REGION).stream().map(entry -> entry.split("\n")[1]).toList();
            tc.stepEvaluator.add(() -> tc.combo.getSelected(EComboBox.COUNTRY).equals("IE"), "Incorrect data in country field.")
                            .add(() -> countryItems.stream().sorted(Collator.getInstance(Locale.ENGLISH)).toList().equals(countryItems), "Entries for country are not in alphabetical order.")
                            .add(() -> tc.combo.isAutoComplete(EComboBox.COUNTRY), "Country is not autocomplete field.");
            tc.addStepInfo("Country field display only ISO code with 2 digits and region will show only Regions related to the country chosen. " +
                            "Entries are in alphabetical order and country is autocomplete field", "ok", tc.stepEvaluator.eval(),
                             new ComparerOptions().takeScreenShotPlatform());

            ExcelControl.createAndAppendDataToExcel(List.of("Street", "City", "Country", "Region"), List.of(street, city, country, region));

            //STEP 5
            tc.combo.select(EComboBox.REGION, "CW");
            tc.button.click(EButton.SAVE);
            WaitFor.specificTime(Duration.ofSeconds(3));//TODO: Temporary fix
            BrowserControl.waitForLoadingIndicator();
            String selectedRegion = tc.combo.getSelected(EComboBox.REGION);
            String selectedStreet = tc.edit.getText(EEdit.STREET);
            String selectedCity = tc.edit.getText(EEdit.CITY);
            String selectedCountry = tc.combo.getSelected(EComboBox.COUNTRY);
            tc.edit.sendKeys(EEdit.INTERNAL_VENDOR_DESCRIPTION,"Vendor Description for LD_POC",true);
            tc.button.click(EButton.SUBMIT);
            BrowserControl.waitForLoadingIndicatorToAppear(Duration.ofSeconds(2));
            BrowserControl.waitForLoadingIndicator(Duration.ofMinutes(2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> selectedRegion.equals("CW"), "Incorrect data in region field.")
                    .add(() -> regionItems.stream().sorted().toList().equals(regionItems), "Entries for region are not in alphabetical order.");
            tc.addStepInfo("Region display is ISO code with 2 digits, entries are in alphabetical order and " +
                            "country is autocomplete field. Changed values are being shown in the fileds.", "ok",
                            tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            ExcelControl.createAndAppendDataToExcel(List.of("SelectedStreet", "SelectedCity", "SelectedCountry", "SelectedRegion"),
                    List.of(selectedStreet, selectedCity, selectedCountry, selectedRegion));

            //STEP 6
            tc.menu.select(EMenu.CREATE,EMenu.SUPPLIER_QUALIFICATION);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()-> tc.combo.exists(EComboBox.SELECT_A_QMS), Duration.ofSeconds(5));
            tc.addStepInfo("A page with a QMS dropdown opens", true, tc.combo.exists(EComboBox.SELECT_A_QMS),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            String SQ_ID = tc.node.getValues(ENode.CASE_HEADER).get(0);
            ExcelControl.createAndAppendDataToExcel(List.of("SQ_ID"), List.of(SQ_ID));
            tc.combo.select(EComboBox.SELECT_A_QMS,"Czech Republic");
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.edit.exists(EEdit.SUPPLIER_ID));
            tc.addStepInfo("The 'Search for Supplier' screen pops up", true,
                    tc.edit.exists(EEdit.SUPPLIER_ID) && tc.button.exists(EButton.SEARCH_FOR_SUPPLIERS),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            tc.edit.sendKeys(EEdit.SUPPLIER_ID,supplierID,true);
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()-> tc.node.getParagraph(ENode.SEARCH_FOR_SUPPLIER)
                    .contains("Supplier search is now complete and has returned 1 results"), Duration.ofSeconds(10));
            tc.addStepInfo("In the 'Select a Supplier List' this supplier is listed", true,
                    tc.edit.exists(EEdit.SUPPLIER_ID) && tc.button.exists(EButton.SEARCH_FOR_SUPPLIERS),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 9
            WaitFor.condition(() -> tc.table.isCheckboxEnabled(ETable.byIndex(0), EColumn.SELECT, 0));
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.table.selectCheckBox(ETable.byIndex(0), EColumn.SELECT, 0);
            tc.button.click(EButton.CONTINUE);
            this.checkForLockedSupplier(tc, SQ_ID, supplierID);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.radio.exists(ERadio.YES));
            tc.addStepInfo("The 'Apply Scope' screen pops up", true, tc.browser.getPageHeaders().contains("Apply Scope"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 10
            tc.radio.select(ERadio.YES);
            tc.edit.sendKeys(EEdit.INTERNAL_VENDOR_DESCRIPTION, "New vendor for czech", true);
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.radio.isSelected(ERadio.YES), "Yes is not selected for 'Quality Relevent'.")
                    .add(() -> tc.edit.getText(EEdit.INTERNAL_VENDOR_DESCRIPTION).equals("New vendor for czech"),
                            "Internal vendor description is not editable.");
            tc.addStepInfo("Quality Relevant 'Yes' is selected and the internal vendor description is editable",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 11
            tc.button.click(EButton.CONTINUE);
            WaitFor.condition(() -> tc.browser.getPageHeaders().contains("Assign CFT Members"));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.browser.getPageHeaders().contains("Assign CFT Members"), "Assign CFT Members screen is not opened.")
                    .add(() -> tc.table.getSelectedFromCombo(ETable.byIndex(0), EColumn.FUNCTIONAL_AREA, 0).equals("Procurement"),
                            "Procurement is not prepopulated in the table.")
                    .add(() -> tc.table.getSelectedFromCombo(ETable.byIndex(0), EColumn.FUNCTIONAL_AREA, 1).equals("Quality"),
                            "Quality is not prepopulated in the table.");
            tc.addStepInfo("The Assign CFT Members screen pops up and the Functional Areas PROCUREMENT and QUALITY are prepopulated",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 12
            String selected_BA_user = tc.table.selectFromCombo(ETable.byIndex(0),EColumn.USERNAME,0, BA_User);
            String selected_KU_user = tc.table.selectFromCombo(ETable.byIndex(0),EColumn.USERNAME,1, KU_User);
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> selected_BA_user.contains("Rishith koppula"), "Expecting BA user '%s' but selected '%s'.".formatted(BA_User, selected_BA_user))
                    .add(() -> selected_KU_user.contains("rishith koppula."), "Expecting KU user '%s' but selected '%s'.".formatted(KU_User, selected_KU_user));
            tc.addStepInfo("The selected Users are available in the USER NAME field", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 13
            tc.stepEvaluator.reset();
            tc.button.click(EButton.CONTINUE);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()-> !tc.button.exists(EButton.CONTINUE));
            WaitFor.condition(()-> tc.button.exists(EButton.GO));
            tc.addStepInfo("The Review and adjust tasks screen pops up in the ToDo0 List.", true,
                    tc.node.getAllTitlesFromTODO(ENode.ASSIGNMENT_TODO).contains("Review and adjust tasks"), new ComparerOptions().takeScreenShotPlatform());

            //Step 14
            tc.button.click(EButton.GO);
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("A list with NO selected tasks will appear", true,
                    tc.table.getRowsCount(ETable.byIndex(0)) > 0, new ComparerOptions().takeScreenShotPlatform());

            //Step 15
            String dueDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            tc.table.itemDoubleClick(ETable.byIndex(0), EColumn.TASK_SUMMARY,1);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.TASK_OWNER));
            tc.combo.select(EComboBox.TASK_OWNER, "rishith koppula.");
            tc.calendar.selectDate(ECalendar.TASK_DUE_DATE, 1);
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> !tc.modal.exists(EModal.MODAL_WRAPPER));
            String date = LocalDate.parse(dueDate, DateTimeFormatter.ofPattern("dd.MM.yyyy")).format(DateTimeFormatter.ofPattern("dd.MM.yy"));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> !tc.modal.exists(EModal.MODAL_WRAPPER), "Add Task window is not closed.")
                    .add(() -> tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.TASK_SUMMARY, "Basic supplier information", EColumn.OWNER).equals("rishith koppula."),
                            "Expected owner is not found in the table.")
                    . add(() -> tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.TASK_SUMMARY, "Basic supplier information",
                                    EColumn.TASK_DUE_DATE).equals(date),
                            "Expected Task Due Date is not found in the table.");
            tc.addStepInfo("The Window has closed and the selected owner is listed in the owner column and the due date is filled.",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 16
            tc.stepEvaluator.reset();
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.node.getAllTitlesFromTODO(ENode.ASSIGNMENT_TODO).contains("Execute Task"));
            tc.node.performActionOnTODO(ENode.ASSIGNMENT_TODO, "Execute Task", EActions.GO);
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("The Task Basic supplier information has opened",
                    true, tc.browser.getTitleOfActiveWindow().contains(SQ_ID), new ComparerOptions().takeScreenShotPlatform());

            //Step 17
            WaitFor.condition(() -> tc.button.exists(EButton.ADD_ATTACHMENT));
            tc.button.click(EButton.ADD_ATTACHMENT);
            WaitFor.condition(() -> tc.button.exists(EButton.UPLOAD_FILE));
            tc.button.click(EButton.UPLOAD_FILE);
            WaitFor.condition(() -> tc.modal.exists(EModal.MODAL_WRAPPER));
            tc.button.sendKeys(EButton.SELECT_FILES, String.valueOf(DirectoryControl.getPathOfTestFile("SLIM_TEST.pdf")));
            tc.button.click(EButton.ATTACH);
            WaitFor.condition(() -> !tc.modal.exists(EModal.MODAL_WRAPPER));
            tc.check.check(ECheckBox.CONFIRM_COMPLETED_TASK);
            tc.editor.sendKeys(EEditor.NOTE, "Test");
            tc.stepEvaluator.add(() -> tc.check.isChecked(ECheckBox.CONFIRM_COMPLETED_TASK), "Checkbox is not marked.")
                    .add(() -> tc.button.exists(EButton.REMOVE_DOCUMENT), "Attached File is not appearing.");
            tc.addStepInfo("The checkbox is marked and the attached file appears in the task.",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 18
            tc.stepEvaluator.reset();
            tc.button.click(EButton.SUBMIT);
            tc.browser.waitForStateChange(() -> tc.table.isValid(ETable.byIndex(0), EColumn.TASK_SUMMARY, "Basic supplier information", EColumn.IS_COMPLETED),
                    Duration.ofMinutes(5), Duration.ofSeconds(4));
            tc.addStepInfo("The basic information task is marked as completed", true,
                    tc.table.isValid(ETable.byIndex(0), EColumn.TASK_SUMMARY, "Basic supplier information", EColumn.IS_COMPLETED),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 19
            tc.node.performActionOnTODO(ENode.ASSIGNMENT_TODO, "Set Supplier Status", EActions.GO);
            WaitFor.condition(() -> !tc.browser.getPageHeaders().contains("Supplier Status"));
            tc.addStepInfo("The Supplier Status screen opens.", true,
                    tc.browser.getPageHeaders().contains("Supplier Status"), new ComparerOptions().takeScreenShotPlatform());

            //Step 20
            tc.combo.select(EComboBox.SUPPLIER_STATUS, "Released");
            tc.stepEvaluator.add(() -> tc.combo.getSelected(EComboBox.SUPPLIER_STATUS).equals("Released"),
                    "Supplier Status is not set to Released.")
                            .add(() -> tc.radio.isSelected(ERadio.NO), "Yes is selected for 'Skip CFT Approval'.");
            tc.addStepInfo("Supplier Status is Released", "ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 21
            tc.stepEvaluator.reset();
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> !tc.node.getValues(ENode.CASE_DETAILS).isEmpty());
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfo("The Case Status changes to Pending-Approval", true,
                    tc.node.getValues(ENode.CASE_DETAILS).contains("PENDING-APPROVAL"), new ComparerOptions().takeScreenShotPlatform());

            //Step 22
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.browser.getPageHeaders().contains("Approval"));
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfo("The Approval screen opens", true, tc.browser.getPageHeaders().contains("Approval"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 23
            tc.button.click(EButton.APPROVE);
            WaitFor.condition(() -> !tc.button.exists(EButton.GO));
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfo("The approval task is no longer in the ToDo list", true,
                    !tc.button.exists(EButton.GO), new ComparerOptions().takeScreenShotPlatform());

            //Step 24
            tc.menu.select(EMenu.CREATE, EMenu.UPLOAD_DOCUMENTS);
            WaitFor.condition(() -> tc.browser.getPageHeaders().contains("Choose QMS"));
            tc.combo.select(EComboBox.SELECT_A_QMS, "Czech Republic");
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.button.exists(EButton.SEARCH_FOR_SUPPLIERS));
            tc.edit.sendKeys(EButton.SUPPLIER_ID, supplierID);
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            WaitFor.condition(()-> tc.node.getParagraph(ENode.SEARCH_FOR_SUPPLIER)
                    .contains("Supplier search is now complete and has returned 1 results"), Duration.ofSeconds(10));
            tc.table.selectCheckBox(ETable.byIndex(0), EColumn.SELECT, 0);
            tc.button.click(EButton.CONTINUE);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.button.exists(EButton.SUBMIT));
            String UD_ID = tc.node.getValues(ENode.CASE_HEADER).get(0);
            ExcelControl.createAndAppendDataToExcel(List.of("UD_ID"), List.of(UD_ID));
            WaitFor.condition(() -> tc.browser.getPageHeaders().contains("Upload Documents"));
            tc.addStepInfo("The UD-ID is available and case was started containing the data available in SLiM Database",
                    true, tc.browser.getPageHeaders().contains("Upload Documents"), new ComparerOptions().takeScreenShotPlatform());

            //Step 25
            tc.menu.select(EMenu.CREATE, EMenu.VALIDITY_DOCUMENTS);
            WaitFor.condition(() -> tc.browser.getPageHeaders().contains("Choose QMS"));
            tc.combo.select(EComboBox.SELECT_A_QMS, "Czech Republic");
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.button.exists(EButton.SEARCH_FOR_SUPPLIERS));
            tc.edit.sendKeys(EButton.SUPPLIER_ID, supplierID);
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            WaitFor.condition(()-> tc.node.getParagraph(ENode.SEARCH_FOR_SUPPLIER)
                    .contains("Supplier search is now complete and has returned 1 results"), Duration.ofSeconds(10));
            tc.table.selectCheckBox(ETable.byIndex(0), EColumn.SELECT, 0);
            tc.button.click(EButton.CONTINUE);
            BrowserControl.waitForLoadingIndicator();
            String VD_ID = tc.node.getValues(ENode.CASE_HEADER).get(0);
            ExcelControl.createAndAppendDataToExcel(List.of("VD_ID"), List.of(VD_ID));
            WaitFor.condition(() -> tc.browser.getPageHeaders().contains("Validity Documents"));
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.addStepInfo("The VD-ID is available and case was started containing the data available in SLiM Database", true,
                    tc.browser.getPageHeaders().contains("Validity Documents") && tc.table.exists(ETable.DOCUMENTS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 26
            String supplierName = Generator.getHashedName("SHS ", true);
            tc.menu.select(EMenu.MANUAL_SUPPLIER_LIST);
            WaitFor.condition(() -> tc.button.exists(EButton.CREATE_SUPPLIER));
            tc.button.click(EButton.CREATE_SUPPLIER);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.combo.select(EComboBox.SELECT_A_QMS, "CT");
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.BUSINESS_ORGANIZATION));
            tc.combo.select(EComboBox.BUSINESS_ORGANIZATION, "IT");
            tc.edit.sendKeys(EEdit.DEPARTMENT_SUPPLIER_NAME, supplierName);
            tc.edit.sendKeys(EEdit.STREET, "SHS Street");
            tc.edit.sendKeys(EEdit.CITY, "Bangalore");
            tc.combo.select(EComboBox.COUNTRY, "IN");
            tc.combo.select(EComboBox.REGION, "KA");
            tc.button.click(EButton.SUBMIT);
            WaitFor.specificTime(Duration.ofSeconds(6));//TODO: Temporary fix
            BrowserControl.waitForLoadingIndicator(Duration.ofMinutes(2));
            WaitFor.condition(tc.alert::exists, Duration.ofMinutes(2));
            String M_ID2 = tc.node.getValues(ENode.CASE_HEADER).get(0);
            ExcelControl.createAndAppendDataToExcel(List.of("M_ID2"), List.of(M_ID2));
            tc.addStepInfo("Case is waiting for BA approval, BA receives an email regarding the case", true,
                    tc.browser.getPageHeaders().contains("Get Approval"), new ComparerOptions().takeScreenShotPlatform());
        });
    }

    private void checkForLockedSupplier(ProjectHandler tc, String sqCase, String supplierID)
    {
        WaitFor.condition(() -> tc.alert.getAlertMessage().contains("Supplier Information"));
        String alertText = tc.alert.getAlertMessage();

        if (alertText.contains("Supplier Information"))
        {
            Matcher matcher = Pattern.compile("SQ-\\d+").matcher(alertText);
            if (matcher.find())
            {
                String sq = matcher.group();
                tc.menu.search(sq);
                tc.button.click(EButton.GO);
                WaitFor.condition(() -> tc.button.exists(EButton.ACTIONS));
                tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.CANCEL_CASE);
                WaitFor.condition(() -> tc.editor.exists(EEditor.CANCELLATION_COMMENTS));
                tc.editor.sendKeys(EEditor.CANCELLATION_COMMENTS, "Deleting");
                tc.button.click(EButton.SUBMIT);
                WaitFor.specificTime(Duration.ofSeconds(2));
                tc.menu.search(sqCase);
                tc.button.click(EButton.GO);
                WaitFor.condition(() -> tc.edit.exists(EEdit.SUPPLIER_ID));
                tc.edit.sendKeys(EEdit.SUPPLIER_ID,supplierID,true);
                tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
                WaitFor.condition(()-> tc.node.getParagraph(ENode.SEARCH_FOR_SUPPLIER)
                        .contains("Supplier search is now complete and has returned 1 results"), Duration.ofSeconds(10));
                tc.table.selectCheckBox(ETable.byIndex(0), EColumn.SELECT, 0);
                tc.button.click(EButton.CONTINUE);
            }
            else
            {
                TcLog.info("No SQ code found in the alert text.");
            }

            // Check for the alert again and recursively call the function if necessary
            WaitFor.specificTime(Duration.ofSeconds(1)); // Small delay before checking again
            if (tc.alert.getAlertMessage().contains("Supplier Information"))
            {
                checkForLockedSupplier(tc, sqCase, supplierID);
            }
        }
        else
        {
            TcLog.info("No Supplier Qualification case is in open status");
        }
    }

}
