package Enums;

import fate.core.Enums.EType;
import fate.core.Interfaces.IInteractableNaming;

public enum ETile implements IInteractableNaming
{
    REPORT_AN_ISSUE("Report an issue with an order or delivery", EType.NAME),
    SHOW_ME_All_REQUESTS("Show me all Requests", EType.NAME),
    SHOW_ME_MY_REQUESTS("Show me my Requests", EType.NAME),
    QUESTIONS_ABOUT_AN_ORDER("Question about an order or eSupport assistance", EType.NAME),
    QUESTIONS_ABOUT_MY_ACCOUNT("Questions about my Account", EType.NAME),
    REQUEST_ALLOCATION("Request Allocation or Saturday Delivery (SET Request)", EType.NAME),
    ADMIN_RULE_SET("Admin Ruleset", EType.NAME),
    USER_MANAGEMENT("User Management", EType.NAME),
    ESCALATION_CRITERIA("Escalation Criteria", EType.NAME),
    IDX("", EType.INDEX);

    private String value;
    private EType type;

    ETile(String value, EType type)
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

    public static ETile byIndex(int i)
    {
        ETile.IDX.value = Integer.toString(i);
        return ETile.IDX;
    }
}
