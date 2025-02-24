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

public class TC05_R_and_D_SU
{
    @Test
    void R_and_D_SU()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.SLIM_FS, "44023", tc ->
        {
            if(!ExcelControl.isDataAvailable(List.of("Supplier2")))
            {
                tc.addStepInfo("Failed due to data absence in Excel Sheet", true, false,
                        new ComparerOptions().abort());
            }
            String supplier2 = ExcelControl.getValueUnderHeader("Supplier2");
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL, ETestData.RISHITH_SU);

            //STEP 1
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(30));
            tc.menu.select(EMenu.CREATE, EMenu.HOME);
            WaitFor.condition(() -> tc.button.exists(EButton.VIEW_ALL));
            tc.button.click(EButton.VIEW_ALL);
            BrowserControl.waitForLoadingIndicatorToAppear();
            BrowserControl.waitForLoadingIndicator();
            tc.node.performActionOnTODO(ENode.HOMEPAGE_TODO, "R&D", supplier2, EActions.GO);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            WaitFor.condition(() -> tc.table.exists(ETable.byIndex(1)));
            boolean isEditable = tc.table.isComboEditable(ETable.byIndex(1), EColumn.ANSWER, 8);
            tc.addStepInfo("Questions assigned to Category \"Technology and Innovations\" are open to receive an input", true,
                    isEditable, new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            String comboValue1 = "Cancellation of goods receipt booking";
            String comboValue2 = "2";
            String comboValue3 = "yes, information only";
            String comboValue4 = "achieved all time-lines";
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.ANSWER, 7, comboValue1);
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.ANSWER, 8, comboValue2);
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.ANSWER, 9, comboValue3);
            tc.table.selectFromCombo(ETable.byIndex(1), EColumn.ANSWER, 10, comboValue4);
            List<String> columnElements = tc.table.getItemsFromColumn(ETable.byIndex(1), EColumn.ANSWER);
            tc.button.click(EButton.SUBMIT);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(10));
            WaitFor.condition(()->tc.modal.exists(EModal.MODAL_WRAPPER),Duration.ofSeconds(10));
            tc.button.click(EButton.CONFIRM);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(10));
            tc.button.click(EButton.EXPAND_EVALUATION_DETAILS);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator
                    .add(() -> columnElements.stream().anyMatch(e -> e.contains(comboValue1)), "Technology & Innovation with Index 1 is not edited")
                    .add(() -> columnElements.stream().anyMatch(e -> e.contains(comboValue2)), "Technology & Innovation with Index 2 is not edited")
                    .add(() -> columnElements.stream().anyMatch(e -> e.contains(comboValue3)), "Technology & Innovation with Index 3 is not edited")
                    .add(() -> columnElements.stream().anyMatch(e -> e.contains(comboValue4)), "Technology & Innovation with Index 4 is not edited");
            tc.addStepInfo("Scores are accepted and Strategic Summary table displays the result according chosen values.\n" +
                    "Case status change to Evaluated", "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.stepEvaluator.reset();
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(10));
            tc.menu.selectWithOutExpand(EMenu.CET);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.stepEvaluator
                    .add(() -> tc.table.getValuesFromRow(ETable.byIndex(0), 1).stream().anyMatch(s -> s.contains("AU")), "Wanted user not allocated for logistics")
                    .add(() -> tc.table.getValuesFromRow(ETable.byIndex(0), 2).stream().anyMatch(s -> s.contains("SU")), "Wanted user not allocated for R&D");
            tc.addStepInfo("CET display Standard User as R&D member and Logistics as AU", "ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

        });
    }
}
