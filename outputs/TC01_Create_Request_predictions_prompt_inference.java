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
            tc.button.click(EButton.ADMIN);
            WaitFor.condition(() -> tc.page.exists(EPage.ADMIN_OPTIONS));
            tc.addStepInfo("Page with 3 options for admins is displayed", true, tc.page.exists(EPage.ADMIN_OPTIONS),
                    new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            List<String> expectedTiles = List.of("User Management", "Escalation Criteria", "Admin Ruleset");
            List<String> actualTiles = tc.tile.getAllTilesName();
            boolean areTilesCorrect = actualTiles.containsAll(expectedTiles);
            tc.addStepInfo("Page contains tiles: - User Management - Escalation Criteria - Admin Ruleset",
                    true, areTilesCorrect, new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.tile.open(ETile.USER_MANAGEMENT);
            WaitFor.condition(() -> tc.page.exists(EPage.USER_MANAGEMENT));
            tc.addStepInfo("User management page is displayed with list of all users", true,
                    tc.page.exists(EPage.USER_MANAGEMENT), new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            String searchQuery = "testUser"; // Example search query
            tc.edit.sendKeys(EEdit.SEARCH_USER, searchQuery);
            WaitFor.condition(() -> tc.table.exists(ETable.USER_TABLE));
            boolean isSearchCorrect = tc.table.getItemsFromColumn(ETable.USER_TABLE, EColumn.USER_NAME)
                    .stream().anyMatch(name -> name.contains(searchQuery));
            tc.addStepInfo("Only users matching search conditions are displayed", true,
                    isSearchCorrect, new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            tc.table.clickButtonInRow(ETable.USER_TABLE, EButton.EDIT, 0); // Click edit on the first user in the list
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with user details, assigned roles and teams is displayed", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            tc.button.click(EButton.UPDATE_USER);
            WaitFor.condition(() -> tc.page.exists(EPage.USER_MANAGEMENT));
            tc.addStepInfo("User management page is displayed users matching search conditions", true,
                    tc.page.exists(EPage.USER_MANAGEMENT), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```