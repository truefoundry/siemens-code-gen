package Enums;

import fate.core.Enums.EType;
import fate.core.Interfaces.IInteractableNaming;

public enum EModal implements IInteractableNaming
{

    MODAL_WRAPPER("mat-dialog-container[role='dialog']", EType.SELECTOR);
    private final String value;
    private final EType type;

    EModal(String value, EType type)
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
}
