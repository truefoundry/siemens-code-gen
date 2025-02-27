package Create_SE_List_And_Start;

import CompositionRoot.IocBuilder;
import Enums.*;
import TestUtils.ExcelControl;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC04_Logistics_AU
{
    @Test
    void Logistics_AU()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.SLIM_FS, "44022", tc ->
        {
            if(!ExcelControl.isDataAvailable(List.of("Supplier2")))
            {
                tc.addStepInfo("Failed due to data absence in Excel Sheet", true, false,
                        new ComparerOptions().abort());
            }
            String supplier2 = ExcelControl.getValueUnderHeader("Supplier2");
            TcLog.info("supplier2 %s".formatted(supplier2));
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL,ETestData.RISHITH_AU);

            //STEP 1
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(10));
            tc.addStepInfo("Email received that a task has been transferred to you", "ok", "ok");

            //STEP 2
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(30));
            tc.menu.search(supplier2);
            WaitFor.specificTime(Duration.ofSeconds(3));
            WaitFor.condition(() -> tc.node.getValues(ENode.CASE_HEADER).contains(supplier2));
            tc.button.click(EButton.GO);
            boolean isLogisticTask7Enabled = tc.table.isComboEditable(ETable.byIndex(1), EColumn.byIndex(5), 5);
            boolean isLogisticTask13Enabled = tc.table.isComboEditable(ETable.byIndex(1), EColumn.byIndex(5), 11);
            tc.stepEvaluator
                    .add(() -> isLogisticTask7Enabled, "Task 7 is not able to take input")
                    .add(() -> isLogisticTask13Enabled, "Task 13 is not able to take Input");
            tc.addStepInfo("Logistics questions 7 and 13 are open to receive a score", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            tc.stepEvaluator.eval();
            String logisticTask7Value = "ESI/SupplyON plus TeamCenter";
            String logisticTask13Value = "greater or equal 60% of part numbers";
            String internalComment ="Test " + supplier2;
            String externalComment ="Test " + supplier2;
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.byIndex(5), 5, logisticTask7Value);
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.byIndex(5), 11, logisticTask13Value);
            tc.table.setTextToEditorBox(ETable.byIndex(1), EColumn.INTERNAL_COMMENT, 5, internalComment);
            tc.table.setTextToEditorBox(ETable.byIndex(1), EColumn.EXTERNAL_COMMENT, 5, internalComment);
            tc.table.setTextToEditorBox(ETable.byIndex(1), EColumn.INTERNAL_COMMENT, 11, internalComment);
            tc.table.setTextToEditorBox(ETable.byIndex(1), EColumn.EXTERNAL_COMMENT, 11, internalComment);
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(tc.modal::exists, Duration.ofSeconds(2));
            if(tc.modal.exists())
            {
                tc.button.click(EButton.CONFIRM);
            }
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.REFRESH);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.button.click(EButton.EXPAND_EVALUATION_DETAILS);
            List<String> columnValues = tc.table.getItemsFromColumn(ETable.byIndex(1), EColumn.ANSWER);
            List<String> internalComments = tc.table.getItemsFromColumn(ETable.byIndex(1), EColumn.INTERNAL_COMMENT);
            List<String> externalComments = tc.table.getItemsFromColumn(ETable.byIndex(1), EColumn.EXTERNAL_COMMENT);
            tc.stepEvaluator
                    .add(() -> columnValues.contains(logisticTask7Value), "incorrect task 7 value")
                    .add(() -> columnValues.contains(logisticTask13Value), "incorrect task 13 value")
                    .add(() -> internalComments.contains(internalComment), "incorrect task internal comments")
                    .add(() -> externalComments.contains(externalComment), "incorrect task external comments");

            tc.addStepInfo("Scores and comments (i.a.) are accepted. Logistic Task disappear from the To Do list.", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

        });
    }
}
