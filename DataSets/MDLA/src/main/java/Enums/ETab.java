package Enums;

import fate.core.Enums.EType;
import fate.core.Interfaces.IInteractableNaming;

public enum ETab implements IInteractableNaming
{
    MY_DASHBOARD("My Dashboard", EType.NAME),
    CREATED_REQUESTS("Created Requests", EType.NAME),
    COMMENTS("Comments", EType.NAME),
    REQUEST_INFORMATION("Request Information", EType.NAME),
    REQUEST_OVERVIEW("Request Overview", EType.NAME),
    REQUESTOR_INFORMATION("Requestor Information", EType.NAME),
    SUBMITTED("Submitted", EType.NAME),
    MY_RESPONSE_NEEDED("My Response Needed", EType.NAME),
    UNDER_REVIEW("Under Review", EType.NAME),
    ACTIVITY_STREAM("Activity Stream", EType.NAME),
    PENDING_REQUESTOR_RESPONSE("Pending Requestor Response", EType.NAME),
    MDLA_REQUESTS("MDLA Requests", EType.NAME),
    IDX("", EType.INDEX);

    private String value;
    private final EType type;

    ETab(String value, EType type)
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

    public static ETab byIndex(int i)
    {
        ETab.IDX.value = Integer.toString(i);
        return ETab.IDX;
    }
}
