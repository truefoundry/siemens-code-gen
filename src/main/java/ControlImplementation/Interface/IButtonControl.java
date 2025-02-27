package ControlImplementation.Interface;

import Enums.EButton;

public interface IButtonControl
{
    /**
     * Clicks on the wanted button.
     *
     * @param button  {@link EButton}
     */
    void click(EButton button);

    /**
     * @param button
     * @return true if button exists, otherwise false
     */
    boolean exists(EButton button);
}
