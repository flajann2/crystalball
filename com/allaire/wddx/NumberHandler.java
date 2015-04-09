package com.allaire.wddx;


import com.allaire.wddx.*;


/**
  * Handler for WDDX number element.
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see org.xml.wddx.WddxElement
  */
class NumberHandler extends com.allaire.wddx.WddxElement 
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Receive notification of the end of a number element.
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
        if (getValue() == null)
        {
            throwException("No content for number element");
        }
    }
    /**
     * Receive notification of character data inside a number element.
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
        try
        {
            setValue(new Double(new String(ch, start, length)));
        }
        catch(StringIndexOutOfBoundsException e)
        {
            throwException("Bad character data callback", e);
        }
        catch(NumberFormatException e)
        {
            throwException("Invalid contents of number element", e);
        }
    }
}