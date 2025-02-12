package ControlImplementation.Table;

import CompositionRoot.IocBuilder;
import ControlImplementation.BrowserControl;
import Enums.EColumn;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.*;
import fate.core.Enums.EType;
import fate.core.PerfMon.PerfMon;
import org.openqa.selenium.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class UserTable implements ITableImplementation
{
    private final CoreCssControl css;
    private WebElement table;
    private final By selector;

    protected UserTable(WebElement tableElement)
    {
        this.table = tableElement;
        this.css = IocBuilder.getContainer().getComponent(CoreCssControl.class);
        this.selector = DomUtils.getElementsSelector(this.table);
    }

    protected WebElement findControl()
    {
        this.table = css.findControl(selector);
        if(this.table != null) TcLog.info("AgTable found.");
        return this.table;
    }

    public List<String> getColumnNames()
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        List<String> columnNames = new ArrayList<>();
        try
        {
            if(table != null)
            {
                List<WebElement> headersRow = this.getAllHeaderRows();
                if(headersRow.size() > 1)
                {
                    for(WebElement col : headersRow)
                    {
                        columnNames.add(col.getText());
                    }
                }
            }
            else
            {
                TcLog.warning("Table not found!");
            }
        }
        catch(NullPointerException  e)
        {
            TcLog.warning("Unable to get column names! " + e);
        }
        return columnNames;
    }

    private List<WebElement> getAllHeaderRows()
    {
        try
        {
            if(table != null)
            {
                List<WebElement> headers = css.findControlsWithRoot(By.cssSelector("div[class=\"user__table__header__column\"]"), table);
                if(!headers.isEmpty())
                {
                    return headers;
                }
                else
                {
                    TcLog.warning("Header rows for table not found!");
                }
            }
            else
            {
                TcLog.warning("Table not found!");
            }
        }
        catch(NullPointerException e)
        {
            TcLog.warning("Unable to get all columns! " + e);
        }

        return new ArrayList<>();
    }


    public int getColumnIndex(EColumn column)
    {
        return column.getType().equals(EType.INDEX) ? column.getIndex() : this.getColumnNames().indexOf(column.getValue());
    }

    /**
     * gets all the row elements(header row is excluded)
     *
     * @return list of row elements
     */
    private List<WebElement> getRows()
    {
        List<WebElement> rows = new ArrayList<>();
        try
        {
            if(table != null)
            {
                rows.addAll(css.findControlsWithRoot(By.cssSelector("div[class='user__table__body__row ng-star-inserted']"), table));
                rows = rows.stream().filter(r -> !r.getAttribute("slot").equals("header")).toList();
            }
            else
            {
                TcLog.warning("Table not found");
            }
        }
        catch(Exception e)
        {
            TcLog.error("Unable to fetch rows from table '%s'".formatted(table));
        }
        return rows;
    }

    /**
     * finds all the webElements of the wanted column
     *
     * @param column {@link EColumn}
     * @return list of webElements
     */
    private List<WebElement> getElementsFromColumn(EColumn column)
    {
        int columnIndex = this.getColumnIndex(column);
        return this.getRows().stream().map(row -> css.findControlsWithRoot(By.cssSelector("div[class*='user__table__body__column']"), row).get(columnIndex)).toList();
    }

    public List<String> getItemsFromColumn(EColumn column)
    {
        return this.getElementsFromColumn(column).stream().map(i -> i.getAttribute("innerText")).toList();
    }

    private int getItemsIndex(EColumn column, String itemNameOrIndex)
    {
        try
        {
            if(itemNameOrIndex.startsWith("#"))
            {
                return Integer.parseInt(itemNameOrIndex.split("#")[1]);
            }
            return this.getItemsFromColumn(column).indexOf(itemNameOrIndex);
        }
        catch(NumberFormatException e)
        {
            TcLog.error("Wanted item '%s' was not found".formatted(itemNameOrIndex), e);
        }
        return -1;
    }

    private WebElement getCellElement(EColumn column, int index)
    {
        WebElement cell = null;
        try
        {
            List<WebElement> colElements= this.getElementsFromColumn(column);
            cell = colElements.get(index);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            TcLog.warning("Unable to find element at index '%s'".formatted(index));
        }
        catch(Exception e)
        {
            TcLog.error("Unable to find wanted cell", e);
        }
        return cell;
    }

    public boolean exists()
    {
        return table.isDisplayed();
    }

    @Override
    public void itemClick(EColumn column, String itemNameOrIndex)
    {
        try
        {
            WebElement wantedItem;
            List<WebElement> elements = this.getElementsFromColumn(column);
            if(itemNameOrIndex.startsWith("#"))
            {
                int i = Integer.parseInt(itemNameOrIndex.split("#")[1]);
                wantedItem= elements.get(i);
            }
            else
            {
                wantedItem = elements.stream().filter(x -> x.getText().equalsIgnoreCase(itemNameOrIndex)).findFirst().orElse(null);
            }
            if(wantedItem != null)
            {
                new CoreMouseControl().moveOverControl(wantedItem);
                wantedItem.click();
                TcLog.info("Item '%s' clicked".formatted(itemNameOrIndex));
                BrowserControl.waitForLoadingIndicator();
            }
            else
            {
                TcLog.warning("Item '%s' not found!".formatted(itemNameOrIndex));
            }
        }
        catch (StaleElementReferenceException e)
        {
            TcLog.warning("Element Staled, Retrying...", e);
            WaitFor.specificTime(Duration.ofSeconds(2));
            this.itemClick(column, itemNameOrIndex);
        }
        catch (NotFoundException | NullPointerException e)
        {
            TcLog.error("Unexpected Error", e);
        }
    }
}
