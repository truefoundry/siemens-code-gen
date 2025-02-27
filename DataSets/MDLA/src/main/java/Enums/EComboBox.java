package Enums;

import fate.core.Enums.EType;
import fate.core.Interfaces.IInteractableNaming;

public enum EComboBox implements IInteractableNaming
{
    COUNTRY("mat-select[placeholder=\"Country\"]", EType.SELECTOR),
    STATE("mat-select[formcontrolname=\"state\"]", EType.SELECTOR),
    SHS_TEAM("mat-select[placeholder=\"Siemens Healthineers Team\"]", EType.SELECTOR),
    CLASSIFICATIONS("mat-select[placeholder=\"Classification\"]", EType.SELECTOR),
    IDX("", EType.INDEX);

    private String value;
    private final EType type;

    EComboBox(String value, EType type)
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

    public static EComboBox byIndex(int i)
    {
        EComboBox.IDX.value = Integer.toString(i);
        return EComboBox.IDX;
    }
}
