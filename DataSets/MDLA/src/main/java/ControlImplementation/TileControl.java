package ControlImplementation;

import CompositionRoot.IocBuilder;
import ControlImplementation.Interface.ITileControl;
import Enums.ETile;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Interfaces.IInteractableNaming;
import fate.core.PerfMon.PerfMon;
import org.jetbrains.annotations.Nullable;
import org.openqa.selenium.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TileControl implements ITileControl
{
    CoreCssControl css = IocBuilder.getContainer().getComponent(CoreCssControl.class);

    protected @Nullable WebElement findControl(IInteractableNaming name, By... rootBy)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        WebElement res;
        try
        {
            WebElement root = Objects.requireNonNull(css.findControl(By.cssSelector("html")));
            res = switch(name.getType())
            {
                case INDEX -> this.getAll().get(name.getIndex());
                case SELECTOR -> css.findControlWithRoot(name.getSelector(), root);
                case NAME -> this.getAll().stream().filter(tle -> {
                    if(tle.getText().replace("\n", " ").trim().equals(name.getValue()))
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

    private List<WebElement> getAll()
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        List<WebElement> res;
        try
        {
            res = new ArrayList<>(css.findControls(By.cssSelector("div[class*='tile']")));
            res = res.stream().filter(WebElement::isDisplayed).toList();
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Staled root element of button ... wait for refreshing");
            WaitFor.specificTime(Duration.ofSeconds(2));
            return this.getAll();
        }

        pm.stop();
        return res;
    }

    public List<String> getAllTilesName()
    {
        List<String> values = new ArrayList<>();
        try
        {
            List<WebElement> tileElements = this.getAll();
            for (WebElement ele : tileElements)
            {
                values.add(ele.getText().replace("\n", " ").trim());
            }
        }
        catch (NullPointerException | NotFoundException e)
        {
            TcLog.error("Unable to find names of tiles");
        }
        return values;
    }

    public boolean exists(ETile name)
    {
        return this.findControl(name) != null;
    }

    public void open(ETile tile)
    {
        try
        {
            WebElement ele = this.findControl(tile);
            if (ele != null)
            {
                ele.click();
                TcLog.action("Tile '%s' has opened".formatted(tile));
                BrowserControl.waitForLoadingIndicator();
            }
            else
            {
                TcLog.error("Unable to find tile '%s'".formatted(tile));
            }
        }
        catch (StaleElementReferenceException e)
        {
            TcLog.info("Element staled, Retying...");
            WaitFor.specificTime(Duration.ofSeconds(2));
            this.open(tile);
        }
        catch (NullPointerException | ElementClickInterceptedException | NotFoundException e)
        {
            TcLog.error("Unable to open tile '%s'".formatted(tile));
        }
    }
}
