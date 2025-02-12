package ControlImplementation;

import ControlImplementation.Interface.IButtonControl;
import Enums.EButton;
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

public class ButtonControl extends CoreInteractables implements IButtonControl
{
    private final WebElement rootElement = null;
    CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);


    @Override
    protected @Nullable WebElement findControl(IInteractableNaming name, By... rootBy)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        WebElement res;
        try
        {
            WebElement root = rootElement == null ?DomUtils.setRootElement(ModalControl.getSelector(), rootBy) : rootElement;

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
            res.addAll(css.findControls(By.cssSelector("div[class*='button']")));
            res.addAll(css.findControlsWithRoot(By.cssSelector("button"), root));
            res = res.stream().filter(WebElement::isDisplayed).toList();
            TcLog.debug("%s list of buttons elements found.".formatted(res.size()));
            TcLog.debug("Buttons found: " + res.stream().map(WebElement::getText).toList());
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

    public void click(EButton button)
    {
        try
        {
            WebElement ele = this.findControl(button);
            if (ele != null)
            {
                new CoreMouseControl().moveOverControl(ele);
                ele.click();
                TcLog.info("Button '%s' has been clicked".formatted(button));
                BrowserControl.waitForLoadingIndicator();
            }
            else
            {
                TcLog.error("Unable to find the button '%s'".formatted(button));
            }
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Element staled, Retrying...");
            WaitFor.specificTime(Duration.ofSeconds(1));
            this.click(button);
        }
        catch (ElementClickInterceptedException | NotFoundException | NullPointerException e)
        {
            TcLog.error("Unable to perform click on button '%s'".formatted(button));
        }
    }

    public boolean exists(EButton button)
    {
        return this.findControl(button) != null;
    }
}
