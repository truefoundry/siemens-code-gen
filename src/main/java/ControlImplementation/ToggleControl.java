package ControlImplementation;

import ControlImplementation.Interface.IToggleControl;
import Enums.EToggle;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.CoreInteractables;
import fate.core.ControlImplementations.DomUtils;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Interfaces.IInteractableNaming;
import fate.core.PerfMon.PerfMon;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ToggleControl extends CoreInteractables implements IToggleControl
{
    CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);

    protected @Nullable WebElement findControl(IInteractableNaming name, By... rootElement)
    {
        PerfMon pm = new PerfMon();
        WebElement root = Objects.requireNonNull(css.findControl(By.cssSelector("html")));

        pm.watch(Duration.ofSeconds(2));

        try
        {
            return switch(name.getType())
            {
                case INDEX -> this.getAll(DomUtils.getElementsSelector(root)).get(Integer.parseInt(name.getValue()));
                case SELECTOR -> css.findControl(name.getSelector());
                case NAME ->
                {
                    List<WebElement> labels = css.findControls(By.cssSelector("div[class=\"settings-grid\"]>div>h3"));

                    for (WebElement label : labels)
                    {
                        if (label.getText().equals(name.getValue()))
                        {
                            yield label;
                        }
                    }

                    TcLog.info("No matching label found for: " + name.getValue());
                    yield null;
                }
            };
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Wanted element got stale ... wait for refreshing");
            WaitFor.specificTime(Duration.ofSeconds(2));
            return this.findControl(name);
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.error("Unable to find wanted menu element!", e);
        }

        return null;
    }

    private List<WebElement> getAll(By root)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        List<WebElement> res;
        try
        {
            res = new ArrayList<>(css.findControls(By.cssSelector("mat-slide-toggle")));
            res = res.stream().filter(WebElement::isDisplayed).toList();
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Staled root element of toggle ... wait for refreshing");
            WaitFor.toBeFresh(root, Duration.ofSeconds(10));
            return this.getAll(root);
        }

        pm.stop();
        return res;
    }

    public boolean isChecked(EToggle toggle)
    {
        WebElement ele = this.findControl(toggle);
        if(ele != null)
        {
            String att = ele.getAttribute("class");
            return att.contains("checked");
        }
        return false;
    }

    public void check(EToggle toggle)
    {
        try
        {
            if(isChecked(toggle))
            {
                TcLog.info("Toggle '%s' already checked".formatted(toggle));
                return;
            }
            WebElement ele = this.findControl(toggle);
            if (ele != null)
            {
                ele.findElement((By.cssSelector("button[role='switch']"))).click();
                TcLog.info("Toggle '%s' has been checked".formatted(toggle));
                BrowserControl.waitForLoadingIndicator();
            }
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.error("Unable to check toggle '%s'".formatted(toggle));
        }
    }

    public void uncheck(EToggle toggle)
    {
        try
        {
            if(!isChecked(toggle))
            {
                TcLog.info("Toggle '%s' already unchecked".formatted(toggle));
                return;
            }
            WebElement ele = this.findControl(toggle);
            if (ele != null)
            {
                ele.findElement(By.cssSelector("button[role='switch']")).click();
                TcLog.info("Toggle '%s' has been unchecked".formatted(toggle));
                BrowserControl.waitForLoadingIndicator();
            }
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.error("Unable to uncheck toggle '%s'".formatted(toggle));
        }
    }

    public boolean exists(EToggle toggle)
    {
        return this.findControl(toggle) != null;
    }
}
