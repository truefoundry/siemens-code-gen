package Admin;

import CompositionRoot.IocBuilder;
import ControlImplementation.BrowserControl;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.util.List;

public class TC05_Admin_Menu_User_Management {

    @Test
    void Admin_Menu_User_Management() {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.ADMIN, "842", tc -> {
            // Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 2
            tc.button.click(EButton.ADMIN);
            WaitFor.condition(() -> tc.tile.exists(ETile.USER_MANAGEMENT));
            tc.addStepInfo("Page with 3 options for admins is displayed", true, tc.tile.exists(ETile.USER_MANAGEMENT),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 3
            List<String> expectedTiles = List.of("User Management", "Escalation Criteria", "Admin Ruleset");
            List<String> actualTiles = tc.tile.getAllTilesName();
            boolean areEqual = expectedTiles.size() == actualTiles.size() &&
                    expectedTiles.stream().allMatch(item -> Collections.frequency(expectedTiles, item) == Collections.frequency(actualTiles, item));
            tc.addStepInfo("""
                    Page contains tiles:
                    User Management
                    Escalation Criteria
                    Admin Ruleset
                    """, true, areEqual, new ComparerOptions().takeScreenShotPlatform());

            // Step 4
            tc.tile.open(ETile.USER_MANAGEMENT);
            WaitFor.condition(() -> tc.tab.exists(ETab.USERS));
            tc.addStepInfo("User management page is displayed with list of all users", true, tc.tab.exists(ETab.USERS),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 5
            final String searchString = "testUser"; // Replace with actual search string
            tc.edit.sendKeys(EEdit.SEARCH, searchString);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.USER_TABLE, EColumn.byIndex(1)).contains(searchString));
            boolean isUserFound = tc.table.getItemsFromColumn(ETable.USER_TABLE, EColumn.byIndex(1)).contains(searchString);
            tc.addStepInfo("Only users matching search conditions are displayed", true, isUserFound,
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 6
            tc.table.itemClick(ETable.USER_TABLE, EColumn.byIndex(1), searchString);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with user details, assigned roles and teams is displayed", true, tc.modal.exists(),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 7
            tc.button.click(EButton.UPDATE_USER);
            WaitFor.condition(() -> tc.tab.exists(ETab.USERS));
            tc.addStepInfo("User management page is displayed users matching search conditions", true, tc.tab.exists(ETab.USERS),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}