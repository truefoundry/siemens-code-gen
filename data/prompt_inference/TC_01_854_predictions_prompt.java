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

public class TC05_Landing_Page_Content_Check
{
    @Test
    void Landing_Page_Content_Check()
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
            List<String> topRibbonElements = tc.ribbon.getTopRibbonElements();
            tc.addStepInfo("""
                    Landing page top ribbon content is according screenshot and consists of:
                    - Contact icon
                    - Settings icon
                    - Notification bell icon
                    - Icon with name shortcut
                    - Name of logged in user
                    - Admin icon
                    """, true, !topRibbonElements.isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 3
            List<String> tiles = tc.tile.getAllTileNames();
            tc.addStepInfo("""
                    Landing page tile content is according screenshot and consists of following tiles:
                    - Report an issue with an order or delivery
                    - Show me my Requests
                    - Question about an order or eSuport assistance
                    - Question about my Account
                    - Request Allocation or Saturday Delivery (SET Request)
                    """, true, !tiles.isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //Step 4
            tc.tile.open(ETile.REPORT_AN_ISSUE);
            WaitFor.condition(() -> tc.edit.exists(EEdit.MOBILE));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, tc.progressBar.getNames().contains("Personal Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 5
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.SHOW_ME_All_REQUESTS));
            tc.tile.open(ETile.SHOW_ME_All_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.byIndex(0)));
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened", true, tc.tab.exists(ETab.byIndex(0)), new ComparerOptions().takeScreenShotPlatform());

            //Step 6
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.QUESTION_ABOUT_AN_ORDER));
            tc.tile.open(ETile.QUESTION_ABOUT_AN_ORDER);
            WaitFor.condition(() -> tc.edit.exists(EEdit.MOBILE));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, tc.progressBar.getNames().contains("Personal Information"), new ComparerOptions().takeScreenShotPlatform());

            //Step 7
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.QUESTION_ABOUT_MY_ACCOUNT));
            tc.tile.open(ETile.QUESTION_ABOUT_MY_ACCOUNT);
            WaitFor.condition(() -> tc.edit.exists(EEdit.MOBILE));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, tc.progressBar.getNames().contains("Personal Information"), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```