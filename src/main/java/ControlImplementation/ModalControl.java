package ControlImplementation;

import Enums.EModal;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.DomUtils;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.EType;
import fate.core.Statics.MsgCode;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ModalControl
{
    CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);

    protected @Nullable WebElement findControl(EModal... modal)
    {
        try
        {
            List<WebElement> all = this.getAll();

            //by selector
            if(!ObjectUtils.isEmpty(modal) && modal[0].getType().equals(EType.SELECTOR))
            {
                return css.findControl(modal[0].getSelector());
            }

            if(!all.isEmpty())
            {
                //get the last modal if there's multiple
                WebElement ctl = this.getTopModal(all);

                //any modal
                if(ObjectUtils.isEmpty(modal))
                {
                    return ctl;
                }

                //by name
                if(ctl != null && modal[0].getType().equals(EType.NAME))
                {
                    WebElement header = css.findControlWithRoot(By.cssSelector("span[id='modaldialog_hd_title'"), ctl);
                    if(header != null && header.getText().trim().equalsIgnoreCase(modal[0].getValue()))
                    {
                        TcLog.debug("Modal findControl return -> Class: %s, ID: %s".formatted(ctl.getAttribute("class"), ctl.getAttribute("id")));
                        return ctl;
                    }
                    else
                    {
                        TcLog.warning(String.format("Modal %s not visible!", modal[0].getValue()));
                    }
                }
            }
        }
        catch(StaleElementReferenceException e)
        {
            WaitFor.specificTime(Duration.ofSeconds(1));
            return this.findControl(modal);
        }
        catch(NullPointerException | IndexOutOfBoundsException e)
        {
            TcLog.warning("Unable to find modal control! " + e);
        }

        return null;
    }

    @CheckForNull
    public static By getSelector()
    {
        CoreCssControl css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
        By modal = null;
        try
        {
            modal = css.findControl(EModal.MODAL_WRAPPER.getSelector()) != null ? EModal.MODAL_WRAPPER.getSelector()
                    : null;

            if(modal == null)
            {
                return null;
            }

            WebElement d = css.findControl(modal);
            if(d != null && d.getTagName().equals("tr"))
            {
                modal = DomUtils.getElementsSelector(Objects.requireNonNull(DomUtils.getParentElement(d, 1)));
            }
        }
        catch(StaleElementReferenceException e)
        {
            TcLog.debug("Staled Modal %s ... wait for refreshing".formatted(modal));
            WaitFor.toBeFresh(modal, Duration.ofMillis(2000));
            return getSelector();
        }
        catch(NullPointerException e)
        {
            TcLog.warning("Unable to get selector! " + e);
        }

        return modal;
    }

    private List<WebElement> getAll()
    {
        List<WebElement> all = new ArrayList<>(css.findControls(EModal.MODAL_WRAPPER.getSelector()).stream().filter(WebElement::isDisplayed).toList());
        return all;
    }

    public WebElement getTopModal(List<WebElement> modals)
    {
        return modals.stream().max((Comparator.comparingInt(a -> {
            try
            {
                return Integer.parseInt(a.getCssValue("z-index"));
            }
            catch(NumberFormatException e)
            {
                return -99999999;
            }
        }))).orElse(null);
    }

    public boolean exists(EModal... modal)
    {
        WebElement ctl = this.findControl(modal);
        boolean res = ctl != null && ctl.isDisplayed();
        TcLog.info("Modal %s exists.".formatted(res ? "is" : "is not"));
        return res;
    }

    public String getTitle()
    {
        String title = MsgCode.NOT_FOUND;
        WebElement modal = this.findControl();
        if(modal != null)
        {
            WebElement header = css.findControlWithRoot(By.cssSelector("h2"), modal);
            if(header != null)
            {
                title = header.getText();
            }
        }
        else
        {
            TcLog.error("No modal found!");
        }
        return title;
    }

    public String getContent()
    {
        String title = MsgCode.NOT_FOUND;
        WebElement modal = this.findControl();
        if(modal != null)
        {
            WebElement header = css.findControlWithRoot(By.cssSelector("p"), modal);
            if(header != null)
            {
                title = header.getText();
            }
        }
        else
        {
            TcLog.error("No modal found!");
        }
        return title;
    }
}
