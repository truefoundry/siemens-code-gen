package DocumentValidity;

import CompositionRoot.IocBuilder;
import ControlImplementations.BrowserControl;
import Enums.*;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.DirectoryControl;
import fate.core.ControlImplementations.WaitFor;
import fate.core.Enums.WebDrv;
import fate.core.Pdf.CorePdfControl;
import fate.core.Results.ComparerOptions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Document_Validity_VD
{
    @Test
    void Document_Validity_VD()
    {
        IocBuilder.execute(Duration.ofMinutes(10), EResultData.DOCUMENT_VALIDITY_FS, "43994", tc ->
        {
            String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            tc.browser.start(WebDrv.CHROME, ETestData.SLIM_USER_LOGIN_URL,ETestData.RISHITH_KU);

            //STEP 1
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(10));
            tc.menu.select(EMenu.CREATE, EMenu.VALIDITY_DOCUMENTS);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.combo.exists(EComboBox.SELECT_A_QMS));
            tc.addStepInfo("'Choose QMS' screen opens", true, tc.browser.getPageHeaders().contains("Choose QMS"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 2
            tc.combo.select(EComboBox.SELECT_A_QMS, "CT");
            tc.button.click(EButton.SUBMIT);
            BrowserControl.waitForLoadingIndicator();
            WaitFor.condition(() -> tc.button.exists(EButton.SEARCH_FOR_SUPPLIERS));
            tc.addStepInfo("Search for Supplier screen opens", true, tc.browser.getPageHeaders().contains("Search for Supplier"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 3
            tc.edit.sendKeys(EEdit.NAME, "schl");
            tc.button.click(EButton.SEARCH_FOR_SUPPLIERS);
            WaitFor.specificTime(Duration.ofSeconds(2));
            tc.addStepInfo("Supplier is displayed", true,
                    tc.table.getItemsFromColumn(ETable.byIndex(0), EColumn.byIndex(2)).contains("Schleifring GmbH"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 4
            tc.table.selectCheckBox(ETable.byIndex(0), EColumn.SELECT, 0);
            tc.button.click(EButton.CONTINUE);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            WaitFor.condition(() -> tc.table.exists(ETable.DOCUMENTS));
            tc.stepEvaluator.add(() -> tc.browser.getPageHeaders().contains("Validity Documents"), "Validity document screen is not visible.")
                    .add(() -> tc.table.getRowsCount(ETable.DOCUMENTS) > 0, "Existing documents are not listed");
            tc.addStepInfo("The \"Validity Documents\" screen opens and existing documents are listed",
                    "ok", tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 5
            tc.stepEvaluator.reset();
            WaitFor.condition(() -> tc.button.exists(EButton.SUBMIT));
            tc.table.selectCheckBox(ETable.DOCUMENTS, EColumn.SELECT, 1);
            WaitFor.specificTime(Duration.ofSeconds(2));
            WaitFor.condition(() -> tc.table.isCheckboxEnabled(ETable.DOCUMENTS, EColumn.VALID, 1));
            tc.table.setTextToEditBox(ETable.DOCUMENTS, EColumn.VALID_TO, 1, todayDate);
            tc.table.selectCheckBox(ETable.DOCUMENTS, EColumn.VALID, 1);
            String title1 = tc.table.getItemValueFromColumn(ETable.DOCUMENTS, EColumn.TITLE, "#1", EColumn.TITLE);
            tc.addStepInfo("Todays date is populated in \"Valid To\" column", todayDate,
                    tc.table.getItemValueFromColumn(ETable.DOCUMENTS, EColumn.VALID_TO, "#1", EColumn.byCustomValue("Valid To")),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 6
            tc.table.selectCheckBox(ETable.DOCUMENTS, EColumn.SELECT, 2);
            WaitFor.condition(() -> tc.table.isCheckboxEnabled(ETable.DOCUMENTS, EColumn.VALID, 1));
            tc.table.deselectCheckBox(ETable.DOCUMENTS, EColumn.VALID, 2);
            String title2 = tc.table.getItemValueFromColumn(ETable.DOCUMENTS, EColumn.TITLE, "#2", EColumn.TITLE);
            tc.stepEvaluator.add(() -> tc.table.isCheckboxSelected(ETable.DOCUMENTS, EColumn.VALID, 1),
                    "Checkmark present at the selected CERTIFICATE Valid?");
            tc.tab.select(ETab.CONTRACTS);
            tc.table.selectCheckBox(ETable.byIndex(0), EColumn.SELECT, 0);
            tc.table.deselectCheckBox(ETable.byIndex(0), EColumn.VALID, 0);
            tc.stepEvaluator.add(() -> !tc.table.isCheckboxSelected(ETable.byIndex(0), EColumn.VALID, 0),
                    "Checkmark present at the selected CONTRACT Valid?");
            tc.addStepInfo("No checkmark at the selected CERTIFICATE and CONTRACT in column \"Valid?\"", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 7
            tc.stepEvaluator.reset();
            String comment = "document and contract are not valid anymore";
            tc.editor.sendKeys(EEditor.COMMENTS, comment);
            tc.addStepInfo("\"document and contract are not valid anymore\" is populated in \"Comments\" field", comment,
                    tc.editor.getText(EEditor.COMMENTS), new ComparerOptions().takeScreenShotPlatform());

            //STEP 8
            tc.button.click(EButton.SUBMIT);
            tc.spinner.waitFor(ESpinner.LOADING_INDICATOR, Duration.ofSeconds(5));
            WaitFor.condition(()-> !tc.browser.getPageHeaders().contains("Validity Documents"));
            tc.addStepInfo("To do screen opens. Creating Quality Records is displayed.", true,
                    tc.node.getAllTitlesFromTODO(ENode.ASSIGNMENT_TODO).contains("Creating Quality Records"), new ComparerOptions().takeScreenShotPlatform());

            //STEP 9
            tc.menu.selectWithOutExpand(EMenu.SUPPLIER);
            tc.browser.waitForStateChange(() -> tc.node.getAllTitlesFromTODO(ENode.ASSIGNMENT_TODO).contains("Awaiting COI Merge Response"),
                    Duration.ofMinutes(5), Duration.ofSeconds(4));
            tc.button.click(EButton.SHOW_360_DREGEE_VIEW);
            WaitFor.condition(()->tc.browser.getTitlesOfExistingWindows().contains("Supplier360ViewDetails"), Duration.ofSeconds(10));
            tc.browser.switchToWindow(1);
            tc.browser.maximize();
            tc.addStepInfo("Supplier 360° View opens", true, tc.browser.getTitleOfActiveWindow().equals("Supplier360ViewDetails"),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 10
            tc.node.expandSection(ENode.DOCUMENTS_CONTRACTS_CASES);
            WaitFor.condition(()->tc.tab.isTabPresent(ETab.DOCUMENTS), Duration.ofSeconds(5));
            tc.stepEvaluator.add(() -> tc.tab.isTabPresent(ETab.DOCUMENTS), "Documents tab is not present")
                    .add(() -> tc.tab.isTabPresent(ETab.CONTRACTS), "Contracts tab is not present")
                    .add(() -> tc.tab.isTabPresent(ETab.CASE_HISTORY), "Case History tab is not present");
            tc.addStepInfo("'Documents', 'Contracts' and 'Case History' tabs are shown", "ok",
                    tc.stepEvaluator.eval(), new ComparerOptions().takeScreenShotPlatform());

            //STEP 11
            tc.browser.switchToWindow(0);
            WaitFor.condition(() -> tc.node.getValues(ENode.CASE_DETAILS).contains("PENDING-AWAITINGRESPONSE"));
            tc.browser.switchToWindow(1);
            WaitFor.condition(() -> !tc.table.isValid(ETable.DOCUMENTS, EColumn.TITLE, title2, EColumn.VALID));
            tc.addStepInfo(" Invalid document is displayed under 'Documents' section as invalid (if it is not shown" +
                            "  wait until the VD case hast to be in PENDING-AWAITINGRESPONSE)", true,
                    !tc.table.isValid(ETable.DOCUMENTS, EColumn.TITLE, title2, EColumn.VALID), new ComparerOptions().takeScreenShotPlatform());

            //STEP 12
            tc.browser.switchToWindow(0);
            tc.utility.expand();
            tc.utility.clickOnLink(EUtility.FILES_AND_DOCUMENTS, "Audit Trail");
            WaitFor.condition(() -> tc.button.exists(EButton.DOWNLOAD_PDF));
            Date download = new Date();
            tc.button.click(EButton.DOWNLOAD_PDF);
            tc.browser.checkDownloadedPdfFile(download);
            File downloadedFile = Objects.requireNonNull(DirectoryControl.getLastDownloadedFile()).toFile();
            String documentsSummary = this.extractPdfContentsBetweenStrings(downloadedFile, "DocumentsSummary", "DocumentsSummary").get(0);
            String contractsSummary = this.extractPdfContentsBetweenStrings(downloadedFile, "ContractsSummary", "ContractsSummary").get(0);

            tc.stepEvaluator
                    .add(() -> contractsSummary.contains("ContractType") && contractsSummary.contains("DocumentTitle")
                            && contractsSummary.contains("CLMURL") && contractsSummary.contains("Is Valid"), "Incorrect Contracts Summary metadata.")
                    .add(() -> documentsSummary.contains("QMS") && documentsSummary.contains("SupplierID") && documentsSummary.contains("DocumentLabel")
                            && documentsSummary.contains("DocumentType") && documentsSummary.contains("ValidFrom") && documentsSummary.contains("ValidTo")
                            && documentsSummary.contains("IsValid"), "Incorrect Documents Summary metadata.");

            tc.addStepInfo("""
                            In field audit history section two calculated properties appear
                            Agregation summary\s
                            DocumentsSummary: aggregation of
                            [QMS]
                            [SupplierID]
                            [DocumentLabel][DocumentType][ValidFrom]
                            [ValidTo][IsValid]
                            ContractsSummary: aggregation of [ContractType]
                            [DocumentTitle][CLMURL][IsValid]""", "ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());

            //STEP 13
            tc.browser.switchToWindow(1);
            String date = LocalDate.parse(todayDate, DateTimeFormatter.ofPattern("dd.MM.yyyy")).format(DateTimeFormatter.ofPattern("dd.MM.yy"));
            tc.stepEvaluator.add(() -> tc.table.getItemValueFromColumn(ETable.DOCUMENTS, EColumn.TITLE, title1, EColumn.VALID_TO).equals(date), "Incorrect date in 'Valid To' column")
                            .add(() -> tc.table.isValid(ETable.DOCUMENTS, EColumn.TITLE, title1, EColumn.VALID), "Document is not valid");
            tc.addStepInfo("Information is displayed according to actions done in case", "ok", tc.stepEvaluator.eval(),
                    new ComparerOptions().takeScreenShotPlatform());
        });
    }

    private List<String> extractPdfContentsBetweenStrings(File file, String start, String end)
    {
        List<String> res = new ArrayList<>();
        try
        {
            CorePdfControl pdf = new CorePdfControl();
            String content = pdf.getPdfFileContent(file);
            int startIndex = 0;
            int firstIndex, secondIndex;
            while ((firstIndex = content.indexOf(start, startIndex)) != -1 &&
                    (secondIndex = content.indexOf(end, firstIndex + start.length())) != -1)
            {
                String val = content.substring(firstIndex + start.length(), secondIndex).trim();
                res.add(val);

                startIndex = secondIndex;
            }
        }
        catch(Exception e)
        {
            TcLog.error("Unable to get the contents of the pdf!", e);
        }
        return res;
    }
}
