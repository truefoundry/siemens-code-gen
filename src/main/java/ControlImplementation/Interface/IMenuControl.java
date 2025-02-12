package ControlImplementation.Interface;

import Enums.EMenu;

public interface IMenuControl
{
    /**
     * opens the wanted menu
     * @param menu wanted Item
     */
    void select(EMenu menu);

    /**
     * @param menu wanted Item
     * @return true if menu is present otherwise false
     */
    boolean exists(EMenu menu);

    /**
     * Selects an item from a dropdown menu.
     *
     * @param menu The menu to be opened, represented by an enumeration of type EMenu.
     * @param item The name of the item to select from the dropdown menu.
     */
    void selectFromDropDown(EMenu menu, String item);
}
