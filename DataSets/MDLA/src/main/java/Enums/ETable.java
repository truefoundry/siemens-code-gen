package Enums;

import fate.core.CompositionRoot.TcLog;
import fate.core.Enums.EType;
import fate.core.Interfaces.IInteractableNaming;
import org.openqa.selenium.By;

public enum ETable implements IInteractableNaming
{
    APP_TABLE("app-table>div>table", EType.SELECTOR),
    USER_TABLE("div[class='user__table ng-star-inserted']", EType.SELECTOR),


    //Basic
    IDX("", EType.INDEX),

    ;

    private String value;
    private final EType type;

    ETable(String value, EType type)
    {
        this.type = type;
        this.value = value;
    }

    @Override
    public String getValue(){return this.value;}

    @Override
    public EType getType()
    {
        return this.type;
    }

    public static ETable byIndex(int i)
    {
        ETable.IDX.value = Integer.toString(i);
        return ETable.IDX;
    }

    @Override
    public By getSelector()
    {
        if(this.getType().equals(EType.SELECTOR))
        {
            return this.getValue().startsWith("//") ? By.xpath(this.getValue()) : By.cssSelector(this.getValue());
        }
        else
        {
            TcLog.warning("Current Enum '%s' does not contain any selectors".formatted(this));
            return By.cssSelector("");
        }
    }
}
