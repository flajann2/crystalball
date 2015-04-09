package com.allaire.wddx;


import com.allaire.wddx.*;
import java.lang.*;
import java.util.Vector;
import org.xml.sax.*;
import com.allaire.util.*;


/**
  * Handler for WDDX recordset element.
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see org.xml.wddx.WddxElement
  */
class RecordsetHandler extends com.allaire.wddx.WddxElement
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private static final String ROWCOUNT_ATTRIBUTE_NAME = "rowCount";
    private static final String FIELDNAMES_ATTRIBUTE_NAME = "fieldNames";
    private static final String FIELD_ELEMENT_NAME = "field";
    private static final String NAME_ATTRIBUTE_NAME = "name";
    
    private SimpleRecordSet m_rs = null;
    private String m_currentFieldName = null;

    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    public void onStartElement(String name, AttributeList attributes) throws WddxDeserializationException
    {
        // Check for the presence of a rowCount attribute
        String rowCount = attributes.getValue(ROWCOUNT_ATTRIBUTE_NAME);
        if (rowCount == null)
        {
            throwException(
                "recordset element does not have a " +
                ROWCOUNT_ATTRIBUTE_NAME + " attribute.");
        }

        // Check for the presence of a fieldNames attribute
        String fieldNames = attributes.getValue(FIELDNAMES_ATTRIBUTE_NAME);
        if (fieldNames == null)
        {
            throwException(
                "recordset element does not have a " +
                FIELDNAMES_ATTRIBUTE_NAME + " attribute.");
        }

        try
        {
            // Build the recordset
            m_rs = new SimpleRecordSet(Integer.parseInt(rowCount));
        }
        catch(NumberFormatException e)
        {
            throwException(
                "The " + ROWCOUNT_ATTRIBUTE_NAME + " attribute is not an integer", 
                e);
        }
        catch(NegativeRowNumberException e)
        {
            throwException(
                "The " + ROWCOUNT_ATTRIBUTE_NAME + " attribute has a negative value", 
                e);
        }
    }

    /**
     * Receive notification of the end of a recordset element.
     *
     * @exception com.allaire.wddx.WddxDeserializationException Any WDDX exception, possibly
     *            wrapping another exception.
     * @see com.allaire.wddx.WddxElement#onStartElement
     * @see com.allaire.wddx.WddxElement#onCharacters
     * @see com.allaire.wddx.WddxElement#onBeforeChild
     * @see com.allaire.wddx.WddxElement#onAfterChild
     */
    public void onEndElement() throws WddxDeserializationException
    {
        setValue(m_rs);
    }

    /**
     * Receive notification of the start of a child element.
     *
     * @param name The element type name.
     * @param attributes The specified or defaulted attributes.
     * @exception com.allaire.wddx.WddxDeserializationException Any WDDX exception, possibly
     *            wrapping another exception.
     * @see com.allaire.wddx.WddxElement#onStartElement
     * @see com.allaire.wddx.WddxElement#onEndElement
     * @see com.allaire.wddx.WddxElement#onCharacters
     * @see com.allaire.wddx.WddxElement#onAfterChild
     */
    public void onBeforeChild(String name, AttributeList attributes) throws WddxDeserializationException
    {
        // Verify that the child name is field
        if (!name.equals(FIELD_ELEMENT_NAME))
        {
            throwException(
                "Only " + FIELD_ELEMENT_NAME +
                " elements can be nested inside a recordset element");
        }

        // Check for the presence of a name attribute
        m_currentFieldName = attributes.getValue(NAME_ATTRIBUTE_NAME);
        if (m_currentFieldName == null)
        {
            throwException(
                FIELD_ELEMENT_NAME + " element does not have a " +
                NAME_ATTRIBUTE_NAME + " attribute.");
        }
    }

    /**
     * Receive notification past the end tag processing of a child element.
     *
     * @param childElement The child element.
     * @exception com.allaire.wddx.WddxDeserializationException Any WDDX exception, possibly
     *            wrapping another exception.
     * @see com.allaire.wddx.WddxElement#onStartElement
     * @see com.allaire.wddx.WddxElement#onEndElement
     * @see com.allaire.wddx.WddxElement#onCharacters
     * @see com.allaire.wddx.WddxElement#onBeforeChild
     */
    public void onAfterChild(WddxElement childElement) throws WddxDeserializationException
    {
        try
        {
            m_rs.addColumn(m_currentFieldName, (Vector)childElement.getValue());
        }
        catch(ClassCastException e)
        {
            // This is an internal error
            throw new WddxDeserializationException(
                "Invalid cast exception for field element result type",
                e);
        }
        catch(DuplicateColumnNameException e)
        {
            throw new WddxDeserializationException(
                "Duplicate field name " + m_currentFieldName,
                e);
        }
        catch(RowSizeMismatchException e)
        {
            throw new WddxDeserializationException(
                "Invalid number of rows in field. There must be " +
                new Integer(m_rs.getRowCount()).toString() + " rows",
                e);
        }
    }
}