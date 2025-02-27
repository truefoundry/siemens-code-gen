package Enums;

import fate.core.Enums.ResultPlatform;
import fate.core.Enums.TestType;
import fate.core.Interfaces.IResultData;

public enum EResultData implements IResultData
{
    ADMIN("Admin", "809", "811", TestType.WEB_UI, ResultPlatform.AZURE),
    EXTERNAL_USER("External User", "809", "852", TestType.WEB_UI, ResultPlatform.AZURE),
    INTERNAL_USER("External User", "809", "864", TestType.WEB_UI, ResultPlatform.AZURE),
    E2E("E2E", "809", "1198", TestType.WEB_UI, ResultPlatform.AZURE);

    private final String path;
    private final TestType type;
    private final ResultPlatform platform;
    private final String plan;
    private final String suite;

    /**
     * @param path     Test suite tree path.
     * @param planID   ID of test plan.
     * @param suiteID  ID of test suite.
     * @param tcType   Test type.
     * @param platform Used result platform.
     */
    EResultData(String path, String planID, String suiteID, TestType tcType, ResultPlatform platform)
    {
        this.path = path;
        this.plan = planID;
        this.suite = suiteID;
        this.type = tcType;
        this.platform = platform;
    }

    @Override
    public ResultPlatform getPlatform() {
        return this.platform;
    }

    @Override
    public String getPlanID() {
        return this.plan;
    }

    @Override
    public String getSuiteID() {
        return this.suite;
    }

    @Override
    public TestType getType() {
        return this.type;
    }

    @Override
    public String getPath() {
        return this.path;
    }
}
