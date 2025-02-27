package Admin;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC05_Admin_Menu_User_Management
{
    @Test
    void Admin_Menu_User_Management()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.ADMIN, "842", tc ->
        {
            //Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 2
            tc.button.click(EButton.ADMIN_ICON);
            WaitFor.condition(() -> tc.tile.exists(ETile.USER_MANAGEMENT));
            tc.addStepInfo("Page with 3 options for admins is displayed", true, tc.tile.exists(ETile.USER_MANAGEMENT),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            List<String> expectedTiles = List.of("User Management", "Escalation Criteria", "Admin Ruleset");
            List<String> actualTiles = List.of(tc.tile.getText(ETile.USER_MANAGEMENT), tc.tile.getText(ETile.ESCALATION_CRITERIA), tc.tile.getText(ETile.ADMIN_RULESET));
            tc.addStepInfo("Page contains tiles: User Management, Escalation Criteria, Admin Ruleset", true, expectedTiles.equals(actualTiles),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.tile.open(ETile.USER_MANAGEMENT);
            WaitFor.condition(() -> tc.table.exists(ETable.USERS_TABLE));
            tc.addStepInfo("User management page is displayed with list of all users", true, tc.table.exists(ETable.USERS_TABLE),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            String searchCriteria = "testUser"; // Replace with actual GID, part of User name or e-mail
            tc.edit.sendKeys(EEdit.SEARCH, searchCriteria);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.USERS_TABLE, EColumn.byIndex(1)).contains(searchCriteria));
            boolean userFound = tc.table.getItemsFromColumn(ETable.USERS_TABLE, EColumn.byIndex(1)).contains(searchCriteria);
            tc.addStepInfo("Only users matching search conditions are displayed", true, userFound,
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            tc.table.itemClick(ETable.USERS_TABLE, EColumn.byIndex(1), searchCriteria);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with user details, assigned roles and teams is displayed", true, tc.modal.exists(),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            tc.button.click(EButton.UPDATE_USER);
            WaitFor.condition(() -> tc.table.exists(ETable.USERS_TABLE));
            tc.addStepInfo("User management page is displayed users matching search conditions", true, tc.table.exists(ETable.USERS_TABLE),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```

