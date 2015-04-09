package com.allaire.wddx;


/**
  * Encapsulate a general WDDX deserialization error or warning.
  *
  * <p>This class can contain basic error or warning information from
  * either the XML parser or the WDDX deserializer.</p>
  *
  * <p>If the parser or application needs to include information about a
  * specific location in an XML document, it should use the
  * WddxParseException subclass.</p>
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see org.xml.wddx.WddxParseException
  */
public class WddxDeserializationException extends com.allaire.wddx.WddxException
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Construction/Finalization
    //
    ///////////////////////////////////////////////////////////////////////


  /**
    * Create a new WddxDeserializationException.
    *
    * @param message The error message.
    */
    public WddxDeserializationException (String message) 
    {
        super(message);
    }


  /**
    * Create a new WddxDeserializationException wrapping an existing exception.
    *
    * <p>The existing exception will be embedded in the new
    * one, and its message will become the default message for
    * the WddxDeserializationException.</p>
    *
    * @param e The exception to be wrapped in a WddxDeserializationException.
    */
    public WddxDeserializationException (Exception e)
    {
        super(e);
    }


  /**
    * Create a new WddxDeserializationException from an existing exception
    * and an existing message.
    *
    * <p>The existing exception will be embedded in the new
    * one, but the new exception will have its own message.</p>
    *
    * @param message The error message.
    * @param e The exception to be wrapped in a WddxDeserializationException.
    */
    public WddxDeserializationException (String message, Exception e)
    {
        super(message, e);
    }
}