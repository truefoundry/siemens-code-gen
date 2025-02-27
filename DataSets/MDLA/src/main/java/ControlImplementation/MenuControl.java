package ControlImplementation;

import CompositionRoot.IocBuilder;
import ControlImplementation.Interface.IMenuControl;
import Enums.EMenu;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.CoreInteractables;
import fate.core.ControlImplementations.DomUtils;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Interfaces.IInteractableNaming;
import fate.core.PerfMon.PerfMon;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuControl extends CoreInteractables implements IMenuControl
{
    CoreCssControl css = IocBuilder.getContainer().getComponent(CoreCssControl.class);

    @Override
    protected @Nullable WebElement findControl(IInteractableNaming name, By... rootElement)
    {
        PerfMon pm = new PerfMon();
        WebElement res = null;
        WebElement root = Objects.requireNonNull(css.findControl(By.cssSelector("html")));

        pm.watch(Duration.ofSeconds(2));

        try
        {
            res = switch(name.getType())
            {
                case INDEX -> this.getAll(DomUtils.getElementsSelector(root)).get(Integer.parseInt(name.getValue()));
                case SELECTOR -> css.findControl(name.getSelector());
                case NAME ->
                {
                    // name not implemented
                    yield null;
                }
            };
            pm.stop();
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Wanted element got stale ... wait for refreshing");
            WaitFor.specificTime(Duration.ofSeconds(2));
            return this.findControl(name);
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.error("Unable to found wanted menu element!", e);
        }

        return res;
    }

    private List<WebElement> getAll(By root)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        List<WebElement> res;
        try
        {
            res = new ArrayList<>(css.findControls(By.cssSelector("div[aria-haspopup*='menu']")));
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

    public boolean exists(EMenu name)
    {
        return this.findControl(name) != null;
    }

    public void select(EMenu name)
    {
        try
        {
            WebElement ele = this.findControl(name);
            if (ele != null)
            {
                ele.click();
                TcLog.info("Menu '%s' has been clicked".formatted(name));
            }
            else
            {
                TcLog.error("Unable to find the Menu '%s'".formatted(name));
            }
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Element staled, Retrying...");
            WaitFor.specificTime(Duration.ofSeconds(1));
            this.select(name);
        }
        catch (ElementClickInterceptedException | NotFoundException | NullPointerException e)
        {
            TcLog.error("Unable to perform select on Menu '%s'".formatted(name));
        }
    }

    public void selectFromDropDown(EMenu menu, String item)
    {
        try
        {
            if(isMenuItemsDisplayed())
            {
                TcLog.info("Menu already opened");
            }
            else
            {
                this.select(menu);
            }
            WebElement menuPanel = Objects.requireNonNull(css.findControl(By.cssSelector("div[class*=\"mat-mdc-menu-panel\"]")));
            WaitFor.condition(menuPanel::isDisplayed);

            List<WebElement> menuButtons = css.findControls(By.cssSelector("button[class*=\"mat-mdc-menu-item\"]"));

            menuButtons.stream()
                    .filter(button -> button.getText().equalsIgnoreCase(item))
                    .findFirst()
                    .ifPresent(WebElement::click);
        }
        catch (NullPointerException | NotFoundException | ElementClickInterceptedException e)
        {
            TcLog.error("Unable to select item '" + item + "' from the dropdown menu: " + menu);
        }
    }

    public boolean isMenuItemsDisplayed()
    {
        WebElement menuPanel = css.findControl(By.cssSelector("div[class*=\"mat-mdc-menu-panel\"]"));
        if (menuPanel != null)
        {
            return menuPanel.isDisplayed();
        }
        return false;
    }

}

