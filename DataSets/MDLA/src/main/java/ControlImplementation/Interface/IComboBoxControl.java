package ControlImplementation.Interface;

import Enums.EComboBox;

public interface IComboBoxControl
{
    /**
     * Selects wanted item in wanted combobox.
     *
     * @param comboBox  Wanted combobox.
     * @param item Item to select.
     * @return Name of selected item.
     */
    String select(EComboBox comboBox, String item);

    /**
     * @param name Name of wanted item
     * @return true if the combo is present, false otherwise.
     */
    boolean exists(EComboBox name);
}
