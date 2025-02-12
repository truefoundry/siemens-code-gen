package Enums;

import fate.core.Enums.EType;

public enum EActions
{
    ICON("Icon"),
    USERNAME("UserName");

    private final String action;

    EActions(String action)
    {
        this.action = action;
    }
    public String getAction()
    {
        return this.action;
    }
}
