package Enums;

import fate.core.Enums.EType;
import fate.core.Interfaces.IInteractableNaming;

public enum EColumn implements IInteractableNaming
{
    CUSTOM("", EType.NAME),
    //Basic
    IDX("", EType.INDEX);

    private String value;
    private final EType type;

    EColumn(String value, EType type)
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

    public static EColumn byIndex(int i)
    {
        EColumn.IDX.value = Integer.toString(i);
        return EColumn.IDX;
    }

    public static EColumn byCustomValue(String value)
    {
        EColumn.CUSTOM.value = value;
        return EColumn.CUSTOM;
    }
}
