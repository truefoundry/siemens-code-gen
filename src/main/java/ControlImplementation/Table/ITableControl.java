package ControlImplementation.Table;

import Enums.EColumn;
import Enums.ETable;

import java.util.List;

public interface ITableControl
{
    /**
     * Check table is exist
     * @param table wanted table
     * @return true if the item is found otherwise false
     */
    boolean exists(ETable table);

    /**
     * Clicks on Item in table
     *
     * @param table           wanted table
     * @param column          wanted column
     * @param itemNameOrIndex item name or index start with '#'
     */
    void itemClick(ETable table, EColumn column, String itemNameOrIndex);

    /**
     * Get Names of Columns in the table.
     *
     * @param table wanted table
     * @return Names of columns as list of strings
     */
    List<String> getColumnNames(ETable table);

    /**
     * Returns column index. In case, columns are divided into column groups, a parent column can be specified.
     *
     * @param table        Wanted table.
     * @param column       Wanted column.
     * @param parentColumn Parent column(If available).
     * @return Column index.
     */
    int getColumnIndex(ETable table, EColumn column, EColumn... parentColumn);

    /**
     * Returns all the items from the wanted column.
     *
     * @param table     {@link ETable}
     * @param column    {@link EColumn}
     * @return          returns column items.
     */
    public List<String> getItemsFromColumn(ETable table, EColumn column);

    /**
     * sends value to the edit box in the table
     *
     * @param column    {@link EColumn}
     * @param rowIndex  Row index (starts from 0)
     * @param value     value to be set in the edit box
     */
    public void sendKeys(ETable table, EColumn column, int rowIndex, String value);

    /**
     * Checks whether the wanted cell is editable or not
     * @param table
     * @param column
     * @param rowIndex
     * @return true if editable, if not returns false
     */
    boolean isEditable(ETable table, EColumn column, int rowIndex);

    /**
     * return the existed value inside the table
     * @param column
     * @param rowIndex
     * @return string of existing value
     */
    String getValue(ETable table, EColumn column, int rowIndex);

    void scrollToColumn(ETable table, EColumn column);
}
