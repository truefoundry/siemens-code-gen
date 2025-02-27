package CAD_TMF_Zone_Operation_Lead_Role;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC06_Zone_Operation_Lead_Role_Schedule_Task_Notification_Actions_AddtoClipboard
{
    @Test
    void TC_06()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.TMF_ZONE_OPERATION_LEAD_ROLE, "104819", tc ->
        {
            tc.browser.start(WebDrv.CHROME, ETestData.URL, new CoreStartOptions());
            tc.browser.login(ETestData.ZONE_OPERATION_USER);

            // STEP 1
            tc.sideBar.openSideMenu();
            WaitFor.condition(()->tc.sideBar.getElements().containsAll(List.of("Schedule", "Home","Calendar Management","Analytics")));
            tc.sideBar.select(ESideBar.SCHEDULE);
            WaitFor.specificTime(Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()-> tc.browser.getCurrentURL().toLowerCase().contains("schedule".toLowerCase()), "Schedule Page is not visible")
                    .add(()-> (tc.tab.getAllTabs()).containsAll(List.of("Tasks","Clipboard","Gantt","Map")), "Some tabs('Tasks','Clipboard','Gantt','Map') present on the Schedule page are not available")
                    .add(()-> tc.tab.isTabPresent(ETab.TERRITORY_PLANNING),"The tab terrirtory planning is not present");
             tc.addStepInfo("Schedule screen should be reflected with Tasks, Clipboard, Gantt, Map, Territory Planning,Working on Domain options",
                    "ok", tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());


            //STEP 2
            tc.tab.select(ETab.TASKS);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->!tc.notification.getAllNotifications().isEmpty(),"Notifications present under task section is not available")
                    .add(()->tc.button.isDisplayed(EButton.FILTER_TASKS),"'%s' button not found!".formatted(EButton.FILTER_TASKS))
                    .add(()->tc.button.isDisplayed(EButton.SEARCH_TASKS),"'%s' button not found!".formatted(EButton.SEARCH_TASKS))
                    .add(()->tc.button.isDisplayed(EButton.SORTED_BY),"'%s' button not found!".formatted(EButton.SORTED_BY));
            tc.addStepInfo("Filter, Sorted by : Call Id, Search Task, Split Task list domain, List of notifications option should be reflected",
                    "ok",tc.stepEvaluator.eval(),new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            WaitFor.condition(()->tc.button.isDisplayed(EButton.FILTER_TASKS),Duration.ofSeconds(10));
            tc.button.click(EButton.FILTER_TASKS);
            WaitFor.condition(()->tc.button.isDisplayed(EButton.QUICK_FILTER),Duration.ofSeconds(10));
            tc.button.click(EButton.QUICK_FILTER);
            WaitFor.condition(()->tc.modal.exists(EModal.QUICK_FILTER_MODAL),Duration.ofSeconds(5));
            if(!tc.checkbox.isChecked(ECheckBox.COMPLETED))
            {
            tc.checkbox.check(ECheckBox.COMPLETED);
            }
            tc.button.click(EButton.QUICK_FILTER);
            tc.spinner.waitForSpinner(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(5));
            tc.spinner.waitForSpinnerToDisappear(ESpinner.SCHEDULE_DATA_LOADER,Duration.ofSeconds(10));
            String expanded_Notification = tc.notification.getAllNotifications().get(2);
            WaitFor.specificTime(Duration.ofSeconds(1));
            tc.notification.expandActions("#2");
            tc.addStepInfo("Below options should be reflected for each notification 'notification', 'Edit', " +
                            "'Site Assigned Engineers', 'Show on Gantt', 'Show on Map', 'Add to Clipboard', " +
                            "'Show related tasks', 'Pin Task', 'Comment', 'Check Rules', 'Get Candidates', 'Schedule'," +
                            " 'Unschedule'" ,
                    true,
                    tc.notification.getAllActions().containsAll(List.of("Edit", "Site Assigned Engineers",
                            "Show on Gantt", "Show on Map", "Add to Clipboard", "Show Related Tasks", "Pin Task" ,
                            "Comment", "Check Rules", "Get Candidates", "Schedule", "Unschedule")),new ComparerOptions().takeScreenShotPlatform());


            //STEP 4
            tc.notification.performActions(EAction.ADD_TO_CLIPBOARD);
            tc.tab.select(ETab.CLIPBOARD);
            tc.addStepInfo("Notification should be reflected under Clipboard section",
                    true,tc.notification.getAllNotifications().contains(expanded_Notification),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}