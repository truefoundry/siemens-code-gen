package ControlImplementation.Interface;

import Enums.ECheckBox;

public interface ICheckBoxControl
{
    /**
     * This method checks if a checkbox is currently checked.
     *
     * @param checkBox the checkbox element to check
     * @return true if the checkbox is checked, false otherwise
     */
    boolean isChecked(ECheckBox checkBox);

    /**
     * This method checks the checkbox if it's not already checked.
     *
     * @param checkBox the checkbox element to check
     */
    void check(ECheckBox checkBox);

    /**
     * This method unchecks the checkbox if it's currently checked.
     *
     * @param checkBox the checkbox element to uncheck
     */
    void uncheck(ECheckBox checkBox);

    /**
     * This method verifies if a checkbox exists on the page.
     *
     * @param checkBox the checkbox element to verify
     * @return true if the checkbox exists, false otherwise
     */
    boolean exists(ECheckBox checkBox);

    /**
     * Checks if the checkbox can be checked, i.e., it's not disabled.
     *
     * @param checkBox the checkbox element to check
     * @return true if the checkbox can be checked, false if it's disabled
     */
    boolean isDisabled(ECheckBox checkBox);
}
