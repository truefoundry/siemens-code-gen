package Enums;

import fate.core.Enums.EType;
import fate.core.Interfaces.IInteractableNaming;

public enum EEdit implements IInteractableNaming
{
    MOBILE("input[formcontrolname=\"phoneNumber\"]", EType.SELECTOR),
    PREFERRED_LANGUAGE("input[formcontrolname=\"preferredLanguage\"]", EType.SELECTOR),
    SUBJECT("input[formcontrolname=\"subject\"]", EType.SELECTOR),
    REASON(" textarea[formcontrolname=\"reason\"]", EType.SELECTOR),
    BROWSE_FILES_INPUT("input[class=\"file-input\"]", EType.SELECTOR),
    SOLD_TO("input[formcontrolname=\"selectedSoldTo\"]", EType.SELECTOR),
    SHIP_TO("input[formcontrolname=\"selectedShipTo\"]", EType.SELECTOR),
    PURCHASE_ORDER("input[id='mat-input-9']", EType.SELECTOR),
    ADDITIONAL_EMAIL("input[id='mat-input-8']", EType.SELECTOR),
    SEARCH("input[id*=\"mat-input\"]", EType.SELECTOR),
    COMMENT("div[class=\"angular-editor-textarea\"]", EType.SELECTOR),
    COMMENT_MODAL("mat-dialog-content>div[class='angular-editor-textarea']", EType.SELECTOR),


    IDX("", EType.INDEX);

    private String value;
    private EType type;

    EEdit(String value, EType type)
    {
        this.type = type;
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public EType getType() {
        return this.type;
    }

    public static EEdit byIndex(int i)
    {
        EEdit.IDX.value = Integer.toString(i);
        return EEdit.IDX;
    }
}
