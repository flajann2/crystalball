package com.allaire.wddx;

import com.allaire.wddx.*;
import java.lang.*;
import java.util.Vector;
import org.xml.sax.*;

/**
  * Handler for WDDX array element.
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see org.xml.wddx.WddxElement
  */
class ArrayHandler extends com.allaire.wddx.WddxElement
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private static final String LENGTH_ATTRIBUTE_NAME = "length";
    
    private Vector m_array = null;


    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Receive notification of the start of an array element.
     *
     * @param name The element type name.
     * @param attributes The specified or defaulted attributes.
     * @exception com.allaire.wddx.WddxDeserializationException Any WDDX exception, possibly
     *            wrapping another exception.
     * @see com.allaire.wddx.WddxElement#onEndElement
     * @see com.allaire.wddx.WddxElement#onCharacters
     * @see com.allaire.wddx.WddxElement#onBeforeChild
     * @see com.allaire.wddx.WddxElement#onAfterChild
     */
    public void onStartElement(String name, AttributeList attributes) throws WddxDeserializationException
    {
        String length = attributes.getValue(LENGTH_ATTRIBUTE_NAME);
        if (length == null)
        {
            throwException(
                "Array element does not have a " +
                LENGTH_ATTRIBUTE_NAME + " attribute.");
        }

        try
        {
            m_array = new Vector(Integer.parseInt(length));
        }
        catch(NumberFormatException e)
        {
            throwException(
                "The " + LENGTH_ATTRIBUTE_NAME + " attribute is not an integer", 
                e);
        }
    }

    /**
     * Receive notification of the end of an array element.
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
        setValue(m_array);
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
        m_array.addElement(childElement.getValue());
    }
}