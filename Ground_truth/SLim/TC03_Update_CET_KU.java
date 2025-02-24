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
import java.util.Objects;

public class TC03_Update_CET_KU
{
    @Test
    void Update_CET_KU()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.SLIM_FS, "44021", tc ->
        {
            if(!ExcelControl.isDataAvailable(List.of("EL-ID", "Supplier1", "Supplier2")))
            {
                tc.addStepInfo("Failed due to data absence in Excel Sheet", true, false,
                        new ComparerOptions().abort());
            }
            String evaluationID = ExcelControl.getValueUnderHeader("EL-ID");
            String supplier1 = ExcelControl.getValueUnderHeader("Supplier1");
            String supplier2 =  ExcelControl.getValueUnderHeader("Supplier2");

            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);

            //STEP 1
            String evaluationList = "Evaluation List";
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.menu.select(EMenu.CREATE, EMenu.HOME);
            tc.button.click(EButton.VIEW_ALL);
            WaitFor.condition(() -> tc.button.exists(EButton.VIEW_LESS));
            tc.node.performActionOnTODO(ENode.HOMEPAGE_TODO ,evaluationList, evaluationID, EActions.GO);
            WaitFor.condition(() -> tc.button.exists(EButton.ACTIONS));
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.REFRESH);
            tc.button.click(EButton.VIEW_ALL);
            WaitFor.specificTime(Duration.ofMillis(500));
            String logisticQuestion = "Answer questions for Logistics";
            tc.node.performActionOnTODO( ENode.ASSIGNMENT_TODO,logisticQuestion, supplier2, EActions.LINK);
            WaitFor.condition(() -> tc.button.exists(EButton.ACTIONS));
            WaitFor.specificTime(Duration.ofSeconds(3));
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.LOGISTICS_TRANSFER_ASSIGNMENT);
            WaitFor.condition(() -> tc.modal.exists(EModal.TRANSFER_ASSIGNMENT));
            tc.addStepInfo("The transfer Assignment screen opens", true,
                    tc.modal.exists(EModal.TRANSFER_ASSIGNMENT), new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            String operatorName = "Rishith_AU";
            tc.combo.select(EComboBox.TRANSFER_TO, "Operator");
            WaitFor.condition(() -> tc.radio.exists(ERadio.OPERATOR_FROM_THE_SYSTEM));
            tc.radio.select(ERadio.OPERATOR_FROM_THE_SYSTEM);
            WaitFor.specificTime(Duration.ofSeconds(2)); //TODO: Should be removed later
            tc.combo.select(EComboBox.OPERATOR, operatorName);
            tc.button.click(EButton.TRANSFER);
            WaitFor.condition(() -> !tc.modal.exists());
            tc.menu.selectWithOutExpand(EMenu.CET);
            WaitFor.specificTime(Duration.ofSeconds(5)); //TODO: Should be removed later
            boolean isPresent = WaitFor.condition(()-> tc.expand.getAvailableHeaders().contains("CET Members"));
            if(!isPresent)
            {
                tc.browser.refresh();
                BrowserControl.waitForLoadingIndicator();
                WaitFor.condition(()-> tc.expand.getAvailableHeaders().contains("CET Members"));
            }
            tc.expand.expandSection(EExpand.CET_MEMBERS);
            tc.addStepInfo("Task is updated accordingly and CET tab does NOT display the new CET member of Logistics, only when task is performed will display", true,
                    tc.table.getValuesFromRow(ETable.byIndex(0), 2).stream().anyMatch(val -> val.contains("Rishith_SU")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.button.selectDropDownItem(EButton.BREADCRUMB, EDropDown.EVALUATION_LIST_SE_AUTOMATION_DEMO);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR,Duration.ofSeconds(10));
            tc.button.click(EButton.VIEW_ALL);
            WaitFor.condition(() -> tc.button.exists(EButton.VIEW_LESS));
            tc.node.performActionOnTODO(ENode.ASSIGNMENT_TODO, "Answer questions for SKA", supplier1, EActions.GO);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            String section = tc.browser.getTitleOfActiveWindow();
            this.isQuestionsEnabled(tc);
            tc.stepEvaluator.add(() -> section.contains(Objects.requireNonNull(supplier1)), "Questionnaire is not opened");
            tc.addStepInfo("Questionnaire opens and only the questions 1 to 8 except 7 are open to receive input",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.stepEvaluator.reset();
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(1)));
            this.selectAnswers(tc);
            tc.addStepInfo("Scores are accepted and Strategic Summary table displays the result according chosen values",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.stepEvaluator.reset();
            tc.button.selectDropDownItem(EButton.BREADCRUMB, EDropDown.EVALUATION_LIST_SE_AUTOMATION_DEMO);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.button.click(EButton.VIEW_ALL);
            WaitFor.condition(() -> tc.button.exists(EButton.VIEW_LESS));
            tc.node.performActionOnTODO(ENode.ASSIGNMENT_TODO, "Answer questions for SKA", supplier2, EActions.GO);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            String section1 = tc.browser.getTitleOfActiveWindow();
            this.isQuestionsEnabled(tc);
            tc.stepEvaluator
                    .add(() -> section1.contains(Objects.requireNonNull(supplier2)), "Questionnaire is not opened");
            tc.addStepInfo("Questionnaire opens and only the questions 1 to 8 except 7 are open to receive input",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.stepEvaluator.reset();
            this.selectAnswers(tc);
            tc.addStepInfo("Scores are accepted and Strategic Summary table displays the result according chosen values",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

        });

    }
    private void isQuestionsEnabled(CompositionRoot.ProjectHandler tc)
    {
        if(tc.button.exists(EButton.GO)) tc.button.click(EButton.GO);
        boolean isQuestion1Enabled = tc.table.isComboEditable(ETable.byIndex(1), EColumn.byIndex(5), 0);
        boolean isQuestion2Enabled = tc.table.isComboEditable(ETable.byIndex(1), EColumn.byIndex(5), 1);
        boolean isQuestion4Enabled = tc.table.isComboEditable(ETable.byIndex(1), EColumn.byIndex(5), 2);
        boolean isQuestion5Enabled = tc.table.isComboEditable(ETable.byIndex(1), EColumn.byIndex(5), 3);
        boolean isQuestion6Enabled = tc.table.isComboEditable(ETable.byIndex(1), EColumn.byIndex(5), 4);
        boolean isQuestion8Enabled = tc.table.isComboEditable(ETable.byIndex(1), EColumn.byIndex(5), 6);
        tc.stepEvaluator
                .add(() -> isQuestion1Enabled, "Question 1 is not able to receive input")
                .add(() -> isQuestion2Enabled, "Question 2 is not able to receive input")
                .add(() -> isQuestion4Enabled, "Question 4 is not able to receive input")
                .add(() -> isQuestion5Enabled, "Question 5 is not able to receive input")
                .add(() -> isQuestion6Enabled, "Question 6 is not able to receive input")
                .add(() -> isQuestion8Enabled, "Question 8 is not able to receive input");
    }

    private void selectAnswers(CompositionRoot.ProjectHandler tc)
    {
        String question1Value = "0";
        String question2Value = "Greater or equal '5 years agreed";
        String question4Value = "greater or equal 4%";
        String question5Value = "greater than 2";
        String question6Value = "supports at least 3 out of 4 mentioned methods";
        String question8Value = "greater or equal 120";
        tc.table.selectFromCombo(ETable.byIndex(1), EColumn.byIndex(5), 0, question1Value);
        tc.table.selectFromCombo(ETable.byIndex(1), EColumn.byIndex(5), 1, question2Value);
        WaitFor.specificTime(Duration.ofSeconds(1));
        tc.table.selectFromCombo(ETable.byIndex(1), EColumn.byIndex(5), 2, question4Value);
        tc.table.selectFromCombo(ETable.byIndex(1), EColumn.byIndex(5), 3, question5Value);
        tc.table.selectFromCombo(ETable.byIndex(1), EColumn.byIndex(5), 4, question6Value);
        tc.table.selectFromCombo(ETable.byIndex(1), EColumn.byIndex(5), 6, question8Value);
        tc.button.click(EButton.SUBMIT);
        WaitFor.condition(tc.modal::exists, Duration.ofSeconds(2));
        if(tc.modal.exists())
        {
            tc.button.click(EButton.CONFIRM);
            WaitFor.condition(() -> !tc.modal.exists());
        }
        tc.menu.selectWithOutExpand(EMenu.DETAILS);
        tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.REFRESH);
        tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
        tc.expand.expandSection(EExpand.EVALUATION_DETAILS);
        List<String> columnValues = tc.table.getItemsFromColumn(ETable.byIndex(1), EColumn.ANSWER);
        tc.stepEvaluator.add(() -> columnValues.contains(question1Value), "Incorrect value for question 1.")
                .add(() -> columnValues.contains(question1Value), "Incorrect value for question 1.")
                .add(() -> columnValues.contains(question2Value), "Incorrect value for question 2.")
                .add(() -> columnValues.contains(question4Value), "Incorrect value for question 4.")
                .add(() -> columnValues.contains(question5Value), "Incorrect value for question 5.")
                .add(() -> columnValues.contains(question6Value), "Incorrect value for question 6.")
                .add(() -> columnValues.contains(question8Value), "Incorrect value for question 8.");
    }
}
