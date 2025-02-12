package ControlImplementation.Interface;

import Enums.EToggle;

public interface IToggleControl
{
    /**
     * Ensures that the specified toggle is checked (enabled).
     * If the toggle is already checked, no action is performed.
     *
     * @param toggle the toggle element to check
     */
     void check(EToggle toggle);

    /**
     * Ensures that the specified toggle is unchecked (disabled).
     * If the toggle is already unchecked, no action is performed.
     *
     * @param toggle the toggle element to uncheck
     */
     void uncheck(EToggle toggle);

    /**
     * Checks if the specified toggle element exists on the page.
     *
     * @param toggle the toggle element to check for existence
     * @return true if the toggle exists, false otherwise
     */
     boolean exists(EToggle toggle);


}
