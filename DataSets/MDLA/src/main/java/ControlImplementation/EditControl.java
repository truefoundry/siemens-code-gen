package ControlImplementation;

import CompositionRoot.IocBuilder;
import ControlImplementation.Interface.IEditControl;
import Enums.EEdit;
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

public class EditControl extends CoreInteractables implements IEditControl
{
    CoreCssControl css = IocBuilder.getContainer().getComponent(CoreCssControl.class);

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
                    List<WebElement> labels = css.findControls(By.cssSelector("label[class*=\"mdc-floating-label\"]"));

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
            res = new ArrayList<>(css.findControls(By.cssSelector("mat-form-field")));
            res = res.stream().filter(WebElement::isDisplayed).toList();
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Staled root element of button ... wait for refreshing");
            WaitFor.toBeFresh(root, Duration.ofSeconds(10));
            return this.getAll(root);
        }

        pm.stop();
        return res;
    }

    public void sendKeys(EEdit name, String text, boolean... clear)
    {

        WaitFor.specificTime(Duration.ofSeconds(1));
        BrowserControl.waitForLoadingIndicator();
    }

    public boolean isEditable(EEdit name)
    {
        try
        {
            WebElement ele = this.findControl(name);
            if(ele != null)
            {
                return ele.isDisplayed() && ele.isEnabled();
            }
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.info("Unable to find Edit '%s'".formatted(name));
        }
        return false;
    }

    public boolean exists(EEdit edit){return this.findControl(edit) != null;}

    public String getValue(EEdit name)
    {
        String val = "";
        try
        {
            WebElement ele = this.findControl(name);
            if(ele != null)
            {
                val = ele.getAttribute("value");
                TcLog.info("From edit box '%s' found '%s'".formatted(name, val));
            }
            else
            {
                TcLog.warning("Failed to get value.");
            }
        }
        catch (StaleElementReferenceException e)
        {
            TcLog.info("Element staled, Retrying", e);
            WaitFor.specificTime(Duration.ofSeconds(2));
            this.getValue(name);
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.info("Unable to find Edit '%s'".formatted(name));
        }
        return val;
    }
}
