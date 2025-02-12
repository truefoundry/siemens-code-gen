package ControlImplementation;

import CompositionRoot.IocBuilder;
import ControlImplementation.Interface.IProgressBarControl;
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

public class ProgressBarControl extends CoreInteractables implements IProgressBarControl
{
    CoreCssControl css = IocBuilder.getContainer().getComponent(CoreCssControl.class);

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
            TcLog.error("Unable to found wanted element!", e);
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
            res = new ArrayList<>(css.findControls(By.cssSelector("div[class*=\"active\"]")));
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

    public List<String> getNames()
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        List<String > res = new ArrayList<>();
        try
        {
            List<WebElement> elements= new ArrayList<>(css.findControls(By.cssSelector("button[class*='items']>span")));
            for(WebElement ele : elements)
            {
                res.add(ele.getText().replace("\n", " ").trim());
            }
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.info("Staled root element of progress bar ... wait for refreshing");
            WaitFor.specificTime(Duration.ofSeconds(2));
            this.getNames();
        }
        pm.stop();
        return res;
    }

}
