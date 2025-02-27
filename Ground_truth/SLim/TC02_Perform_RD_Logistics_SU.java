package Create_SE_List_And_Start;

import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import TestUtils.ExcelControl;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC02_Perform_RD_Logistics_SU
{
    @Test
    void Perform_RD_Logistics_SU()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.SLIM_FS, "44020", tc ->
        {
            if(!ExcelControl.isDataAvailable(List.of("Supplier1")))
            {
                tc.addStepInfo("Failed due to data absence in Excel Sheet", true, false,
                        new ComparerOptions().abort());
            }

            String taskID = ExcelControl.getValueUnderHeader("Supplier1");

            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_SU);

            //STEP 1
            tc.addStepInfo("Emails received for both Strategic Evaluation cases", true, true,
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(30));
            String logisticQuestion = "Answer questions for Logistics";
            tc.menu.select(EMenu.CREATE, EMenu.HOME);
            tc.button.click(EButton.VIEW_ALL);
            WaitFor.condition(() -> tc.button.exists(EButton.VIEW_LESS));
            tc.node.performActionOnTODO(ENode.HOMEPAGE_TODO ,logisticQuestion, taskID, EActions.GO);
            WaitFor.condition(() -> tc.nav.getBreadcrumbTitle().contains(taskID));
            String section = tc.nav.getBreadcrumbTitle();
            boolean isLogisticTask7Enabled = tc.table.isComboEditable(ETable.byIndex(1), EColumn.byIndex(5), 5);
            boolean isLogisticTask13Enabled = tc.table.isComboEditable(ETable.byIndex(1), EColumn.byIndex(5), 11);
            tc.stepEvaluator
                    .add(() -> section.contains(taskID), "Questionnaire is no opened")
                    .add(() -> isLogisticTask7Enabled, "Task 7 is not able to take input")
                    .add(() -> isLogisticTask13Enabled, "Task 13 is not able to take Input");
            tc.addStepInfo("Questionnaire opens and only task 7 and 13 are open to receive an input", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.stepEvaluator.reset();
            String logisticTask7Value = "ESI/SupplyON plus TeamCenter";
            String logisticTask13Value = "greater or equal 60% of part numbers";
            WaitFor.specificTime(Duration.ofMillis(2000));
            String logisticTask7EnteredValue = tc.table.selectFromCombo(ETable.byIndex(1), EColumn.byIndex(5), 5, logisticTask7Value);
            WaitFor.specificTime(Duration.ofMillis(2000));
            String logisticTask13EnteredValue = tc.table.selectFromCombo(ETable.byIndex(1), EColumn.byIndex(5), 11, logisticTask13Value);
            tc.stepEvaluator
                    .add(() -> logisticTask7Value.equalsIgnoreCase(logisticTask7EnteredValue), "Unable to edit task 7")
                    .add(() -> logisticTask13Value.equalsIgnoreCase(logisticTask13EnteredValue), "Unable to edit task 13");
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(tc.modal::exists, Duration.ofSeconds(2));
            if(tc.modal.exists())
            {
                tc.button.click(EButton.CONFIRM);
            }
            tc.addStepInfo("Comments are not mandatory and scores selected are being displayed", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.stepEvaluator.reset();
            BrowserControl.waitForLoadingIndicator();
            String RandDQuestion = "Answer questions for R&D";
            tc.menu.select(EMenu.CREATE, EMenu.HOME);
            tc.button.click(EButton.VIEW_ALL);
            WaitFor.condition(() -> tc.button.exists(EButton.VIEW_LESS));
            tc.node.performActionOnTODO(ENode.HOMEPAGE_TODO ,RandDQuestion, taskID, EActions.GO);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            String section1 = tc.browser.getTitleOfActiveWindow();
            tc.addStepInfo("Questionnaire opens", true, section1.contains(taskID), new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.stepEvaluator.reset();
            String RandDTask9Value = "Late goods receipt booking";
            String RandDTask10Value = "greater than 2";
            String RandDTask11Value = "yes, and highlights opportunities";
            String RandDTask12Value = "achieved all time-lines";
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.ANSWER, 7, RandDTask9Value);
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.ANSWER, 8, RandDTask10Value);
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.ANSWER, 9, RandDTask11Value);
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.ANSWER, 10, RandDTask12Value);
            tc.button.click(EButton.SUBMIT);
            WaitFor.condition(tc.modal::exists, Duration.ofSeconds(2));
            if(tc.modal.exists())
            {
                tc.button.click(EButton.CONFIRM);
            }
            tc.menu.selectWithOutExpand(EMenu.DETAILS);
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.REFRESH);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.button.click(EButton.EXPAND_EVALUATION_DETAILS);
            List<String> columnValues = tc.table.getItemsFromColumn(ETable.byIndex(1), EColumn.ANSWER);
            tc.stepEvaluator
                    .add(() -> columnValues.contains(RandDTask9Value), "incorrect task 9 value")
                    .add(() -> columnValues.contains(RandDTask10Value), "incorrect task 10 value")
                    .add(() -> columnValues.contains(RandDTask11Value), "incorrect task 11 value")
                    .add(() -> columnValues.contains(RandDTask12Value), "incorrect task 12 value");

            tc.addStepInfo("Comments are not mandatory and scores selected are being displayed", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

        });
    }
}
