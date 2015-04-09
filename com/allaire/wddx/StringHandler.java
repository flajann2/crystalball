package com.allaire.wddx;


import com.allaire.wddx.*;
import java.lang.*;
import org.xml.sax.*;


/**
  * Handler for WDDX string element.
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see org.xml.wddx.WddxElement
  */
class StringHandler extends com.allaire.wddx.WddxElement
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private static final String CHAR_ELEMENT_NAME = "char";
    private static final String CODE_ATTRIBUTE_NAME = "code";
    
    private StringBuffer m_buffer = new StringBuffer();


    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Receive notification of the end of a string element.
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
        setValue(m_buffer.toString());
    }


    /**
     * Receive notification of character data inside a string element.
     *
     * @param ch The characters.
     * @param start The start position in the character array.
     * @param length The number of characters to use from the
     *               character array.
     * @exception com.allaire.wddx.WddxDeserializationException Any WDDX exception, possibly
     *            wrapping another exception.
     * @see com.allaire.wddx.WddxElement#onStartElement
     * @see com.allaire.wddx.WddxElement#onEndElement
     * @see com.allaire.wddx.WddxElement#onBeforeChild
     * @see com.allaire.wddx.WddxElement#onAfterChild
     */
    public void onCharacters(char ch[], int start, int length) throws WddxDeserializationException
    {
        m_buffer.append(ch, start, length);
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
        if (! name.equals(CHAR_ELEMENT_NAME))
        {
            throwException("Invalid element " + name + " inside a string element");
        }

        String code = attributes.getValue(CODE_ATTRIBUTE_NAME);
        if (code == null)
        {
            throwException("code attribute is not present for element char");
        }

        try
        {
            m_buffer.append((char)Integer.parseInt(code, 16));
        }
        catch(NumberFormatException e)
        {
            throwException("Bad character code in char element code= attribute.", e);
        }
    }
}