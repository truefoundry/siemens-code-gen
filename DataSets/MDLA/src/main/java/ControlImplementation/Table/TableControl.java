package ControlImplementation.Table;

import Enums.EColumn;
import Enums.ETable;
import fate.core.CompositionRoot.CoreIocBuilder;
import fate.core.CompositionRoot.TcLog;
import fate.core.ControlImplementations.CoreCssControl;
import fate.core.Interfaces.IInteractableNaming;
import fate.core.PerfMon.PerfMon;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

import javax.annotation.CheckForNull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableControl implements ITableControl
{
    private final TableFactory factory;
    private final CoreCssControl css;

    public TableControl()
    {
        css = CoreIocBuilder.getContainer().getComponent(CoreCssControl.class);
        this.factory = new TableFactory();
    }

    @CheckForNull
    protected ITableImplementation findControl(IInteractableNaming name, By... rootElement)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));

        try
        {
            ITableImplementation res = switch(name.getType())
            {
                case INDEX ->
                {
                    TcLog.warning("INDEX Not Implemented");
                    yield null;
                }
                case SELECTOR -> this.factory.getTableImpl(css.findControl(name.getSelector()));
                case NAME ->
                {
                    TcLog.warning("NAME Not Implemented!");
                    yield null;
                }
            };

            pm.stop();
            return res;
        }
        catch(StaleElementReferenceException e)
        {
            pm.abort();
            TcLog.debug("Table is staled...!", e);
            this.findControl(name, rootElement);
        }
        catch(NullPointerException | IndexOutOfBoundsException e)
        {
            TcLog.warning("Unable to find table control with name '%s'! ".formatted(name.getValue()) + e);
            pm.abort();
        }
        catch(NoSuchElementException e)
        {
            TcLog.warning("Could not resize the table!");
        }

        TcLog.warning("Table '" + name.getValue() + "' was not found!");
        return null;
    }

    @Override
    public boolean exists(ETable table)
    {
        boolean isVisible = false;
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        final ITableImplementation tbl = this.findControl(table);
        if(tbl != null)
        {
            isVisible = tbl.exists();
        }
        pm.stop();
        return isVisible;
    }

    @Override
    public void itemClick(ETable table, EColumn column, String itemNameOrIndex)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        final ITableImplementation tbl = this.findControl(table);
        if(tbl != null) tbl.itemClick(column, itemNameOrIndex);
        pm.stop();
    }

    @Override
    public List<String> getColumnNames(ETable table)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        final ITableImplementation tbl = this.findControl(table);
        List<String> items = tbl != null ? tbl.getColumnNames() : new ArrayList<>();
        pm.stop();
        TcLog.action("Table '%s' contains following columns:%s".formatted(table.getValue(), Arrays.toString(items.toArray())));
        return items;
    }

    @Override
    public int getColumnIndex(ETable table, EColumn column, EColumn... parentColumn)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        final ITableImplementation tbl = this.findControl(table);
        int index = tbl != null ? tbl.getColumnIndex(column) : -1;
        TcLog.action("Column '%s' is found at index '%s'".formatted(column.getValue(), index));
        pm.stop();
        return index;
    }


    @Override
    public List<String> getItemsFromColumn(ETable table, EColumn column)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        final ITableImplementation tbl = this.findControl(table);
        List<String> items = tbl != null ? tbl.getItemsFromColumn(column) : new ArrayList<>();
        TcLog.action("Column '%s' contains following items:%s".formatted(column.getValue(), Arrays.toString(items.toArray())));
        pm.stop();
        return items;
    }

    @Override
    public void sendKeys(ETable table, EColumn column, int rowIndex, String value)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        final ITableImplementation tbl = this.findControl(table);
        if( tbl != null ) tbl.sendKeys(column, rowIndex, value);
    }
    @Override
    public boolean isEditable(ETable table, EColumn column, int rowIndex)
    {
        PerfMon pm = new PerfMon();
        boolean result = false;
        pm.watch(Duration.ofSeconds(2));
        final ITableImplementation tbl = this.findControl(table);
        if( tbl != null )
        {
            result = tbl.isEditable(column, rowIndex);
        }
        pm.stop();
        return result;
    }

    public String getValue(ETable table, EColumn column, int rowIndex)
    {
        PerfMon pm = new PerfMon();
        String result = "";
        pm.watch(Duration.ofSeconds(2));
        final ITableImplementation tbl = this.findControl(table);
        if( tbl != null )
        {
            result = tbl.getValue(column, rowIndex);
        }
        pm.stop();
        return result;
    }

    public void scrollToColumn(ETable table, EColumn column)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        final ITableImplementation tbl = this.findControl(table);
        if( tbl != null )
        {
           tbl.scrollToColumn(column);
        }
        pm.stop();
    }
}
