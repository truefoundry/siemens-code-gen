package ControlImplementation;

import ControlImplementation.Interface.IComboBoxControl;
import Enums.EComboBox;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.CoreKeyboardControl;
import fate.core.ControlImplementations.DomUtils;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Interfaces.IInteractableNaming;
import fate.core.PerfMon.PerfMon;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.awt.event.KeyEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComboBoxControl implements IComboBoxControl
{
    CoreCssControl css = new CoreCssControl();

    protected @Nullable WebElement findControl(IInteractableNaming name, By... rootBy)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        WebElement res = null;

        try
        {
            WebElement root =DomUtils.setRootElement(ModalControl.getSelector(), rootBy);
            res = switch(name.getType())
            {
                case INDEX ->
                {
                    List<WebElement> all = this.getAll(root);
                    yield !all.isEmpty() ? all.get(name.getIndex()) : null;
                }
                case SELECTOR -> css.findControlWithRoot(name.getSelector(), root);
                case NAME ->
                {
                    yield null;
                }
            };
            pm.stop();
            return res;
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.warning("Element Staled, Retrying...");
            WaitFor.specificTime(Duration.ofSeconds(1));
            return this.findControl(name);
        }
        catch(NullPointerException | NumberFormatException | IndexOutOfBoundsException e)
        {
            TcLog.warning("Unable to find control '%s'! ".formatted(name.getValue()) + e);
        }

        pm.abort();
        return null;
    }

    private List<WebElement> getAll(WebElement root)
    {
        List<WebElement> result = new ArrayList<>(css.findControlsWithRoot(By.cssSelector("mat-select[role=combobox]"), root));
        return result.stream().filter(WebElement::isDisplayed).toList();
    }

    public String select(EComboBox comboBox, String item)
    {
        WebDriver drv = CoreIocBuilder.getContainer().getComponent(WebDriver.class);
        try
        {
            WebElement ele = this.findControl(comboBox);
            if (ele != null)
            {
                String currentSelection = ele.getText().trim();
                if (currentSelection.equalsIgnoreCase(item))
                {
                    TcLog.info("Item '" + item + "' is already selected in the dropdown menu: " + comboBox);
                    return currentSelection; // Return the already selected item
                }
                ele.click();
            }

            WebElement menuPanel = Objects.requireNonNull(css.findControl(By.cssSelector("div[role=\"listbox\"]")));
            WaitFor.condition(menuPanel::isDisplayed);

            List<WebElement> options = css.findControls(By.cssSelector("mat-option[role='option']"));

            WaitFor.specificTime(Duration.ofNanos(1200));

            options.stream()
                    .filter(option -> option.getText().equalsIgnoreCase(item))
                    .findFirst()
                    .ifPresent(option ->
                    {
                        Actions actions = new Actions(drv);
                        actions.moveToElement(option).perform();
                        option.click();
                    });
            WaitFor.specificTime(Duration.ofNanos(1200));

            // Check for selected option in dropdown
            WebElement selectedOption = options.stream()
                    .filter(option -> option.getAttribute("class").contains("selected"))
                    .findFirst()
                    .orElse(null);

            if (selectedOption != null)
            {
                return selectedOption.getText();  // Return the selected item's text
            }
            else
            {
                TcLog.error("Item '" + item + "' not found or not selected from the dropdown menu: " + comboBox);
                return "";  // Return empty string if no item is selected
            }
        }
        catch (StaleElementReferenceException e)
        {
            TcLog.info("Element staled, Retrying...");
            WaitFor.specificTime(Duration.ofSeconds(3));
            return this.select(comboBox, item);
        }
        catch (NullPointerException | NotFoundException e)
        {
            TcLog.error("Unable to select item '" + item + "' from the dropdown menu: " + comboBox);
            return "";  // Return empty string on error
        }
    }


    public boolean exists(EComboBox combo){return this.findControl(combo) != null;}

}
