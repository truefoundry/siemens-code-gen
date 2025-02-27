package Create_SE_List_And_Start;

import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import ControlImplementations.ModalControl;
import Enums.*;
import TestUtils.ExcelControl;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.ControlImplementations.DomUtils;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;
import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TC07_Check_Supplier_Info_SU
{
    @Test
    void Check_Supplier_Info_SU()
    {
        IocBuilder.execute(Duration.ofMinutes(30), EResultData.SLIM_FS, "44025", tc ->
        {
            if(!ExcelControl.isDataAvailable(List.of("last Evaluation Date","Supplier1","Supplier2","SupplierID1", "Supplier1StrategyActualScore",
                    "Supplier1LogisticsActualScore", "Supplier1TotalActualScore", "Supplier1TechnologyActualScore",
                    "SupplierID2", "Supplier2StrategyActualScore", "Supplier2LogisticsActualScore", "Supplier2TotalActualScore",
                    "Supplier2TechnologyActualScore")))
            {
                tc.addStepInfo("Failed due to data absence in Excel Sheet", true, false,
                        new ComparerOptions().abort());
            }

            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL,ETestData.RISHITH_SU);

            String evaluationDate = ExcelControl.getValueUnderHeader("last Evaluation Date");

            String caseIDS1 = ExcelControl.getValueUnderHeader("Supplier1");
            String supplierIDS1 = ExcelControl.getValueUnderHeader("SupplierID1");
            String strategyProcurementScoreS1 = ExcelControl.getValueUnderHeader("Supplier1StrategyActualScore");
            String logisticsScoreS1 = ExcelControl.getValueUnderHeader("Supplier1LogisticsActualScore");
            String totalScoreS1 = ExcelControl.getValueUnderHeader("Supplier1TotalActualScore");
            String technologyInnovationScoreS1 = ExcelControl.getValueUnderHeader("Supplier1TechnologyActualScore");;

            String caseIDS2  = ExcelControl.getValueUnderHeader("Supplier2");
            String supplierIDS2 = ExcelControl.getValueUnderHeader("SupplierID2");
            String strategyProcurementScoreS2 = ExcelControl.getValueUnderHeader("Supplier2StrategyActualScore");
            String logisticsScoreS2  = ExcelControl.getValueUnderHeader("Supplier2LogisticsActualScore");
            String totalScoreS2  = ExcelControl.getValueUnderHeader("Supplier2TotalActualScore");
            String technologyInnovationScoreS2  = ExcelControl.getValueUnderHeader("Supplier2TechnologyActualScore");;


            //STEP 1
            BrowserControl.waitForLoadingIndicator();
            tc.menu.select(EMenu.CREATE, EMenu.FIND_MY_SUPPLIER);
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            WaitFor.condition(()->tc.combo.isDisplayed(EComboBox.SELECT_A_QMS),Duration.ofSeconds(5));
            tc.combo.select(EComboBox.SELECT_A_QMS,"ARGENTINA");
            tc.edit.sendKeys(EEdit.SUPPLIER_ID,supplierIDS1,true);
            tc.check.check(ECheckBox.INCLUDE_RESULT_FROM_OTHER_QMS);
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            WaitFor.specificTime(Duration.ofSeconds(2));
            WaitFor.condition(()->!tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS,EColumn.byIndex(0)).isEmpty() , Duration.ofSeconds(10));
            tc.addStepInfo("Supplier is displayed and system shows how many results your search retrieved",
                    true, supplierIDS1.equals(tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS,EColumn.byIndex(0)).get(0)),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            tc.table.doubleClickOnRow(ETable.SUPPLIER_DETAILS,0);
            WaitFor.condition(()->tc.browser.getTitlesOfExistingWindows().contains("Supplier360ViewDetails"),Duration.ofSeconds(10));
            tc.browser.switchToWindow(1);
            tc.browser.maximize();
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.node.expandSection(ENode.STRATEGIC_EVALUATION_RESULTS);
            HashMap<String,String> evalResults = this.fetchEvalResults();
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->evalResults.get("Last Strategic Evaluation - Strategy & Procurement Score")
                            .equals(strategyProcurementScoreS1),"Strategy & Procurement Score was expecting %s but found %s".formatted(logisticsScoreS1, evalResults.get("Last Strategic Evaluation - Strategy & Procurement Score")))
                    .add(()->evalResults.get("Last Strategic Evaluation - Logistics Score")
                            .equals(logisticsScoreS1),"Logistics Score was expecting %s but found %s".formatted(logisticsScoreS1, evalResults.get("Last Strategic Evaluation - Logistics Score")))
                    .add(()->evalResults.get("Last Strategic Evaluation - Total Score")
                            .equals(totalScoreS1),"Total Score was expecting %s but found %s".formatted(totalScoreS1, evalResults.get("Last Strategic Evaluation - Total Score")))
                    .add(()->evalResults.get("Last Strategic Evaluation - Technology & Innovation Score")
                            .equals(technologyInnovationScoreS1),"Technology & Innovation Score was expecting %s but found %s".formatted(logisticsScoreS1, evalResults.get("Last Strategic Evaluation - Technology & Innovation Score")))
                    .add(()->evalResults.get("Last Strategic Evaluation Date")
                            .equals(evaluationDate),"Last Evaluation date was expecting %s but found %s".formatted(evaluationDate, evalResults.get("Last Strategic Evaluation Date")));
            tc.addStepInfo("Results from SE performed are being displayed","ok",tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.node.expandSection(ENode.DOCUMENTS_CONTRACTS_CASES);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.DOCUMENTS),Duration.ofSeconds(5));
            tc.tab.select(ETab.DOCUMENTS);
            WaitFor.condition(()->tc.table.exists(ETable.DOCUMENTS),Duration.ofSeconds(10));
            tc.table.filter(ETable.DOCUMENTS,EColumn.byIndex(0),caseIDS1+"_Audit Trail");
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.table.getRowsCount(ETable.DOCUMENTS)==1,Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator.add(()->(!tc.table.getItemsFromColumn(ETable.DOCUMENTS, EColumn.byIndex(0)).isEmpty()),"There are no related Audit trails in the table");
            File pdf = tc.table.downloadFileFromTable(ETable.DOCUMENTS, EColumn.byIndex(6), "#0");
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdf, "Internal Comment"), "Mismatch InternalCommentS1");
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdf, "External Comment"), "Mismatch ExternalCommentS1");
            tc.addStepInfo("All given answers are correctly displaying.","ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.browser.closeByIndex(2);
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.CASE_HISTORY),Duration.ofSeconds(5));
            tc.tab.select(ETab.CASE_HISTORY);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.table.exists(ETable.CASE_HISTORY),Duration.ofSeconds(10));
            tc.table.filter(ETable.CASE_HISTORY,EColumn.byIndex(0),caseIDS1);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.table.getRowsCount(ETable.CASE_HISTORY)==1,Duration.ofSeconds(5));
            tc.addStepInfo("Executed cases are listed",true,
                    !tc.table.getItemsFromColumn(ETable.CASE_HISTORY, EColumn.byIndex(0)).isEmpty(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.browser.closeByIndex(1);
            tc.browser.switchToTab(0);
            BrowserControl.waitForLoadingIndicator();
            tc.menu.select(EMenu.CREATE, EMenu.FIND_MY_SUPPLIER);
            WaitFor.condition(()->tc.combo.exists(EComboBox.SELECT_A_QMS),Duration.ofSeconds(5));
            tc.combo.select(EComboBox.SELECT_A_QMS,"ARGENTINA");
            tc.edit.sendKeys(EEdit.SUPPLIER_ID,supplierIDS2,true);
            tc.check.check(ECheckBox.INCLUDE_RESULT_FROM_OTHER_QMS);
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            WaitFor.specificTime(Duration.ofSeconds(2));
            WaitFor.condition(()->!tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS,EColumn.byIndex(0)).isEmpty() , Duration.ofSeconds(10));
            tc.addStepInfo("Supplier is displayed and system shows how many results your search retrieved",
                    true,supplierIDS2.equals(tc.table.getItemsFromColumn(ETable.SUPPLIER_DETAILS,EColumn.byIndex(0)).get(0)),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.table.doubleClickOnRow(ETable.SUPPLIER_DETAILS,0);
            WaitFor.condition(()->tc.browser.getTitlesOfExistingWindows().contains("Supplier360ViewDetails"),Duration.ofSeconds(10));
            tc.browser.switchToWindow(1);
            tc.browser.maximize();
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.node.expandSection(ENode.STRATEGIC_EVALUATION_RESULTS);
            HashMap<String,String> evalResults2 = this.fetchEvalResults();
            tc.stepEvaluator.reset();
            tc.stepEvaluator
                    .add(()->evalResults2.get("Last Strategic Evaluation - Strategy & Procurement Score").equals(strategyProcurementScoreS2),"Strategy & Procurement Score is Incorrect")
                    .add(()->evalResults2.get("Last Strategic Evaluation - Logistics Score").equals(logisticsScoreS2),"Logistics Score is Incorrect")
                    .add(()->evalResults2.get("Last Strategic Evaluation - Total Score").equals(totalScoreS2),"Total Score is Incorrect")
                    .add(()->evalResults2.get("Last Strategic Evaluation - Technology & Innovation Score").equals(technologyInnovationScoreS2),"Technology & Innovation Score is Incorrect")
                    .add(()->evalResults2.get("Last Strategic Evaluation Date").equals(evaluationDate),"Last Strategic Evaluation Date");
            tc.addStepInfo("Results from SE performed are being displayed","ok",tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.node.expandSection(ENode.DOCUMENTS_CONTRACTS_CASES);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.DOCUMENTS),Duration.ofSeconds(5));
            tc.tab.select(ETab.DOCUMENTS);
            WaitFor.condition(()->tc.table.exists(ETable.DOCUMENTS),Duration.ofSeconds(10));
            tc.table.filter(ETable.DOCUMENTS,EColumn.byIndex(0),caseIDS2+"_Audit Trail");
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.table.getRowsCount(ETable.DOCUMENTS)==1,Duration.ofSeconds(5));
            tc.stepEvaluator.reset();
            tc.stepEvaluator.add(()->(!tc.table.getItemsFromColumn(ETable.DOCUMENTS, EColumn.byIndex(0)).isEmpty()),"There are no related Audit trails in the table");
            File pdfS2 = tc.table.downloadFileFromTable(ETable.DOCUMENTS, EColumn.byIndex(6), "#0");
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdfS2, "Internal"), "Mismatch InternalCommentS2");
            tc.stepEvaluator.add(() -> tc.table.checkContentInPdf(pdfS2, "External"), "Mismatch ExternalCommentS2");
            tc.addStepInfo("All given answers are correctly displaying.","ok",tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            tc.browser.closeByIndex(2);
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.CASE_HISTORY),Duration.ofSeconds(5));
            tc.tab.select(ETab.CASE_HISTORY);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.table.exists(ETable.CASE_HISTORY),Duration.ofSeconds(10));
            tc.table.filter(ETable.CASE_HISTORY,EColumn.byIndex(0),caseIDS2);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(()->tc.table.getRowsCount(ETable.CASE_HISTORY)==1,Duration.ofSeconds(5));
            tc.addStepInfo("Executed cases are listed",true,
                    !tc.table.getItemsFromColumn(ETable.CASE_HISTORY, EColumn.byIndex(0)).isEmpty(),
                    new ComparerOptions().takeScreenShotPlatform());

        });
    }

    public HashMap<String, String> fetchEvalResults()
    {
        WebElement root = DomUtils.setRootElement(ModalControl.getSelector());
        CoreCssControl css = IocBuilder.getContainer().getComponent(CoreCssControl.class);
        try
        {
            HashMap<String, String> map = new HashMap<>();
            WebElement outerRoot = css.findControlWithRoot(By.xpath("(//*[@id='RULE_KEY']/div)[11]"), root);
            List<WebElement> strategicEval = css.findControlsWithRoot(By.cssSelector("div[string_type='layout'][data-template]"), outerRoot);
            if (strategicEval.isEmpty())
            {
                throw new NotFoundException("The Strategic Evaluation Result is Empty");
            }

            for (WebElement webElement : strategicEval)
            {
                String value = Objects.requireNonNull(css.findControlWithRoot(By.cssSelector("div[data-methodname]"), webElement)).getText().replaceAll("\\s+", "");
                map.put(Objects.requireNonNull(css.findControlWithRoot(By.tagName("label"), webElement)).getText().trim(),
                        value.matches("\\d+outof\\d+") ? value.split("outof")[0] : value);
            }

            TcLog.info("Strategic Evaluation Result: %s".formatted(map));
            return map;
        }
        catch (Exception e)
        {
            TcLog.error("UnExpected error ");
        }
        return new HashMap<>();
    }
}
