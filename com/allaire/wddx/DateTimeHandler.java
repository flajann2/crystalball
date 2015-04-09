package com.allaire.wddx;


import com.allaire.wddx.*;
import com.allaire.util.ISO8601;
import java.util.Date;


/**
  * Handler for WDDX dateTime element.
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see org.xml.wddx.WddxElement
  */
class DateTimeHandler extends com.allaire.wddx.WddxElement
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Receive notification of the end of a dateTime element.
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
            throwException("No content for dateTime element");
        }
    }

    /**
     * Receive notification of character data inside a dateTime element.
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
            String dateString = new String(ch, start, length);

            Date dt = ISO8601.parseDate(dateString);
            if (dt == null)
            {
                throwException("invalid date string '" + dateString + "'");
            }

            setValue(dt);
        }
        catch(StringIndexOutOfBoundsException e)
        {
            throwException("Bad character data callback", e);
        }
    }
}