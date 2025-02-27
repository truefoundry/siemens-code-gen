package ControlImplementation;

import CompositionRoot.IocBuilder;
import Enums.EButton;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.CoreKeyboardControl;
import fate.core.ControlImplementations.WaitFor;
import fate.core.PerfMon.PerfMon;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationControl
{
    CoreCssControl css = IocBuilder.getContainer().getComponent(CoreCssControl.class);

    protected @Nullable WebElement findControl(String requestId) {
        PerfMon pm = new PerfMon();
        WebElement root = Objects.requireNonNull(css.findControl(By.cssSelector("html")));

        pm.watch(Duration.ofSeconds(2));

        try
        {
            List<WebElement> elements = getAll(root);

            for (WebElement element : elements)
            {
                try
                {
                    WebElement ele = element.findElement(By.tagName("a"));

                    String linkText = ele.getText().trim();

                    if (linkText.equalsIgnoreCase(requestId))
                    {
                        return element;
                    }
                }
                catch (NoSuchElementException e)
                {
                    TcLog.info("No <a> tag found in this element, skipping...");
                }
            }

            TcLog.info("No element found with requestId: '%s'".formatted(requestId));
        }
        catch (StaleElementReferenceException e)
        {
            TcLog.info("Wanted element got stale ... waiting to refresh");
            WaitFor.specificTime(Duration.ofSeconds(2));
            return this.findControl(requestId);
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.error("Unable to find the wanted menu element!", e);
        }

        return null;
    }


    private List<WebElement> getAll(WebElement root)
    {
        List<WebElement> elements = new ArrayList<>();
        try
        {
             elements.addAll(css.findControlsWithRoot(By.cssSelector("app-notifications-card[class='notifications-card-items']"), root)
                     .stream().filter(WebElement::isDisplayed).toList());
        }
        catch (NullPointerException | NotFoundException e)
        {
            TcLog.error("");
        }
        return elements;
    }

    public boolean exists(String requestId)
    {
        return this.findControl(requestId) != null;
    }

    public void openOrClose(boolean decide)
    {
        ButtonControl btn = IocBuilder.getContainer().getComponent(ButtonControl.class);
        By notificationsContainer = By.cssSelector("div[class*='notifications-card-container']");
        CoreKeyboardControl key = new CoreKeyboardControl();
        try
        {
            if (decide)
            {
                btn.click(EButton.NOTIFICATIONS);
                WaitFor.toBeVisible(notificationsContainer);
                TcLog.info("Notifications opened successfully.");
            }
            else
            {
                key.pressKey(Keys.ESCAPE);
                WaitFor.specificTime(Duration.ofMillis(500));
                TcLog.info("Notifications are closed.");
            }
        } catch (Exception e)
        {
            TcLog.error("Error occurred while processing openOrClose action.", e);
        }
    }

}
