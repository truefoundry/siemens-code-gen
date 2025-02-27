package ControlImplementation.Interface;

import Enums.ETab;
import org.openqa.selenium.NotFoundException;

import java.util.List;

public interface ITabControl
{
    /**
     * Finds and selects a tab based on the specified name.
     *
     * @param name the tab to select.
     * @throws NotFoundException if the tab cannot be located.
     */
    void select(ETab name);

    /**
     * Checks if the specified tab is currently selected.
     *
     * @param name the tab to check.
     * @return true if the tab is selected, false otherwise.
     * @throws NotFoundException if the tab cannot be located.
     */
    boolean isSelected(ETab name);

    /**
     * Retrieves the names of all available tabs in the tab control.
     *
     * @return a list of strings representing the names of the tabs.
     * @throws NotFoundException if no tabs are found.
     */

    List<String> getAllTabsNames();

    /**
     * Checks if a specific Tab exists.
     *
     * @param name The tab to check, represented by an enumeration of type ETab.
     * @return true if the specified Tab exists, false otherwise.
     */
    boolean exists(ETab name);
}
