package com.allaire.wddx;


import com.allaire.wddx.*;
import java.lang.*;
import java.util.Hashtable;
import org.xml.sax.*;


/**
  * Handler for WDDX var element.
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see org.xml.wddx.WddxElement
  */
class VarHandler extends com.allaire.wddx.WddxElement
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


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
        // Propagate value upwards
        setValue(childElement.getValue());
    }
}