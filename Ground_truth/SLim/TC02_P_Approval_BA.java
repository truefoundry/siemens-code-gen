package Edit_Manual_Supplier;

import CompositionRoot.IocBuilder;
import CompositionRoot.ProjectHandler;
import Enums.*;
import TestUtils.ExcelControl;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC02_P_Approval_BA
{
    @Test
    void P_Approval_BA()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.SLIM_EDIT_MANUAL_SUPPLIER_FS, "44045", tc ->
        {

            if (!ExcelControl.isDataAvailable(List.of("SQ_ID", "M_ID1", "M_ID2", "Supplier Name", "SupplierID")))
            {
                tc.addStepInfo("Failed due to data absence in Excel Sheet", true, false,
                        new ComparerOptions().abort());
            }

            final String sq_case = ExcelControl.getValueUnderHeader("SQ_ID");
            final String manual_supplier = ExcelControl.getValueUnderHeader("M_ID1");
            final String manual_supplier1 = ExcelControl.getValueUnderHeader("M_ID2");
            final String supplierName = ExcelControl.getValueUnderHeader("Supplier Name");
            final String supplierId = ExcelControl.getValueUnderHeader("SupplierID");


            //Pre-condition
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_BA);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(30));
            tc.menu.select(EMenu.HOME);

            //Step 1
            if(tc.button.exists(EButton.VIEW_ALL))tc.button.click(EButton.VIEW_ALL);
            WaitFor.condition(() -> !tc.node.getValues(ENode.HOMEPAGE_TODO).isEmpty());
            tc.node.performActionOnTODO(ENode.HOMEPAGE_TODO, "Supplier Qualification", sq_case, EActions.GO);
            WaitFor.condition(() -> tc.button.exists(EButton.APPROVE));
            tc.addStepInfo("The Approval screen opens", true, tc.browser.getPageHeaders().contains("Approval"),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            tc.button.click(EButton.APPROVE);
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.menu.select(EMenu.HOME);
            WaitFor.condition(() -> !tc.node.getValues(ENode.HOMEPAGE_TODO).isEmpty());
            boolean isSqPresentInTodoList = tc.node.getValues(ENode.HOMEPAGE_TODO).contains(sq_case);
            tc.addStepInfo("The approval task is no longer in the ToDo list", true, !isSqPresentInTodoList
                    , new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            tc.menu.search(sq_case);
            WaitFor.condition(() -> tc.button.exists(EButton.EXPAND_UTILITY_PANE));
            WaitFor.specificTime(Duration.ofSeconds(3));
            if(tc.utility.getContents(EUtility.FILES_AND_DOCUMENTS).isEmpty())tc.button.click(EButton.EXPAND_UTILITY_PANE);
            this.waitForPDFtoAppear(tc);
            List<String> lst = tc.utility.getContents(EUtility.FILES_AND_DOCUMENTS);
            tc.stepEvaluator
                    .add(()-> !lst.stream().filter(x -> x.contains("Summary")).toList().isEmpty(), "Expected summary pdf not found!")
                    .add(()-> !lst.stream().filter(x -> x.contains("Audit Trail")).toList().isEmpty(), "Expected Audit Trail pdf not found!");
            tc.addStepInfo("Two PDF documents will be generated", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.menu.search(manual_supplier);
            WaitFor.condition(() -> !tc.node.getValues(ENode.ASSIGNMENT_TODO).isEmpty());
            tc.addStepInfo("The Case Screen opens", true, !tc.node.getValues(ENode.ASSIGNMENT_TODO).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            tc.button.click(EButton.GO);
            WaitFor.condition(() -> tc.button.exists(EButton.APPROVE));
            tc.addStepInfo("The Approval Screen opens", true, tc.button.exists(EButton.APPROVE), new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            tc.stepEvaluator.reset();
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.byIndex(0)).isEmpty());
            tc.stepEvaluator
                    .add(()-> tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.byIndex(0)).contains(supplierName),
                            "Expected supplier name not found!")
                    .add(()-> tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.byIndex(1)).contains(supplierId),
                            "Expected supplier id not found!");
            tc.addStepInfo("The given information of all manually created supplier for this Business Organization is shown",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            tc.button.click(EButton.APPROVE);
            WaitFor.condition(() -> tc.alert.getAlertMessage().contains("Thank you! The next step in this case has been routed appropriately."));
            tc.addStepInfo("SHS ID was created based on the Business Organization and Department given. The case start creating " +
                    "records and Key User who started the case is notified",
                    true, tc.alert.getAlertMessage().contains("Thank you! The next step in this case has been routed appropriately."),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 8
            tc.menu.search(manual_supplier1);
            tc.button.click(EButton.GO);
            tc.button.click(EButton.REJECT);
            WaitFor.condition(()-> tc.modal.getTitle().equals("Rejection Comments"));
            tc.editor.sendKeys(EEditor.NOTES, "Rejecting manual supplier");
            tc.button.click(EButton.SUBMIT);
            tc.button.click(EButton.REJECT);
            WaitFor.condition(() -> tc.node.getValues(ENode.CASE_DETAILS).contains("RESOLVED-REJECTED"));
            boolean isRejected = tc.node.getValues(ENode.CASE_DETAILS).contains("RESOLVED-REJECTED");
            tc.addStepInfo("SHS ID is not given and case is resolved-rejected. Key User is notified about case cancellation and " +
                    "justification via email", true, isRejected, new ComparerOptions().takeScreenShotPlatform());
        });
    }
    private void waitForPDFtoAppear(ProjectHandler tc)
    {
        WaitFor.condition(() ->
        {
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.REFRESH);
            List<String> lst = tc.utility.getContents(EUtility.FILES_AND_DOCUMENTS);

            return !lst.stream().filter(x -> x.contains("Summary")).toList().isEmpty() &&
                    !lst.stream().filter(x -> x.contains("Audit Trail")).toList().isEmpty();

        }, Duration.ofMinutes(2), Duration.ofSeconds(10));
    }

}
