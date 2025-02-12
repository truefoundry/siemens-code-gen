package Enums;

import fate.core.CompositionRoot.TcLog;
import fate.core.Enums.EType;
import fate.core.Interfaces.IInteractableNaming;
import org.openqa.selenium.By;

public enum EMenu implements IInteractableNaming
{
    LANGUAGE("span[class='icon-Language']", EType.SELECTOR),
    SETTINGS("span[class*='icon-Settings']", EType.SELECTOR),
    CHANGE_STATUS("button[class*='status-menu']", EType.SELECTOR),
    NAME_ICON("div[class='avatar']", EType.SELECTOR),
    USER("div[class='user_info']", EType.SELECTOR),
    CUSTOM("",EType.NAME),
    IDX("", EType.INDEX);

    private String value;
    private final EType type;

    EMenu(String value, EType type)
    {
        this.type = type;
        this.value = value;
    }

    @Override
    public String getValue(){return this.value;}

    @Override
    public By getSelector()
    {
        if(this.type.equals(EType.SELECTOR))
        {
            return By.cssSelector(this.value);
        }
        else
        {
            TcLog.warning("Current Enum '%s' does not contain any selectors".formatted(value));
            return By.cssSelector("");
        }
    }

    @Override
    public EType getType()
    {
        return this.type;
    }

    public static EMenu byCustomValue(String value)
    {
        EMenu.CUSTOM.value = value;
        return EMenu.CUSTOM;
    }

    public static EMenu byIndex(int i)
    {
        EMenu.IDX.value = Integer.toString(i);
        return EMenu.IDX;
    }
}
