package ControlImplementation.Table;

import Enums.EColumn;
import fate.core.CompositionRoot.TcLog;

import java.util.ArrayList;
import java.util.List;

public interface ITableImplementation
{
    /**
     * Clicks on Item in table
     *
     * @param column          wanted column
     * @param itemNameOrIndex item name or index start with '#'
     */
    default void itemClick(EColumn column, String itemNameOrIndex)
    {
        TcLog.warning("Not implemented!");
    }

    /**
     * Get Names of Columns in the table.
     *
     * @return Names of columns as list of strings
     */
    default List<String> getColumnNames()
    {
        TcLog.warning("Not implemented!");
        return new ArrayList<>();
    }

    default List<String> getItemsFromColumn(EColumn column)
    {
        TcLog.warning("Not implemented!");
        return new ArrayList<>();
    }

    /**
     * gets the index of wanted column
     *
     * @param column {@link EColumn}
     * @return index
     */
    default int getColumnIndex(EColumn column)
    {
        return -1;
    }

    /**
     * sends value to the edit box in the table
     *
     * @param column    {@link EColumn}
     * @param rowIndex  Row index (starts from 0)
     * @param value     value to be set in the edit box
     */
    default void sendKeys(EColumn column, int rowIndex, String value) {}

    /**
     * Checks whether the wanted cell is editable or not
     * @param column
     * @param rowIndex
     * @return true if editable, if not returns false
     */
    default boolean isEditable(EColumn column, int rowIndex) {return false;}

    /**
     * return the existed value inside the table
     * @param column
     * @param rowIndex
     * @return string of existing value
     */
    default String getValue( EColumn column, int rowIndex) {return "";}

    default boolean exists(){return false;}

    default void scrollToColumn(EColumn column){};
}
