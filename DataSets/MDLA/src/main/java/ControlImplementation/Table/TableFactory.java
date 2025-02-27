package ControlImplementation.Table;

import fate.core.CompositionRoot.TcLog;
import fate.core.PerfMon.PerfMon;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public class TableFactory
{
    protected ITableImplementation getTableImpl(WebElement tableElement)
    {
        PerfMon pm = new PerfMon();
        pm.watch(Duration.ofSeconds(2));
        if(tableElement == null)
        {
            TcLog.warning("No table implementation returned, source element is null!");
            return null;
        }

        if(tableElement.getTagName().equals("table"))
        {
                pm.stop();
                return new AppTable(tableElement);
        }

        String classAtt = tableElement.getAttribute("class");
        if(classAtt != null && classAtt.contains("user__table"))
        {
            pm.stop();
            return new UserTable(tableElement);
        }

        pm.stop();
        TcLog.error("Unsupported type '" + tableElement.getTagName() + "'!");
        return null;
    }
}
