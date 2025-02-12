package Enums;

import fate.core.CompositionRoot.TcLog;
import fate.core.Enums.EType;
import fate.core.Interfaces.IInteractableNaming;
import org.openqa.selenium.By;

;public enum EButton implements IInteractableNaming
{
    CONTACT("mail\nContact", EType.NAME),
    NEXT("Next", EType.NAME),
    BROWSE_FILES("Browse files", EType.NAME),
    SAVE_PLUS_SUBMIT("Save + Submit", EType.NAME),
    YES_SUBMIT("Yes, submit", EType.NAME),
    ADMIN("//span[text() = 'Admin']", EType.SELECTOR),
    EDIT("Edit", EType.NAME),
    UPDATE_USER("Update User", EType.NAME),
    GO_BACK_TO_THE_DASHBOARD("Go back to the dashboard", EType.NAME),
    DELETE("Delete", EType.NAME),
    CONFIRM("Confirm", EType.NAME),
    SAVE("Save", EType.NAME),
    CANCEL("Cancel", EType.NAME),
    YES_CANCEL_REQUEST("Yes, cancel request", EType.NAME),
    PUT_ON_HOLD("Put on Hold", EType.NAME),
    SET_ACTIVE("Set Active", EType.NAME),
    RESOLVE_REQUEST("Resolve Request", EType.NAME),
    REOPEN_REQUEST("Reopen Request", EType.NAME),
    REOPEN_REQUEST_MODAL("Reopen request", EType.NAME),
    SUBMIT_ANYWAY("Submit Anyway", EType.NAME),
    COMMENT_ONLY("Comment Only", EType.NAME),
    COMMENT_AND_SEND("Comment and Send", EType.NAME),
    STATUS("//button[contains(text(), 'Status')]", EType.SELECTOR),
    APP_NAME("div[class*='app-name']", EType.SELECTOR),
    CLOSE("div[class=\"close-button-container\"]>button", EType.SELECTOR),
    MS_LOGIN_NEXT_BTN("input[type='submit']", EType.SELECTOR),
    MS_SIGN_IN("input[id='idSIButton9']",EType.SELECTOR),
    MS_NO("input[id='idBtn_Back']",EType.SELECTOR),
    MS_YES("input[id*='idSIButton']", EType.SELECTOR),
    NOTIFICATIONS("button>span[class*='notifications-icon-button']", EType.SELECTOR),
    SEND_ICON("//mat-icon[text() = 'send']", EType.SELECTOR),


    IDX("", EType.INDEX);

    private String value;
    private final EType type;

    EButton(String value, EType type)
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
    public static EButton byIndex(int i)
    {
        EButton.IDX.value = Integer.toString(i);
        return EButton.IDX;
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

