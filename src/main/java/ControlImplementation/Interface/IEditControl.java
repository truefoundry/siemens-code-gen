package ControlImplementation.Interface;

import Enums.EComboBox;
import Enums.EEdit;

public interface IEditControl
{
    /**
     * Sets wanted text to wanted edit control.
     *
     * @param name  Name of wanted control.
     * @param text  Text to set.
     * @param clear Clears current text before setting new one.
     */
    void sendKeys(EEdit name, String text, boolean... clear);

    /**
     * finds whether wanted edit is present or not
     * @param name Name of the wanted control
     * @return true if edit is present, false otherwise
     */
    boolean isEditable(EEdit name);

    /**
     * @param name Name of wanted item
     * @return true if the combo is present, false otherwise.
     */
    boolean exists(EEdit name);

}
