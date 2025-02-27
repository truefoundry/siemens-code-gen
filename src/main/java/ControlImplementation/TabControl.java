package ControlImplementation;

import ControlImplementation.Interface.ITabControl;
import Enums.ETab;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.*;
import fate.core.Interfaces.IInteractableNaming;
import fate.core.PerfMon.PerfMon;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TabControl extends CoreInteractables implements ITabControl
{
    CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);

    protected @Nullable WebElement findControl(IInteractableNaming name, By... rootBy)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        WebElement res;
        try
        {
            WebElement root = DomUtils.setRootElement(ModalControl.getSelector(), rootBy);

            res = switch(name.getType())
            {
                case INDEX -> this.getAll(DomUtils.getElementsSelector(root)).get(name.getIndex());
                case SELECTOR -> css.findControlWithRoot(name.getSelector(), root);
                case NAME -> this.getAll(DomUtils.getElementsSelector(root)).stream().filter(btn -> {
                    if(btn.getText().trim().equals(name.getValue()))
                    {
                        return true;
                    }
                    return false;
                }).findFirst().orElse(null);
            };

            pm.stop();
            return res;
        }
        catch(StaleElementReferenceException e)
        {
            pm.abort();
            WaitFor.specificTime(Duration.ofSeconds(1));
            return this.findControl(name);
        }
        catch(NullPointerException | NumberFormatException | IndexOutOfBoundsException e)
        {
            pm.abort();
            TcLog.warning("Unable to find control '%s'! ".formatted(name.getValue()) + e);
        }

        pm.abort();
        return null;
    }

    private List<WebElement> getAll(By root)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        List<WebElement> res = new ArrayList<>();
        try
        {
            res.addAll(css.findControls(By.cssSelector("div[role=\"tab\"]")));
            res = res.stream().filter(WebElement::isDisplayed).toList();
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Staled root element of tab ... wait for refreshing");
            WaitFor.toBeFresh(root, Duration.ofSeconds(10));
            return this.getAll(root);
        }

        pm.stop();
        return res;
    }

    public void select(ETab name)
    {
        try
        {
            WebElement ele = this.findControl(name);
            if(ele != null)
            {
                if(ele.getAttribute("aria-selected").equalsIgnoreCase("true"))
                {
                    TcLog.info("Tab '%s' already selected".formatted(name));
                }
                new CoreMouseControl().moveOverControl(ele);
                ele.click();
                TcLog.action("Tab '%s' has been selected".formatted(name));
                BrowserControl.waitForLoadingIndicator();
            }
            else
            {
                TcLog.error("Unable to find tab '%s'".formatted(name));
            }
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Staled root element of tab ... wait for refreshing");
            WaitFor.specificTime(Duration.ofSeconds(3));
            this.select(name);
        }
        catch (NotFoundException | NullPointerException | ElementClickInterceptedException e)
        {
            TcLog.error("Unable to select Tab'%s'".formatted(name));
        }
    }

    public boolean isSelected(ETab name)
    {
        try
        {
            WebElement ele = this.findControl(name);
            if(ele != null)
            {
                return ele.getAttribute("aria-selected").equalsIgnoreCase("true");
            }
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Staled root element of tab ... wait for refreshing");
            WaitFor.specificTime(Duration.ofSeconds(3));
            this.isSelected(name);
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.error("Unexpected Exception");
        }
        return false;
    }

    public List<String> getAllTabsNames()
    {
        WebElement root = DomUtils.setRootElement(ModalControl.getSelector());
        List<String> names = new ArrayList<>();
        try
        {
            List<WebElement> elements = this.getAll(DomUtils.getElementsSelector(root));
            for(WebElement ele : elements)
            {
                names.add(ele.getText());
            }
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Staled root element of tab ... wait for refreshing");
            WaitFor.specificTime(Duration.ofSeconds(3));
            this.getAllTabsNames();
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.error("Unable to find tabs");
        }
        return names;
    }

    public boolean exists(ETab tab)
    {
        return this.findControl(tab) != null;
    }
}
