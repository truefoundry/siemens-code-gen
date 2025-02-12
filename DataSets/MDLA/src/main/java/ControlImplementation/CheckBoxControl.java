package ControlImplementation;

import ControlImplementation.Interface.ICheckBoxControl;
import Enums.ECheckBox;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.*;
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


public class CheckBoxControl extends CoreInteractables implements ICheckBoxControl
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
                    List<WebElement> checkboxes = css.findControls(By.cssSelector("mat-checkbox"));

                    for (WebElement checkbox : checkboxes)
                    {
                        WebElement label = checkbox.findElement(By.cssSelector("label"));
                        if (label.getText().equals(name.getValue()))
                        {
                            yield checkbox;
                        }
                    }

                    TcLog.info("No matching checkbox label found for: " + name.getValue());
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
            TcLog.error("Unable to find wanted checkbox element!", e);
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
            res = new ArrayList<>(css.findControls(By.cssSelector("mat-checkbox")));
            res = res.stream().filter(WebElement::isDisplayed).toList();
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Staled root element of checkbox ... wait for refreshing");
            WaitFor.toBeFresh(root, Duration.ofSeconds(10));
            return this.getAll(root);
        }

        pm.stop();
        return res;
    }

    public boolean isChecked(ECheckBox checkBox)
    {
        WebElement ele = this.findControl(checkBox);
        if(ele != null)
        {
            String att = ele.getAttribute("class");
            return att.contains("checked");
        }
        return false;
    }

    public void check(ECheckBox checkBox)
    {
        try
        {
            if(isChecked(checkBox))
            {
                TcLog.info("Checkbox '%s' already checked".formatted(checkBox));
                return;
            }
            WebElement root = this.findControl(checkBox);
            if (root != null)
            {
                WebElement ele = root.findElement(By.cssSelector("input[type=\"checkbox\"]"));
                ele.click();
                TcLog.info("Checkbox '%s' has been checked".formatted(checkBox));
            }
        }
        catch (StaleElementReferenceException e)
        {
            TcLog.info("Element staled, Retying...");
            WaitFor.specificTime(Duration.ofSeconds(3));
            this.uncheck(checkBox);
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.error("Unable to check checkbox '%s'".formatted(checkBox));
        }
    }

    public void uncheck(ECheckBox checkBox)
    {
        try
        {
            if(!isChecked(checkBox))
            {
                TcLog.info("Checkbox '%s' already unchecked".formatted(checkBox));
                return;
            }
            WebElement root = this.findControl(checkBox);
            if (root != null)
            {
                WebElement ele = root.findElement(By.cssSelector("input[type=\"checkbox\"]"));
                ele.click();
                TcLog.info("Checkbox '%s' has been unchecked".formatted(checkBox));
            }
        }
        catch (StaleElementReferenceException e)
        {
            TcLog.info("Element staled, Retying...");
            WaitFor.specificTime(Duration.ofSeconds(3));
            this.check(checkBox);
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.error("Unable to uncheck checkbox '%s'".formatted(checkBox));
        }
    }

    public boolean exists(ECheckBox checkBox)
    {
        return this.findControl(checkBox) != null;
    }

    public boolean isDisabled(ECheckBox checkBox)
    {
        WebElement ele = this.findControl(checkBox);
        if (ele != null)
        {
            String classAttribute = ele.getAttribute("class");
            // Check if the class contains 'disabled', meaning the checkbox is disabled and cannot be checked
            return !classAttribute.contains("disabled");
        }
        return false;
    }
}
