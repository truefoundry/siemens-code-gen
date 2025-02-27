package Enums;

import fate.core.DTO.ProjectData;
import fate.core.Email.EmailConfig;
import fate.core.Email.IEmailConfig;
import fate.core.Interfaces.ITestData;

public enum ETestData implements ITestData
{
    FUNCTIONAL_USER("@siemens-healthineers.com", "", new EmailConfig("", "")),
    EXTERNAL_USER("mdla.siemens@gmail.com", "", new EmailConfig("", ""))
    ;
    private final String name;
    private final String pass;
    private final IEmailConfig emailConfig;
    public static final String QA_ENV_URL = "https://qa.my-digital-lab-assistant.siemens-healthineers.com/menu";
    public static final String QA_WEBS_SHOP_URL = "https://heart:Heart123!@shop-upgrade-uat.hcvpc.io/SIEMENS/dashboard";



    public static final ProjectData PROJECT_DATA = new ProjectData(
            "https://dev.azure.com",
            "",
            System.getenv("SYSTEM_ACCESSTOKEN") != null ? System.getenv("SYSTEM_ACCESSTOKEN") : System.getenv("ARTIFACTS_ENV_ACCESS_TOKEN"),
            "SHS-IT-PLE-GDC",
            "SHS IT Test Service", "");

    ETestData(String name, String pass, IEmailConfig emailConfig)
    {
        this.name = name;
        this.pass = pass;
        this.emailConfig = emailConfig;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPass() {
        return this.pass;
    }

    @Override
    public IEmailConfig getEmailConfig() {
        return this.emailConfig;
    }
}
