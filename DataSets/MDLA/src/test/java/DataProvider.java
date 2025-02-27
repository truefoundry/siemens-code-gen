import CompositionRoot.IocBuilder;
import CompositionRoot.MdlaHandler;
import Enums.EResultData;
import fate.core.PlatformConnectors.azure.TestCaseParameters;

import java.util.Map;
import java.util.stream.Stream;

public class DataProvider
{

    public static Stream<Map<String, String>> E2E()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.E2E, "460");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC01_Landing_Page_Layout_check()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.ADMIN, "465");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC02_Top_Ribbon_Functionality_Check()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.ADMIN, "469");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC03_Create_Request()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.ADMIN, "840");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC04_Show_Me_All_Requests_Layout_Check()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.ADMIN, "471");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC05_Admin_Menu_User_Management()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.ADMIN, "471");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC01_External_User_Landing_Page_Layout_Check()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.EXTERNAL_USER, "465");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC02_External_User_Top_Ribbon_Functionality_Check()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.EXTERNAL_USER, "469");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC03_External_User_Issue_with_an_order_or_deliver()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.EXTERNAL_USER, "470");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC04_External_User_Question_About_An_Order_Or_eSupport_Assistance()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.EXTERNAL_USER, "471");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC05_External_User_Question_About_My_Account()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.EXTERNAL_USER, "471");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC01_Internal_User_Landing_Page_Layout_Check()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.INTERNAL_USER, "465");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC02_Internal_User_Top_Ribbon_Functionality_Check()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.INTERNAL_USER, "469");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC03_Internal_User_Issue_with_an_order_or_deliver()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.INTERNAL_USER, "470");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC04_Internal_User_Question_About_An_Order_Or_eSupport_Assistance()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.INTERNAL_USER, "471");
        return new TestCaseParameters().getTCDataFromJSON();
    }

    public static Stream<Map<String, String>> TC05_Internal_User_Question_About_My_Account()
    {
        MdlaHandler handler = IocBuilder.createNewTest(EResultData.INTERNAL_USER, "471");
        return new TestCaseParameters().getTCDataFromJSON();
    }
}
