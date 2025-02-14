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
            tc.browser.start(WebDrv.CHROME, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 2
            tc.button.click(EButton.ADMIN);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Page with 3 options for admins is displayed", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            // Step 3
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(() -> tc.tile.exists(ETile.USER_MANAGEMENT), "User Management tile is not present")
                    .add(() -> tc.tile.exists(ETile.ESCALATION_CRITERIA), "Escalation Criteria tile is not present")
                    .add(() -> tc.tile.exists(ETile.ADMIN_RULESET), "Admin Ruleset tile is not present");
            tc.addStepInfo("""
                    Page contains tiles:
                    - User Management
                    - Escalation Criteria
                    - Admin Ruleset
                    """, "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            // Step 4
            tc.tile.open(ETile.USER_MANAGEMENT);
            WaitFor.condition(() -> tc.table.exists(ETable.USER_LIST));
            tc.addStepInfo("User management page is displayed with list of all users", true,
                    tc.table.exists(ETable.USER_LIST), new ComparerOptions().takeScreenShotPlatform());

            // Step 5
            String searchCriteria = "GID123";
            tc.edit.sendKeys(EEdit.USER_SEARCH, searchCriteria);
            WaitFor.condition(() -> tc.table.getItemsFromColumn(ETable.USER_LIST, EColumn.byIndex(1)).contains(searchCriteria));
            tc.addStepInfo("Only users matching search conditions are displayed", true,
                    true, new ComparerOptions().takeScreenShotPlatform());

            // Step 6
            tc.button.click(EButton.EDIT_USER);
            WaitFor.condition(tc.modal::exists);
            tc.addStepInfo("Popup with user details, assigned roles and teams is displayed", true,
                    tc.modal.exists(), new ComparerOptions().takeScreenShotPlatform());

            // Step 7
            tc.button.click(EButton.UPDATE_USER);
            WaitFor.condition(() -> tc.table.exists(ETable.USER_LIST));
            tc.addStepInfo("User management page is displayed users matching search conditions", true,
                    tc.table.exists(ETable.USER_LIST), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```