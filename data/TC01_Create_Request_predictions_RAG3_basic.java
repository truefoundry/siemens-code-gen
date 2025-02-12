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
    void User_Management()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.ADMIN, "842", tc ->
        {
            // Step 1
            tc.browser.start(WebDrv.FIREFOX, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is displayed", true, tc.tile.exists(ETile.SHOW_ME_All_REQUESTS),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 2
            tc.button.click(EButton.ADMIN);
            WaitFor.condition(() -> tc.tile.exists(ETile.USER_MANAGEMENT));
            tc.addStepInfo("Page with 3 options for admins is displayed", true, tc.tile.exists(ETile.ESCALATION_CRITERIA),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 3
            tc.stepEvaluator.reset();
            boolean hasUserManagement = tc.tile.exists(ETile.USER_MANAGEMENT);
            boolean hasEscalationCriteria = tc.tile.exists(ETile.ESCALATION_CRITERIA);
            boolean hasAdminRuleset = tc.tile.exists(ETile.ADMIN_RULESET);
            tc.stepEvaluator
                    .add(() -> hasUserManagement, "User Management tile is not present")
                    .add(() -> hasEscalationCriteria, "Escalation Criteria tile is not present")
                    .add(() -> hasAdminRuleset, "Admin Ruleset tile is not present");
            tc.addStepInfo("Page contains tiles: User Management, Escalation Criteria, Admin Ruleset", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            // Step 4
            tc.tile.open(ETile.USER_MANAGEMENT);
            WaitFor.condition(() -> tc.table.exists(ETable.USER_LIST));
            tc.addStepInfo("User management page is displayed with list of all users", true,
                    tc.table.exists(ETable.USER_LIST), new ComparerOptions().takeScreenShotPlatform());

            // Step 5
            tc.edit.sendKeys(EEdit.SEARCH_USER, "testUser");
            WaitFor.condition(() -> !tc.table.getItemsFromColumn(ETable.USER_LIST, EColumn.byName("User Name")).isEmpty());
            tc.addStepInfo("Only users matching search conditions are displayed", true,
                    !tc.table.getItemsFromColumn(ETable.USER_LIST, EColumn.byName("User Name")).isEmpty(),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 6
            tc.table.clickButtonInRow(ETable.USER_LIST, EColumn.byName("Edit"), "testUser");
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with user details, assigned roles and teams is displayed", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            // Step 7
            tc.button.click(EButton.UPDATE_USER);
            WaitFor.condition(() -> tc.table.exists(ETable.USER_LIST));
            tc.addStepInfo("User management page is displayed with users matching search conditions", true,
                    tc.table.exists(ETable.USER_LIST), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```
This code implements an automated test for "Admin Menu - User Management" in a digital customer portal using Java and Selenium.