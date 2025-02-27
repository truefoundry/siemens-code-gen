```java
package Admin;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class TC05_Admin_Menu_User_Management
{
    @Test
    void Admin_Menu_User_Management()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.ADMIN, "842", tc ->
        {
            // Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE);
            tc.addStepInfo("Landing page is displayed", true, tc.button.exists(EButton.CONTACT),
                                                  new ComparerOptions().takeScreenShotPlatform());

            // Step 2
            tc.button.click(EButton.ADMIN);
            WaitFor.condition(() -> tc.tile.exists(ETile.USER_MANAGEMENT));
            tc.addStepInfo("Page with options for admins is displayed", true, tc.tile.exists(ETile.USER_MANAGEMENT),
                                                  new ComparerOptions().takeScreenShotPlatform());

            // Step 3
            String[] expectedTiles = { "User Management", "Escalation Criteria", "Admin Ruleset" };
            List<String> actualTiles = tc.tile.getAllTilesName();
            tc.addStepInfo("Page contains tiles: User Management, Escalation Criteria, Admin Ruleset", true, 
                           expectedTiles.stream().allMatch(actualTiles::contains), 
                           new ComparerOptions().takeScreenShotPlatform());

            // Step 4
            tc.tile.open(ETile.USER_MANAGEMENT);
            WaitFor.condition(() -> tc.table.exists(ETable.APP_TABLE));
            tc.addStepInfo("User management page is displayed with list of all users", true, 
                           tc.table.exists(ETable.APP_TABLE), new ComparerOptions().takeScreenShotPlatform());

            // Step 5
            tc.edit.sendKeys(EEdit.SEARCH, "GID_or_User_name_or_email");
            WaitFor.condition(() -> tc.table.getTableRowCount(ETable.APP_TABLE) > 0);
            tc.addStepInfo("Only users matching search conditions are displayed", true, 
                           tc.table.getTableRowCount(ETable.APP_TABLE) > 0, 
                           new ComparerOptions().takeScreenShotPlatform());

            // Step 6
            tc.button.click(EButton.EDIT);
            WaitFor.condition(() -> tc.modal.exists());
            tc.addStepInfo("Popup with user details is displayed", true, 
                           tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            // Step 7
            tc.button.click(EButton.UPDATE_USER);
            WaitFor.condition(() -> !tc.modal.exists());
            tc.addStepInfo("User management page is displayed with matching users", true, 
                           tc.table.exists(ETable.APP_TABLE), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```