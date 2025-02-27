package TestAutomation_1;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class TC_ID_2433
{
    @Test
    @Disabled("clarification")
    void tc_rs_1901()
    {
        IocBuilder.execute(Duration.ofMinutes(20), EResultData.TEST_AUTOMATION, "2433", tc -> {

            //----- STEP 1 -----
            tc.browser.start(WebDrv.CHROME, ETestData.AutoUser.get());
            tc.addStepInfo("Rapid Response will be visible.", true, tc.browser.getCurrentURL().contains("default.aspx")
                    && tc.browser.getCurrentURL().toLowerCase().contains("RapidResponse".toLowerCase())
                    && tc.button.exists(EButton.USER_PROFILE));

            //Create scenario
            final String scenarioParent = "S&OP Candidate";
            tc.menu.openMenu(EActivity.SCENARIOS);
            String scenario = tc.menu.scenarios.create(scenarioParent);

            //----- STEP 2 -----
            tc.menu.openMenu(EActivity.RESOURCES);
            String workBook = "SHS S&OP Write History Records";
            String workSheet = "Forecast Detail Records";
            String part = "All Parts";
            String site = "All sites";
            tc.menu.resource.filterResources(workBook, true);
            WaitFor.condition(() -> tc.modal.exists(EModal.DATA_SETTINGS));
            tc.workBook.menu.setDataSettings(List.of(
                    Map.entry(EComboBox.WORKSHEET, workSheet),
                    Map.entry(EComboBox.SCENARIO, scenario),
                    Map.entry(EComboBox.FILTER, part),
                    Map.entry(EComboBox.SITE, site)
            ));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.modal.exists(EModal.DATA_SETTINGS), "Data settings modal not opened!")
                    .add(workSheet, tc.combo.getSelected(EComboBox.WORKSHEET), "Worksheet wasn't selected!")
                    .add(scenario, tc.combo.getSelected(EComboBox.SCENARIO), "Scenario wasn't selected!")
                    .add(part, tc.combo.getSelected(EComboBox.FILTER), "Filter wasn't selected!")
                    .add(site, tc.combo.getSelected(EComboBox.SITE), "Site wasn't selected!");
            tc.button.click(EButton.OPEN);
            WaitFor.condition(() -> tc.workBook.getSelected().equals(workBook));
            tc.stepEvaluator.add(workBook, tc.workBook.getSelected(), "Wrong workbook opened!");

            // TODO: 10/9/2023 finish 
         });
    }
}
