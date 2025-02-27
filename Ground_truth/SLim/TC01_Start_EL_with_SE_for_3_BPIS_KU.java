package Create_SE_List_And_Start;

import CompositionRoot.IocBuilder;
import CompositionRoot.ProjectHandler;
import ControlImplementations.BrowserControl;
import ControlImplementations.ModalControl;
import Create_SE_List_And_Start.DataProvider.SLiMTestCaseId;
import Create_SE_List_And_Start.DataProvider.SLiMTestEnvironmentProvider;
import Enums.*;
import TestUtils.ExcelControl;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.*;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TC01_Start_EL_with_SE_for_3_BPIS_KU
{
    @Test
    void Start_EL_with_SE_for_3_BPIS_KU()
    {
        IocBuilder.execute(Duration.ofMinutes(80), EResultData.SLIM_FS, "44019", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL,ETestData.RISHITH_KU);
            String SU_USER = "RISHITH_SU";
            String attached_file = "SE_template.xlsx";
            String startDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String endDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            ExcelControl.createAndAppendDataToExcel(List.of("last Evaluation Date"),List.of(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yy"))));

            //STEP 1
            BrowserControl.waitForLoadingIndicator();
            tc.menu.select(EMenu.CREATE, EMenu.CREATE_EVALUATION_LIST);
            WaitFor.condition(()->tc.radio.exists(ERadio.STRATEGIC),Duration.ofSeconds(2));
            tc.radio.select(ERadio.STRATEGIC);
            WaitFor.condition(()->tc.button.exists(EButton.UPLOAD_A_FILE),Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->tc.button.exists(EButton.UPLOAD_A_FILE),"The '%s' is not exists".formatted(EButton.UPLOAD_A_FILE))
                    .add(()->tc.radio.isSelected(ERadio.STRATEGIC),"The radio button '%s' is not enabled".formatted(ERadio.STRATEGIC));
            tc.addStepInfo("Fields related to Strategic Evaluation appear", "ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            tc.button.click(EButton.UPLOAD_A_FILE);
            tc.button.sendKeys(EButton.CHOOSE_FILE, String.valueOf(DirectoryControl.getPathOfTestFile(attached_file)));
            tc.button.click(EButton.UPLOAD_FILE);
            WaitFor.condition(()->tc.button.exists(EButton.CREATE_EVALUATION_LIST),Duration.ofSeconds(10));
            WaitFor.specificTime(Duration.ofSeconds(2));
            List<String> businessPartnerNames = tc.table.getItemsFromColumn(ETable.BUSINESS_PARTNERS,EColumn.BUSINESS_PARTNER_NAME);
            tc.addStepInfo("A list of Business Partners open with the supplier prepopulated. System recognizes the Business " +
                    "Partner ID name and SKA Name", true, businessPartnerNames.containsAll(List.of("ZT Group Int'l, Inc.","MOOG INC.","MS Steinbach GmbH & Co. KG")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.table.setTextToEditBox(ETable.BUSINESS_PARTNERS, EColumn.SHS_PVO_LY_EURO, 0, "110");
            WaitFor.condition(()->tc.button.isClickable(EButton.CREATE_EVALUATION_LIST),Duration.ofSeconds(10));
            tc.addStepInfo("Line disappears and Create Evaluation list button is enabled", true,
                    tc.button.isClickable(EButton.CREATE_EVALUATION_LIST)&&tc.table.getItemsFromColumn(ETable.BUSINESS_PARTNERS,EColumn.SHS_PVO_LY_EURO).get(0).equals("110"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.button.click(EButton.CREATE_EVALUATION_LIST);
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.edit.sendKeys(EEdit.LIST_NAME, "SE_Automation Demo");
            tc.edit.sendKeys(EEdit.EVALUATION_PERIOD_FROM, startDate);
            tc.edit.sendKeys(EEdit.EVALUATION_PERIOD_TO, endDate);
            tc.addStepInfo("Values are accepted", true, tc.button.exists(EButton.CREATE), new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.button.click(EButton.CREATE);
            WaitFor.condition(()->!tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(5));
            boolean res = tc.node.waitForCETMembersAssignment(Duration.ofMinutes(60));
            String evaluation_ID = this.getPageTitle();
            ExcelControl.createAndAppendDataToExcel(List.of("EL-ID"),List.of(evaluation_ID));
            tc.addStepInfo("SE-ID gets created after some time and appear in the EL list", true, res, new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            WaitFor.specificTime(Duration.ofSeconds(2));
            String supplier1 = tc.node.fetchCETMemberSEID(0);
            tc.node.selectCETMemberByIndex(0);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.table.exists(ETable.ASSIGN_CET_MEMBERS),Duration.ofSeconds(10));
            WaitFor.condition(()->tc.button.exists(EButton.BREADCRUMB),Duration.ofSeconds(5));
            String currentSupplier1 = this.getPageTitle();
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->supplier1.equals(currentSupplier1),"Task not opened")
                    .add(()->tc.table.exists(ETable.ASSIGN_CET_MEMBERS),"Assign CET Members Table does not exist");
            tc.addStepInfo("The task opens", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.button.selectDropDownItem(EButton.ACTIONS,EDropDown.CANCEL_CASE);
            BrowserControl.waitForLoadingIndicator();
            tc.editor.sendKeys(EEditor.CANCELLATION_COMMENTS,"Tester Cancelled the case",true);
            tc.button.click(EButton.SUBMIT);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->!tc.button.exists(EButton.SUBMIT),Duration.ofSeconds(10));
            //TODO: need to add proper validation
            tc.addStepInfo("Case is cancelled", true, !tc.button.exists(EButton.SUBMIT), new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            tc.nav.navigateToELlist();
            BrowserControl.waitForLoadingIndicator();
            WaitFor.specificTime(Duration.ofSeconds(3));
            WaitFor.condition(() -> tc.table.getItemValueFromColumn(ETable.byIndex(0),EColumn.CASE_ID,supplier1,EColumn.CASE_STATUS).equals("Removed"));
            String status = tc.table.getItemValueFromColumn(ETable.byIndex(0),EColumn.CASE_ID,supplier1,EColumn.CASE_STATUS);
            tc.addStepInfo("Status show as removed", true, status.equals("Removed"), new ComparerOptions().takeScreenShotPlatform());

            //STEP 9
            String supplier2 = tc.node.fetchCETMemberSEID(0);
            String partnerName2 = tc.table.getItemValueFromColumn(ETable.byIndex(0),EColumn.CASE_ID,supplier2,EColumn.BUSINESS_PARTNER_NAME);
            tc.node.selectCETMemberByIndex(0);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.table.exists(ETable.ASSIGN_CET_MEMBERS),Duration.ofSeconds(10));
            WaitFor.condition(()->tc.button.exists(EButton.BREADCRUMB),Duration.ofSeconds(5));
            String currentSupplier2 = this.getPageTitle();
            String supplierId2 = this.getSupplierId();
            ExcelControl.createAndAppendDataToExcel(List.of("Supplier1"),List.of(supplier2));
            ExcelControl.createAndAppendDataToExcel(List.of("SupplierID1"),List.of(supplierId2));
            ExcelControl.createAndAppendDataToExcel(List.of("Bussiness Partner Name1"),List.of(partnerName2));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->supplier2.equals(currentSupplier2),"Task not opened")
                    .add(()->tc.table.exists(ETable.ASSIGN_CET_MEMBERS),"Assign CET Members Table does not exist");
            tc.addStepInfo("The task opens", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 10
            this.assignUser(tc,SU_USER);
            tc.stepEvaluator
                    .add(()->!tc.button.exists(EButton.SUBMIT),"Submit button is still displayed");
            tc.nav.navigateToELlist();
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("Tasks are assigned to members", "ok",tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 11
            String supplier3 = tc.node.fetchCETMemberSEID(0);
            String partnerName3 = tc.table.getItemValueFromColumn(ETable.byIndex(0),EColumn.CASE_ID,supplier3,EColumn.BUSINESS_PARTNER_NAME);
            tc.node.selectCETMemberByIndex(0);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.table.exists(ETable.ASSIGN_CET_MEMBERS),Duration.ofSeconds(10));
            WaitFor.condition(()->tc.button.exists(EButton.BREADCRUMB),Duration.ofSeconds(5));
            String currentSupplier3 = this.getPageTitle();
            String supplierId3 = this.getSupplierId();
            ExcelControl.createAndAppendDataToExcel(List.of("Supplier2"),List.of(supplier3));
            ExcelControl.createAndAppendDataToExcel(List.of("SupplierID2"),List.of(supplierId3));
            ExcelControl.createAndAppendDataToExcel(List.of("Bussiness Partner Name2"),List.of(partnerName3));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->supplier3.equals(currentSupplier3),"Task not opened")
                    .add(()->tc.table.exists(ETable.ASSIGN_CET_MEMBERS),"Assign CET Members Table does not exist");
            tc.addStepInfo("The task opens", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 12
            this.assignUser(tc,SU_USER);
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->!tc.button.exists(EButton.SUBMIT),"Submit button is still displayed");
            BrowserControl.waitForLoadingIndicator();
            tc.addStepInfo("Tasks are assigned to members", "ok",tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());
        });
    }
    private void assignUser(ProjectHandler tc, String SU_USER)
    {
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.table.setTextToEditBox(ETable.byIndex(0),EColumn.USERNAME,1,SU_USER);
            WaitFor.condition(()->tc.table.exists(ETable.USERNAME_ASSIGNEE),Duration.ofSeconds(5));
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.table.clickOnRow(ETable.USERNAME_ASSIGNEE,0);
            WaitFor.condition(()->!tc.table.exists(ETable.USERNAME_ASSIGNEE),Duration.ofSeconds(5));
            tc.table.setTextToEditBox(ETable.byIndex(0),EColumn.USERNAME,2,SU_USER);
            WaitFor.condition(()->tc.table.exists(ETable.USERNAME_ASSIGNEE),Duration.ofSeconds(5));
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.table.clickOnRow(ETable.USERNAME_ASSIGNEE,0);
            WaitFor.condition(()->!tc.table.exists(ETable.USERNAME_ASSIGNEE),Duration.ofSeconds(5));
            tc.button.click(EButton.SUBMIT);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->!tc.button.exists(EButton.SUBMIT),Duration.ofSeconds(10));
    }

    private String getPageTitle()
    {
        CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
        try
        {
            WebElement root = DomUtils.setRootElement(ModalControl.getSelector());
            WebElement element = css.findControlWithRoot(By.cssSelector("span[class='case_title']"),root);
            return Objects.requireNonNull(element).getText();
        }
        catch (Exception e)
        {
            TcLog.error("Could not able to retrieve the breadcrumb title");
        }
        return "";
    }
    private String getSupplierId() {
        CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
        try {
            WebElement root = css.findControl(By.cssSelector("div[class='content-item content-field item-1 flex']"));
            WebElement element = css.findControlWithRoot(By.tagName("div"), root);
            return Objects.requireNonNull(element).getText();
        } catch (Exception e) {
            TcLog.error("Could not able to retrieve the Supplier ID");
        }
        return "";
    }
}
