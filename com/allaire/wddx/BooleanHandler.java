package com.allaire.wddx;


import com.allaire.wddx.*;
import java.lang.*;
import org.xml.sax.*;


/**
  * Handler for WDDX boolean element.
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see org.xml.wddx.WddxElement
  */
class BooleanHandler extends com.allaire.wddx.WddxElement
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private static final String VALUE_ATTRIBUTE_NAME = "value";
    private static final String VALUE_TRUE = "true";
    private static final String VALUE_FALSE = "false";

 
    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Receive notification of the start of a boolean element.
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
        String value = attributes.getValue(VALUE_ATTRIBUTE_NAME);
        if (value == null)
        {
            throwException(
                "Boolean element missing " + VALUE_ATTRIBUTE_NAME + 
                " attribute");
        }

        if (value.equals(VALUE_TRUE))
        {
            setValue(new Boolean(true));
        }
        else if (value.equals(VALUE_FALSE))
        {
            setValue(new Boolean(false));
        }
        else
        {
            throwException(
                "Value attribute of boolean element must be '" + VALUE_TRUE + 
                "' or '" + VALUE_FALSE + "'");
        }
    }
}