package com.allaire.util;


/**
 * Recordset with random row/column access
 *
 * <p>RecordSet can be used to manage and manipulate tabular
 * data. It does not impose cursor style processing in the 
 * way java.sql.ResultSet does.</p>
 *
 * <p>Note:<ul>
 *      <li>Recordset column names are case-sensitive</li>
 *      <li>Rows are numbered starting from zero</li>
 *      <li>Columns are numbered starting from zero</li></ul></p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see java.sql.ResultSet
 */
public interface RecordSet
{
    /**
     * Constant value returned by findColumn as a failure indicator.
     *
     * @see #findColumn
     */
    public static final int NOT_FOUND = -1;
    
    
    /**
     * Counts the number of rows in this recorset
     *
     * @return Number of rows in this recordset
     */
    public int getRowCount();
    

    /**
     * Adds rows to this recordset
     *
     * <p>All field values in the new rows are set to null.</p>
     *
     * @return Current row count
     * @param rowsToAdd Number of rows to add
     * @exception com.allaire.util.NegativeRowNumberException
     *      Signals that rowsToAdd is negative
     */    
    public int addRows(int rowsToAdd) throws NegativeRowNumberException;

    
    /**
     * Counts the number of column in this recorset
     *
     * @return Number of columns in this recordset
     */
    public int getColumnCount();
    
    
    /**
     * Adds a column to this recordset
     *
     * <p>All fields of the added column are set to null</p>
     *
     * @return Index of added column
     * @param columnName Name of column to add
     * @exception com.allaire.util.DuplicateColumnNameException
     *      Signals that this recordset already has a column by this name
     * @see #findColumn
     */    
    public int addColumn(String columnName) throws DuplicateColumnNameException;
    
    
    /**
     * Finds a column in this recordset
     *
     * @return Index of column, or <a href="#NOT_FOUND">NOT_FOUND</a>
     * @param columnName Name of column to search for
     * @see #addColumn(java.lang.String)
     */    
    public int findColumn(String columnName);
    
    
    /**
     * Retrieves the column names in this recordset
     *
     * @return Array of the column names at their respective column indexes
     */    
    public String[] getColumnNames();
    
    
    /**
     * Retrieves the field value in a given row/column position
     *
     * @return Field value
     * @param row Row number
     * @param column Column index
     * @exception com.allaire.util.InvalidRowIndexException
     *      Signals that an invalid row number was provided
     * @exception com.allaire.util.InvalidColumnIndexException
     *      Signals that an invalid column index was provided
     * @see #setField
     */    
    public Object getField(int row, int column) throws InvalidRowIndexException, InvalidColumnIndexException;
    
    
    /**
     * Sets the field value in a given row/column position
     *
     * @param row Row number
     * @param column Column index
     * @param value Field value
     * @exception com.allaire.util.InvalidRowIndexException
     *      Signals that an invalid row number was provided
     * @exception com.allaire.util.InvalidColumnIndexException
     *      Signals that an invalid column index was provided
     * @see #getField
     */    
    public void setField(int row, int column, Object value) throws InvalidRowIndexException, InvalidColumnIndexException;
}