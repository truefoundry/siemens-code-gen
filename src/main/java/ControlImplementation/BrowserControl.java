package ControlImplementation;

import CompositionRoot.IocBuilder;
import Enums.EActions;
import Enums.EButton;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreBrowserControl;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.CoreStartOptions;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Interfaces.ITestData;
import fate.core.PerfMon.PerfMon;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

public class BrowserControl extends CoreBrowserControl
{
    static CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
    ButtonControl btn = CoreIocBuilder.getContainer().getComponent(ButtonControl.class);

    public WebDriver getDriver()
    {
        return drv;
    }

    public void login(ITestData user)
    {
        WaitFor.condition(()-> this.getCurrentURL().contains("siemens-healthineers.com/login"), Duration.ofSeconds(20));

        By ssoLink = By.cssSelector("a[id='loginWithShsAad']");
        WaitFor.toBeVisible(ssoLink);
        Objects.requireNonNull(css.findControl(By.cssSelector("a[id='loginWithShsAad']"))).click();
        TcLog.info("Clicked on the 'Company Single Sign-On' link.");
        WaitFor.condition(()-> this.getCurrentURL().contains("login.microsoftonline.com"));
        WaitFor.toBeVisible(By.cssSelector("img[class*='logo'i]"));
        css.sendKeys(By.cssSelector("input[name='loginfmt']"), user.getName());
        css.click(EButton.MS_LOGIN_NEXT_BTN);
        TcLog.action("Clicked on '%s'.".formatted(EButton.MS_LOGIN_NEXT_BTN));
        WaitFor.toBeVisible(By.cssSelector("input[name='passwd']"));
        css.sendKeys(By.cssSelector("input[name='passwd']"), user.getPass());
        TcLog.info("Password has been set");
        css.click(EButton.MS_SIGN_IN);
        TcLog.action("Clicked on '%s'.".formatted(EButton.MS_SIGN_IN));
        WaitFor.toBeVisible(EButton.MS_YES.getSelector());
        css.click(EButton.MS_YES);
        TcLog.action("Clicked on '%s'.".formatted(EButton.MS_YES));
        WaitFor.specificTime(Duration.ofSeconds(10));
    }

    public UUID start(WebDrv drv, String url, ITestData user) throws InterruptedException
    {
        UUID id = this.start(drv, url, new CoreStartOptions().startIncognito());
        this.login(user);
        return id;
    }

    @Override
    public void localLogin()
    {
        WaitFor.condition(()-> this.getCurrentURL().contains("siemens-healthineers.com/login"), Duration.ofSeconds(20));

        By ssoLink = By.cssSelector("a[id='loginWithShsAad']");
        WaitFor.toBeVisible(ssoLink);
        Objects.requireNonNull(css.findControl(By.cssSelector("a[id='loginWithShsAad']"))).click();
        TcLog.info("Clicked on the 'Company Single Sign-On' link.");
        WaitFor.condition(()-> css.findControl(By.cssSelector("div[class='introduction']")) != null, Duration.ofSeconds(30));
        TcLog.action("logged in successfully");
        WaitFor.specificTime(Duration.ofSeconds(10));
    }

    public void externalUserLogin(ITestData user)
    {
        WebDriver driver = IocBuilder.getContainer().getComponent(WebDriver.class);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMinutes(1));
        Actions actions = new Actions(driver);

        try
        {
            // Handle cookie consent
            WebElement cookieConsent = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button[id*='OptinAllowAll']")));
            cookieConsent.click();

            // Click Login button
            WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(text(), 'Login')]")));
            BrowserControl.waitForLoadingIndicator(Duration.ofMinutes(3));
            WaitFor.specificTime(Duration.ofMillis(1500));
            loginLink.click();

            // Enter username
            WebElement next = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("button[id='btn-next']")));
            actions.sendKeys(user.getName()).perform();
            next.click();

            // Enter password
            WebElement login = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("button[id='btn-login']")));
            actions.sendKeys(user.getPass()).perform();
            WaitFor.specificTime(Duration.ofMillis(1500));
            login.click();

            // Wait for and click 'Shop Now'
            WebElement shopNowButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Shop Now')]")));
            BrowserControl.waitForLoadingIndicator(Duration.ofMinutes(3));
            WaitFor.specificTime(Duration.ofMillis(1500));
            shopNowButton.click();


            // Navigate to user details page
            WebElement userDetailsLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("a[href=\"/SIEMENS/digicare/sendUserDetails\"]>img")));
            BrowserControl.waitForLoadingIndicator(Duration.ofMinutes(3));
            WaitFor.specificTime(Duration.ofMillis(1500));
            userDetailsLink.click();

            // Wait for page load
            BrowserControl.waitForLoadingIndicator(Duration.ofMinutes(3));
            super.switchToTab(1);
            WaitFor.specificTime(Duration.ofSeconds(10));
        }
        catch(Exception e)
        {
            TcLog.error("Login failed!", e);
        }
    }

    public String getActiveUser(EActions actions)
    {
        try
        {
            String action = actions.getAction().toLowerCase();
            WebElement ele;

            switch (action)
            {
                case "icon":
                    ele = Objects.requireNonNull(css.findControl(By.cssSelector("div[class='avatar']")));
                    return ele.getText();

                case "username":
                    ele = Objects.requireNonNull(css.findControl(By.cssSelector("div[class*='user_info']")));
                    return ele.getText();

                default:
                    TcLog.info("Unsupported action: " + actions.getAction());
                    break;
            }
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.info("Unable to find active user");
        }
        return "";
    }

    public String getPageTitle()
    {
        try
        {
            WebElement txt = Objects.requireNonNull(css.findControl(By.cssSelector("div[class*='introduction']")));
            return txt.getText().replace("\n", " ").trim();
        }
        catch (StaleElementReferenceException e)
        {
            TcLog.info("Element staled, Retrying...");
            WaitFor.specificTime(Duration.ofSeconds(2));
            this.getPageTitle();
        }
        catch (NullPointerException | NotFoundException e)
        {
            TcLog.error("Unable to find page title");
        }
        return "";
    }

    public String getMessage()
    {
        By ele = By.cssSelector("div[id*=\"mat-snack-bar-container\"]");
        WaitFor.toBeVisible(ele, Duration.ofSeconds(10));
        WebElement txtEle = css.findControl(ele);
        if (txtEle != null)
        {
            return txtEle.getText();
        }
        return "";
    }

    public void closePopUp()
    {
        try
        {
            By ele = By.cssSelector("div[id*=\"mat-snack-bar-container\"]");
            WaitFor.toBeVisible(ele, Duration.ofSeconds(10));
            WebElement root = css.findControl(ele);
            if(root != null)
            {
                root.findElement(By.cssSelector("div[class=\"icon-Close\"]")).click();
                TcLog.info("Popup closed");
            }
            else
            {
                TcLog.error("No popup found");
            }
        }
        catch (StaleElementReferenceException e)
        {
            TcLog.info("Element staled, retying...");
            WaitFor.specificTime(Duration.ofSeconds(2));
            this.closePopUp();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void waitForLoadingIndicator(Duration... dur)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(5));
        try
        {
            TcLog.info("Waiting for Loading Indicator...");
            WaitFor.disposal(By.cssSelector("mat-spinner[role=\"progressbar\"]"), dur.length > 0 ? dur[0] : Duration.ofMinutes(1));
            WaitFor.disposal(By.cssSelector("div[class='innerSec_spinner']"), dur.length > 0 ? dur[0] : Duration.ofMinutes(1));
            WaitFor.readyState();
        }
        catch(StaleElementReferenceException e)
        {
            WaitFor.specificTime(Duration.ofMillis(500));
            waitForLoadingIndicator();
        }
        catch (NullPointerException e)
        {
            TcLog.error("Unable to find loader");
        }
        pm.stop();
    }

}
