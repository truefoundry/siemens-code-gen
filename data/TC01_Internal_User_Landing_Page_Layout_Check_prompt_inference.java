```java
package InternalUser;

import CompositionRoot.IocBuilder;
import Enums.*;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

public class TC05_Landing_Page_Layout_Check
{
    @Test
    void Landing_Page_Layout_Check()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.INTERNAL_USER, "865", tc ->
        {
            // Step 1
            tc.browser.start(WebDrv.EDGE, ETestData.QA_ENV_URL, new CoreStartOptions());
            tc.browser.localLogin();
            WaitFor.condition(() -> tc.tile.exists(ETile.REPORT_AN_ISSUE));
            tc.addStepInfo("Landing page is Displayed", true, tc.button.exists(EButton.CONTACT),
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 2
            List<String> ribbonElements = tc.ribbon.getElements();
            tc.addStepInfo("""
                    Landing page top ribbon content is according screenshot and consists of:
                    - Contact icon
                    - Settings icon
                    - Notification bell icon
                    - Icon with name shortcut
                    - Name of logged in user
                    - Admin icon
                    """, true, ribbonElements.containsAll(List.of("Contact icon", "Settings icon", "Notification bell icon", 
                    "Icon with name shortcut", "Name of logged in user", "Admin icon")), 
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 3
            List<String> tileElements = tc.tile.getAllTileNames();
            tc.addStepInfo("""
                    Landing page tile content is according screenshot and consists of following tiles:
                    - Report an issue with an order or delivery
                    - Show me my Requests
                    - Question about an order or eSuport assistance
                    - Question about my Account
                    - Request Allocation or Saturday Delivery (SET Request)
                    """, true, tileElements.containsAll(List.of("Report an issue with an order or delivery", "Show me my Requests", 
                    "Question about an order or eSuport assistance", "Question about my Account", 
                    "Request Allocation or Saturday Delivery (SET Request)")), 
                    new ComparerOptions().takeScreenShotPlatform());

            // Step 4
            tc.tile.open(ETile.REPORT_AN_ISSUE);
            WaitFor.condition(() -> tc.page.getTitle().contains("Report an issue"));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, 
                    tc.page.getTitle().contains("Report an issue"), new ComparerOptions().takeScreenShotPlatform());

            // Step 5
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.SHOW_ME_MY_REQUESTS));
            tc.tile.open(ETile.SHOW_ME_MY_REQUESTS);
            WaitFor.condition(() -> tc.tab.exists(ETab.CREATED_REQUESTS));
            tc.addStepInfo("Page with dashboard with all requests created by the user is opened", true, 
                    tc.tab.isSelected(ETab.CREATED_REQUESTS), new ComparerOptions().takeScreenShotPlatform());

            // Step 6
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.QUESTION_ABOUT_ORDER));
            tc.tile.open(ETile.QUESTION_ABOUT_ORDER);
            WaitFor.condition(() -> tc.page.getTitle().contains("Question about an order"));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, 
                    tc.page.getTitle().contains("Question about an order"), new ComparerOptions().takeScreenShotPlatform());

            // Step 7
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.QUESTION_ABOUT_ACCOUNT));
            tc.tile.open(ETile.QUESTION_ABOUT_ACCOUNT);
            WaitFor.condition(() -> tc.page.getTitle().contains("Question about my Account"));
            tc.addStepInfo("Page with details for reporting an issue is opened", true, 
                    tc.page.getTitle().contains("Question about my Account"), new ComparerOptions().takeScreenShotPlatform());

            // Step 8
            tc.browser.navigateBack();
            WaitFor.condition(() -> tc.tile.exists(ETile.REQUEST_ALLOCATION));
            tc.tile.open(ETile.REQUEST_ALLOCATION);
            WaitFor.condition(() -> tc.browser.getUrl().contains("Sales Efficiency"));
            tc.addStepInfo("User is redirected to external Sales Efficiency tool page", true, 
                    tc.browser.getUrl().contains("Sales Efficiency"), new ComparerOptions().takeScreenShotPlatform());
        });
    }
}
```