package com.allaire.util;


import java.util.Hashtable;
import java.util.Vector;


/**
 * Simple RecordSet implementation
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
 * @see com.allaire.util.RecordSet
 * @see java.sql.ResultSet
 */
public class SimpleRecordSet extends java.lang.Object implements com.allaire.util.RecordSet
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private static final int INITIAL_COLUMN_NUMBER = 8;
    
    private int m_colCount = 0;
    private int m_rowCount = 0;
    private String[] m_colNames = new String[INITIAL_COLUMN_NUMBER];
    private Vector[] m_colData = new Vector[INITIAL_COLUMN_NUMBER];
        
 
    ///////////////////////////////////////////////////////////////////////
    //
    // Construction/Finalization
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Constructs an empty recordset
     */
    public SimpleRecordSet()
    {
        m_colCount = 0;
        m_rowCount = 0;
        m_colNames = new String[INITIAL_COLUMN_NUMBER];
        m_colData = new Vector[INITIAL_COLUMN_NUMBER];
    }
    
    
    /**
     * Constructs a recordset with a given row size
     *
     * @param rows Row size
     * @exception com.allaire.util.NegativeRowNumberException
     *      Signals that a negative row number was provided
     */
    public SimpleRecordSet(int rows) throws NegativeRowNumberException
    {
        if (rows < 0)
        {
            throw new NegativeRowNumberException();
        }
        
        m_colCount = 0;
        m_rowCount = rows;
        m_colNames = new String[INITIAL_COLUMN_NUMBER];
        m_colData = new Vector[INITIAL_COLUMN_NUMBER];
    }
    

    /**
     * Constructs a recordset with a given row size and certain columns
     *
     * @param rows Row size
     * @param columns Columns to add to this recordset
     * @exception com.allaire.util.NegativeRowNumberException
     *      Signals that a negative row number was provided
     * @exception com.allaire.util.DuplicateColumnNameException
     *      Signals that a duplicate column name was provided
     */
    public SimpleRecordSet(int rows, String[] columns) throws NegativeRowNumberException, DuplicateColumnNameException
    {
        if (rows < 0)
        {
            throw new NegativeRowNumberException();
        }
        
        m_rowCount = rows;        
        m_colCount = 0;
        m_colNames = new String[columns.length];
        m_colData = new Vector[columns.length];
        
        for (int i = 0; i < columns.length; ++i)
        {
            addColumn(columns[i]);
        }
    }
    
    
    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Counts the number of rows in this recorset
     *
     * @return Number of rows in this recordset
     */
    public int getRowCount()
    {
        return m_rowCount;
    }
    

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
    public int addRows(int rowsToAdd) throws NegativeRowNumberException
    {
        if (rowsToAdd < 0)
        {
            throw new NegativeRowNumberException();
        }
        
        for (int i = 0; i < m_colCount; ++i)
        {
            for (int j = 0; j < rowsToAdd; ++j)
            {
                m_colData[i].addElement(null);
            }
        }
        
        m_rowCount += rowsToAdd;
        
        return m_rowCount;
    }
    
    
    /**
     * Counts the number of column in this recorset
     *
     * @return Number of columns in this recordset
     */
    public int getColumnCount()
    {
        return m_colCount;
    }
    
    
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
     * @see #addColumn(java.lang.String, java.util.Vector)
     */    
    public int addColumn(String columnName) throws DuplicateColumnNameException
    {
        Vector columnData = new Vector(m_rowCount);
        columnData.setSize(m_rowCount);
        
        try
        {
            return addColumn(columnName, columnData);
        }
        catch(RowSizeMismatchException e)
        {
            // This should never happen because we use the correct row number
            // If it does :), simulate a plausible error
            throw new DuplicateColumnNameException();
        }
    }
    
    
    /**
     * Adds specific column data to this recordset
     *
     * @return Index of added column
     * @param columnName Name of column to add
     * @param columnData Column data to add
     * @exception com.allaire.util.DuplicateColumnNameException
     *      Signals that this recordset already has a column by this name
     * @exception com.allaire.util.RowSizeMismatchException
     *      Signals that the number of elements in columnData does not 
     *      match the row count of the recordset
     * @see #findColumn
     * @see #addColumn(java.lang.String)
     */    
    public int addColumn(String columnName, Vector columnData) throws DuplicateColumnNameException, RowSizeMismatchException
    {
        // Check for row size mismatch
        if (columnData.size() != m_rowCount)
        {
            throw new RowSizeMismatchException();
        }
        
        // Check for duplicate column names
        int colId = findColumn(columnName);
        if (colId != NOT_FOUND)
        {
            throw new DuplicateColumnNameException();
        }
        else
        {
            // Resize data structures, if necessary
            if (m_colNames.length <= m_colCount)
            {
                // Need to reallocate the arrays
                
                String[] tempColNames = new String[2 * m_colNames.length];
                Vector[] tempColData = new Vector[2 * m_colNames.length];
                
                for (int i = 0; i < m_colCount; ++i)
                {
                    tempColNames[i] = m_colNames[i];
                    tempColData[i] = m_colData[i];
                }
                
                m_colNames = tempColNames;
                m_colData = tempColData;
            }
            
            // Determine new column id
            colId = m_colCount;
            
            // This is a new column, record its name
            m_colNames[colId] = columnName;

            // Increase column count
            ++m_colCount;        
        }
        
        // Add column data
        m_colData[colId] = columnData;

        // Return column id
        return colId;
    }
    
    
    /**
     * Finds a column in this recordset
     *
     * @return Index of column, or <a href="com.allaire.util.RecordSet.html#NOT_FOUND">NOT_FOUND</a>
     * @param columnName Name of column to search for
     * @see #addColumn(java.lang.String)
     */    
    public int findColumn(String columnName)
    {
        int i = 0;
        while (i < m_colCount && !m_colNames[i].equals(columnName))
        {
            ++i;
        }
        
        if (i != m_colCount)
        {
            return i;
        }
        else
        {
            return NOT_FOUND;
        }
    }
    
    
    /**
     * Retrieves the column names in this recordset
     *
     * @return Array of the column names at their respective column indexes
     */    
    public String[] getColumnNames()
    {
        String[] colNames = new String[m_colCount];
        for (int i = 0; i < m_colCount; ++i)
        {
            colNames[i] = m_colNames[i];
        }
        return colNames;
    }
    
    
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
    public Object getField(int row, int column) throws InvalidRowIndexException, InvalidColumnIndexException
    {
        if (row < 0 || row >= m_rowCount)
        {
            throw new InvalidRowIndexException();
        }

        if (column < 0 || column >= m_colCount)
        {
            throw new InvalidColumnIndexException();
        }
        
        return m_colData[column].elementAt(row);
    }
    
    
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
    public void setField(int row, int column, Object value) throws InvalidRowIndexException, InvalidColumnIndexException
    {
        if (row < 0 || row >= m_rowCount)
        {
            throw new InvalidRowIndexException();
        }

        if (column < 0 || column >= m_colCount)
        {
            throw new InvalidColumnIndexException();
        }
        
        m_colData[column].setElementAt(value, row);
    }
    
    /**
     * Returns a string representation of the recordset
     *
     * @returns A string representation of the recordset
     */
    public String toString()
    {
		int rowCount = getRowCount();
		int columnCount = getColumnCount();
		String[] columnNames = getColumnNames();
        
        StringBuffer sb = new StringBuffer("[com.allaire.util.RecordSet (rows ");
        sb.append(rowCount);
        sb.append(" columns ");
        
        for (int column = 0; column < columnCount; ++column)
        {
            if (column > 0)
            {
                sb.append(", ");
            }
            sb.append(columnNames[column]);
        }
        
        sb.append("): ");
        
		for (int column = 0; column < columnCount; ++column)
		{
			sb.append("[");
			sb.append(columnNames[column]);
			sb.append(": ");
			sb.append(m_colData[column].toString());
			sb.append("] ");
		}
		
		sb.append("]");
		
		return sb.toString();
    }
}
