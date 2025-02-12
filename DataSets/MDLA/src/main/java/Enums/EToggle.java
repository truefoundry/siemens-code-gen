package Enums;

import fate.core.Enums.EType;
import fate.core.Interfaces.IInteractableNaming;

public enum EToggle implements IInteractableNaming
{
    GLOBAL_SEARCH("Global Search (without filters)", EType.NAME),
    IDX("", EType.INDEX);

    private String value;
    private final EType type;

    EToggle(String value, EType type)
    {
        this.value = value;
        this.type = type;
    }

    @Override
    public String getValue()
    {
        return this.value;
    }

    @Override
    public EType getType()
    {
        return this.type;
    }
    public static EToggle byIndex(int i)
    {
        EToggle.IDX.value = Integer.toString(i);
        return EToggle.IDX;
    }
}
