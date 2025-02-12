package Enums;

import fate.core.CompositionRoot.TcLog;
import fate.core.Enums.EType;
import fate.core.Interfaces.IInteractableNaming;
import org.openqa.selenium.By;

public enum ECheckBox implements IInteractableNaming
{
    REFER_OUT_REQUEST_AFTER_SUBMISSION("Refer Out Request after Submission", EType.NAME),
    INFORMATION_ONLY("Informational Only", EType.NAME),
    MARK_AS_URGENT("Mark as urgent", EType.NAME),
    ONLY_SHOW_MY_ASSIGNED_TASKS("Only Show My Assigned Tasks", EType.NAME),
    CUSTOM("",EType.NAME),
    IDX("", EType.INDEX);
    private String value;
    private final EType type;

    ECheckBox(String value, EType type)
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

    public static ECheckBox byCustomValue(String value)
    {
        ECheckBox.CUSTOM.value = value;
        return ECheckBox.CUSTOM;
    }

    public static ECheckBox byIndex(int i)
    {
        ECheckBox.IDX.value = Integer.toString(i);
        return ECheckBox.IDX;
    }
}
