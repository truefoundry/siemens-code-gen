package Create_SE_List_And_Start;

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

public class TC06_Complete_Evaluation_list_KU
{
    @Test
    void Complete_Evaluation_list_KU()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.SLIM_FS, "44024", tc ->
        {
            if(!ExcelControl.isDataAvailable(List.of("EL-ID", "Supplier1", "Supplier2")))
            {
                tc.addStepInfo("Failed due to data absence in Excel Sheet", true, false,
                        new ComparerOptions().abort());
            }
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_KU);
            String evaluationID = ExcelControl.getValueUnderHeader("EL-ID");
            String supplier1 = ExcelControl.getValueUnderHeader("Supplier1");;
            String supplier2 = ExcelControl.getValueUnderHeader("Supplier2");

            //STEP 1
            String evaluationList = "Evaluation List";
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(10));
            tc.menu.select(EMenu.CREATE, EMenu.HOME);
            tc.button.click(EButton.VIEW_ALL);
            WaitFor.condition(() -> tc.button.exists(EButton.VIEW_LESS));
            tc.node.performActionOnTODO(ENode.HOMEPAGE_TODO ,evaluationList, evaluationID, EActions.GO);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(0)));
            tc.table.itemClick(ETable.byIndex(0), EColumn.CASE_ID, supplier1);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.button.click(EButton.EXPAND_EVALUATION_DETAILS);
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.ADJUST_QUESTIONNAIRE);
            WaitFor.condition(() -> tc.table.isComboEditable(ETable.byIndex(1), EColumn.ANSWER, 8));
            tc.addStepInfo("Questionnaire is again open to receive inputs.", true,
                    tc.table.isComboEditable(ETable.byIndex(1), EColumn.ANSWER, 8),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            String internalCommentS1 = "Internal Comment: Key User changing score for question 10 from max to min";
            String externalCommentS1 = "External Comment: Key User changing score for question 10 from max to min";
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.ANSWER, 8, "0");
            tc.table.setTextToEditorBox(ETable.byIndex(1), EColumn.INTERNAL_COMMENT, 8, internalCommentS1);
            tc.table.setTextToEditorBox(ETable.byIndex(1), EColumn.EXTERNAL_COMMENT, 8, externalCommentS1);
            tc.button.click(EButton.SUBMIT);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.REFRESH);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.button.click(EButton.EXPAND_EVALUATION_DETAILS);
            String supplier1StrategyActualScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Strategy & Procurement", EColumn.ACTUAL_CATEGORY_SCORE);
            String supplier1StrategyMaxScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Strategy & Procurement", EColumn.MAX_CATEFORY_SCORE);
            String supplier1TechnologyActualScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Technology & Innovation", EColumn.ACTUAL_CATEGORY_SCORE);
            String supplier1TechnologyMaxScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Technology & Innovation", EColumn.MAX_CATEFORY_SCORE);
            String supplier1LogisticsActualScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Logistics", EColumn.ACTUAL_CATEGORY_SCORE);
            String supplier1LogisticsMaxScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Logistics", EColumn.MAX_CATEFORY_SCORE);
            String supplier1TotalActualScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Total Score", EColumn.ACTUAL_CATEGORY_SCORE);
            String supplier1TotalMaxScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Total Score", EColumn.MAX_CATEFORY_SCORE);

            List<String> headers = List.of("InternalCommentS1", "ExternalCommentS1", "Supplier1StrategyActualScore", "Supplier1StrategyMaxScore", "Supplier1TechnologyActualScore",
                    "Supplier1TechnologyMaxScore", "Supplier1LogisticsActualScore", "Supplier1LogisticsMaxScore", "Supplier1TotalActualScore",
                    "Supplier1TotalMaxScore");
            List<String> values = List.of(internalCommentS1, externalCommentS1, supplier1StrategyActualScore, supplier1StrategyMaxScore,
                    supplier1TechnologyActualScore, supplier1TechnologyMaxScore, supplier1LogisticsActualScore, supplier1LogisticsMaxScore,
                    supplier1TotalActualScore, supplier1TotalMaxScore);
            ExcelControl.createAndAppendDataToExcel(headers,  values);

            tc.stepEvaluator.add(() -> tc.table.getItemValueFromColumn(ETable.byIndex(1), EColumn.NUMBER, "10", EColumn.ANSWER).equals("0"),
                    "Scores for question 10 is not accepted.")
                    .add(() -> tc.table.getItemValueFromColumn(ETable.byIndex(1), EColumn.NUMBER, "10", EColumn.INTERNAL_COMMENT).equals(internalCommentS1),
                            "Incorrect Internal comment")
                    .add(() -> tc.table.getItemValueFromColumn(ETable.byIndex(1), EColumn.NUMBER, "10", EColumn.EXTERNAL_COMMENT).equals(externalCommentS1),
                            "Incorrect External comment");
            tc.addStepInfo("Scores are accepted and comments are displayed", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.stepEvaluator.reset();
            tc.button.selectDropDownItem(EButton.BREADCRUMB, EDropDown.EVALUATION_LIST_SE_AUTOMATION_DEMO);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(0)));
            tc.table.itemClick(ETable.byIndex(0), EColumn.CASE_ID, supplier2);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.button.click(EButton.EXPAND_EVALUATION_DETAILS);
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.ADJUST_QUESTIONNAIRE);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.addStepInfo("Questionnaire is again open to receive inputs.", true,
                    tc.table.isComboEditable(ETable.byIndex(1), EColumn.ANSWER, 10),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.stepEvaluator.reset();
            String internalCommentS2 ="Internal Comment: Key User changing score for question 12 from max to min";
            String externalCommentS2 ="External Comment: Key User changing score for question 12 from max to min";
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.ANSWER, 10, "missed several milestones");
            tc.table.setTextToEditorBox(ETable.byIndex(1), EColumn.INTERNAL_COMMENT, 10, internalCommentS2);
            tc.table.setTextToEditorBox(ETable.byIndex(1), EColumn.EXTERNAL_COMMENT, 10, externalCommentS2);
            tc.button.click(EButton.SUBMIT);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.REFRESH);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.button.click(EButton.EXPAND_EVALUATION_DETAILS);
            String supplier2StrategyActualScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Strategy & Procurement", EColumn.ACTUAL_CATEGORY_SCORE);
            String supplier2StrategyMaxScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Strategy & Procurement", EColumn.MAX_CATEFORY_SCORE);
            String supplier2TechnologyActualScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Technology & Innovation", EColumn.ACTUAL_CATEGORY_SCORE);
            String supplier2TechnologyMaxScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Technology & Innovation", EColumn.MAX_CATEFORY_SCORE);
            String supplier2LogisticsActualScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Logistics", EColumn.ACTUAL_CATEGORY_SCORE);
            String supplier2LogisticsMaxScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Logistics", EColumn.MAX_CATEFORY_SCORE);
            String supplier2TotalActualScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Total Score", EColumn.ACTUAL_CATEGORY_SCORE);
            String supplier2TotalMaxScore = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CATEGORY, "Total Score", EColumn.MAX_CATEFORY_SCORE);

            List<String> headers2 = List.of("InternalCommentS2", "ExternalCommentS2", "Supplier2StrategyActualScore", "Supplier2StrategyMaxScore", "Supplier2TechnologyActualScore",
                    "Supplier2TechnologyMaxScore", "Supplier2LogisticsActualScore", "Supplier2LogisticsMaxScore", "Supplier2TotalActualScore",
                    "Supplier2TotalMaxScore");
            List<String> values2 = List.of(internalCommentS2, externalCommentS2, supplier2StrategyActualScore, supplier2StrategyMaxScore,
                    supplier2TechnologyActualScore, supplier2TechnologyMaxScore, supplier2LogisticsActualScore, supplier2LogisticsMaxScore,
                    supplier2TotalActualScore, supplier2TotalMaxScore);
            ExcelControl.createAndAppendDataToExcel(headers2,  values2);

            tc.stepEvaluator.add(() -> tc.table.getItemValueFromColumn(ETable.byIndex(1), EColumn.NUMBER, "12", EColumn.ANSWER).equals("missed several milestones"),
                            "Scores for question 10 is not accepted.")
                    .add(() -> tc.table.getItemValueFromColumn(ETable.byIndex(1), EColumn.NUMBER, "12", EColumn.INTERNAL_COMMENT).equals(internalCommentS2),
                            "Incorrect Internal comment")
                    .add(() -> tc.table.getItemValueFromColumn(ETable.byIndex(1), EColumn.NUMBER, "12", EColumn.EXTERNAL_COMMENT).equals(externalCommentS2),
                            "Incorrect External comment");
            tc.addStepInfo("Scores are accepted and comments are displayed", "ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.stepEvaluator.reset();
            tc.nav.navigateToELlist();
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.stepEvaluator.add(() -> tc.nav.getBreadcrumbTitle().equals(evaluationID), "Failed to navigate " +
                            "to Evaluation List")
                    .add(() -> tc.node.getAllTitlesFromTODO(ENode.ASSIGNMENT_TODO).contains("Review Evaluation List"), "Review Evaluation " +
                            "Task is not present on the To do list");
            tc.addStepInfo("Evaluation opens and Review Evaluation Task is on the To do list", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.stepEvaluator.reset();
            tc.stepEvaluator.add(() -> tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CASE_ID, supplier1, EColumn.TOTAL_SCORE).equals(supplier1TotalActualScore),
                    "Incorrect scores are displayed for Supplier1.")
                    .add(() -> tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.CASE_ID, supplier2, EColumn.TOTAL_SCORE).equals(supplier2TotalActualScore),
                                    "Incorrect scores are displayed for Supplier2.");
            tc.addStepInfo("Given scores are being displayed", "ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.stepEvaluator.reset();
            tc.button.click(EButton.GO);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.addStepInfo("The EL opens and Complete List button is available", true,
                    tc.button.exists(EButton.COMPLETE_LIST), new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            tc.button.click(EButton.COMPLETE_LIST);
            WaitFor.condition(tc.modal::exists);
            tc.button.click(EButton.YES);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            this.waitForMergeResponse(tc);
            WaitFor.specificTime(Duration.ofSeconds(3));
            WaitFor.condition(() -> tc.node.getAllTitlesFromTODO(ENode.ASSIGNMENT_TODO).contains("Awaiting COI Merge Response"));
            tc.addStepInfo("Task shows that system is creating records for both suppliers", true,
                    !tc.node.getAllTitlesFromTODO(ENode.ASSIGNMENT_TODO).isEmpty() && tc.node.getAllTitlesFromTODO
                            (ENode.ASSIGNMENT_TODO).contains("Awaiting COI Merge Response"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 9
            tc.menu.selectWithOutExpand(EMenu.HISTORY);
            tc.button.click(EButton.FIELD_HISTORY);
            WaitFor.condition(() -> tc.table.exists(ETable.FIELD_HISTORY));
            tc.table.itemClick(ETable.FIELD_HISTORY, EColumn.FIELD, "#0");
            WaitFor.specificTime(Duration.ofSeconds(1));
            String businessPartnerSummary = tc.table.getItemValueFromColumn(ETable.byIndex(0), EColumn.FIELD, "BusinessPartnerSummary", EColumn.NEW_VALUE);
            tc.stepEvaluator.add(() -> businessPartnerSummary.contains("BusinessPartnerID"), "BusinessPartnerID is missing.")
                            .add(() -> businessPartnerSummary.contains("SE CASEID"), "SE CASEID is missing.")
                            .add(() -> businessPartnerSummary.contains("EvaluationPeriodFrom"), "EvaluationPeriodFrom is missing.")
                            .add(() -> businessPartnerSummary.contains("EvaulationPeriodUntil"), "EvaulationPeriodUntil is missing.")
                            .add(() -> businessPartnerSummary.contains("TotalScore"), "TotalScore is missing.")
                            .add(() -> businessPartnerSummary.contains("Strategy & Procurement"), "Strategy & Procurement is missing.")
                            .add(() -> businessPartnerSummary.contains("Technology & Innovation Score"), "Technology & Innovation Score is missing.")
                            .add(() -> businessPartnerSummary.contains("Logistics"), "Logistics is missing.");
            tc.addStepInfo("For both suppliers BusinessPartnerSummary: calculated property is displayed as aggregation of \n" +
                    "[BusinessPartnerID][SE CaseID][EvaluationPeriodFrom][EvaluationPeriodUntil][TotalScore][Strat. and Proc. Score]" +
                    "[Tech. and Innovation Score][Logistics Score]", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 10
            tc.stepEvaluator.reset();
            tc.menu.selectWithOutExpand(EMenu.DETAILS);
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(0)));
            tc.table.itemClick(ETable.byIndex(0), EColumn.CASE_ID, supplier1);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            tc.addStepInfo("Case opens", true, tc.nav.getBreadcrumbTitle().equals(supplier1), new ComparerOptions().takeScreenShotPlatform());

            //STEP 11
            tc.stepEvaluator.reset();
            tc.utility.expand();
            this.waitForPDFtoAppear(tc);
            List<String> caseRelatedContent = tc.utility.getContents(EUtility.FILES_AND_DOCUMENTS);
            tc.stepEvaluator.add(() -> caseRelatedContent.stream().anyMatch(s -> s.contains("Summary")), "Summary file is not present.")
                            .add(() -> caseRelatedContent.stream().anyMatch(s -> s.contains("Audit Trail")), "Audit Trail is not present.");
            tc.addStepInfo("Summary file and audit trail appear", "ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }

    private void waitForMergeResponse(ProjectHandler tc)
    {
        WaitFor.condition(() ->
        {
            tc.button.selectDropDownItem(EButton.ACTIONS, EDropDown.REFRESH);

            return !tc.node.getAllTitlesFromTODO(ENode.ASSIGNMENT_TODO).isEmpty() && tc.node.getAllTitlesFromTODO(ENode.ASSIGNMENT_TODO).contains("Awaiting COI Merge Response");
        }, Duration.ofMinutes(2), Duration.ofSeconds(10));
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
