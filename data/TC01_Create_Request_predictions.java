To create a Java code for automated testing based on the provided steps, we can use Selenium WebDriver for web automation. Below is a sample Java code that outlines how you might implement these steps. Note that this code assumes you have the necessary setup for Selenium WebDriver and that you have access to the required web elements on the Digital Customer Portal.

```java
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

public class DigitalCustomerPortalTest {

    public static void main(String[] args) {
        // Set up WebDriver (Ensure ChromeDriver path is set correctly)
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        WebDriver driver = new ChromeDriver();

        try {
            // Step 1: Log in to Digital Customer Portal
            driver.get("https://digitalcustomerportal.com/login");
            driver.findElement(By.id("username")).sendKeys("your_username");
            driver.findElement(By.id("password")).sendKeys("your_password");
            driver.findElement(By.id("loginButton")).click();

            // Step 2: Click on tile "Report an issue with an order or delivery"
            driver.findElement(By.id("reportIssueTile")).click();

            // Step 3: Enter data into fields
            driver.findElement(By.id("mobile")).sendKeys("1234567890");
            driver.findElement(By.id("country")).sendKeys("USA");
            driver.findElement(By.id("state")).sendKeys("California");
            new Select(driver.findElement(By.id("preferredLanguage"))).selectByVisibleText("English");

            // Step 4: Click "Next" button
            driver.findElement(By.id("nextButton")).click();

            // Step 5: Enter data into fields
            new Select(driver.findElement(By.id("requestType"))).selectByVisibleText("Order Issue");
            driver.findElement(By.id("subject")).sendKeys("Subject Text");
            driver.findElement(By.id("reason")).sendKeys("Reason Text");
            driver.findElement(By.id("classification")).sendKeys("Classification Text");
            driver.findElement(By.id("siemensTeam")).sendKeys("Team Name");
            driver.findElement(By.id("soldTo")).sendKeys("Sold To Info");
            driver.findElement(By.id("shipTo")).sendKeys("Ship To Info");
            driver.findElement(By.id("orderNumber")).sendKeys("12345");
            driver.findElement(By.id("addOrderNumber")).click();
            driver.findElement(By.id("additionalEmail")).sendKeys("additional@example.com");
            driver.findElement(By.id("addEmail")).click();

            // Step 6: Attach files
            driver.findElement(By.id("browseFiles")).sendKeys("path/to/file1");
            // Step 7: Drag and drop file
            // Note: Selenium does not support drag and drop directly, you might need a workaround or third-party library

            // Step 8: Click "Next" button
            driver.findElement(By.id("nextButton")).click();

            // Step 9: Verify fields (Assuming verification is checking if fields are displayed)
            assert driver.findElement(By.id("fieldToVerify")).isDisplayed();

            // Step 10: Check checkboxes
            driver.findElement(By.id("informationalOnly")).click();
            // Or
            driver.findElement(By.id("referOutRequest")).click();

            // Step 11: Click "Save + Submit" button
            driver.findElement(By.id("saveSubmitButton")).click();

            // Step 12: Click "Yes, submit" button
            driver.findElement(By.id("yesSubmitButton")).click();

            // Step 13: Click "Go back to the dashboard" button
            driver.findElement(By.id("goBackDashboardButton")).click();

            // Step 14: Verify notification not sent (Assuming checking absence of notification element)
            assert driver.findElements(By.id("notification")).isEmpty();

            // Step 15: Click on tile "Show me my Requests"
            driver.findElement(By.id("showRequestsTile")).click();

            // Step 16: Click on tab "Created Requests"
            driver.findElement(By.id("createdRequestsTab")).click();

            // Step 17: Enter request ID or part of Subject in search field
            driver.findElement(By.id("searchField")).sendKeys("Request ID or Subject");

            // Step 18: Click on filtered request
            driver.findElement(By.id("filteredRequest")).click();

            // Step 19: Verify content of fields
            assert driver.findElement(By.id("fieldToVerify")).isDisplayed();

            // Step 20: Enter text, add attachment, and submit new comment
            driver.findElement(By.id("commentField")).sendKeys("New comment");
            driver.findElement(By.id("addAttachment")).sendKeys("path/to/attachment");
            driver.findElement(By.id("submitCommentButton")).click();

            // Step 21-23: Verify content in various tabs
            driver.findElement(By.id("requestInformationTab")).click();
            assert driver.findElement(By.id("fieldToVerify")).isDisplayed();
            driver.findElement(By.id("requestorInformationTab")).click();
            assert driver.findElement(By.id("fieldToVerify")).isDisplayed();
            driver.findElement(By.id("requestOverviewTab")).click();
            assert driver.findElement(By.id("fieldToVerify")).isDisplayed();

            // Step 24: Click on button "Back"
            driver.findElement(By.id("backButton")).click();

            // Step 25-27: Delete request
            driver.findElement(By.id("createdRequestsTab")).click();
            driver.findElement(By.id("yourRequest")).click();
            driver.findElement(By.id("requestInformationTab")).click();
            driver.findElement(By.id("deleteButton")).click();
            driver.findElement(By.id("confirmButton")).click();

            // Step 28: Verify request deletion
            driver.findElement(By.id("showRequestsTile")).click();
            driver.findElement(By.id("createdRequestsTab")).click();
            // Verify request is deleted, e.g., by checking absence of request element
            assert driver.findElements(By.id("yourRequest")).isEmpty();

        } finally {
            // Close the browser
            driver.quit();
        }
    }
}
```

This code provides a basic structure for automating the test steps using Selenium WebDriver. You will need to adjust the element locators (`By.id`, `By.xpath`, etc.) based on the actual HTML structure of the Digital Customer Portal. Additionally, ensure that the WebDriver setup and file paths are correctly configured for your environment.