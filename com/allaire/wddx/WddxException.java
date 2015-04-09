// $Header: /var/lune/cvs/crystalball/com/allaire/wddx/WddxException.java,v 1.1 2003/10/18 15:26:20 fred Exp $

package com.allaire.wddx;

/**
  * Encapsulate a general WDDX error or warning.
  *
  * <p>This class can contain basic error or warning information from
  * either the XML parser or the WDDX serializer/deserializer.</p>
  *
  * <p>If the parser or application needs to include information about a
  * specific location in an XML document, it should use the
  * WddxParseException subclass.</p>
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see org.xml.wddx.WddxDeserializationException
  */
public class WddxException extends Exception 
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private String m_message;
    private Exception m_exception;

    
    ///////////////////////////////////////////////////////////////////////
    //
    // Construction/Finalization
    //
    ///////////////////////////////////////////////////////////////////////


  /**
    * Create a new WddxException.
    *
    * @param message The error message.
    */
    public WddxException (String message) 
    {
        super();
        m_message = message;
        m_exception = null;
    }


  /**
    * Create a new WddxException wrapping an existing exception.
    *
    * <p>The existing exception will be embedded in the new
    * one, and its message will become the default message for
    * the WddxException.</p>
    *
    * @param e The exception to be wrapped in a WddxException.
    */
    public WddxException (Exception e)
    {
        super();
        m_message = null;
        m_exception = e;
    }


  /**
    * Create a new WddxException from an existing exception
    * and an existing message.
    *
    * <p>The existing exception will be embedded in the new
    * one, but the new exception will have its own message.</p>
    *
    * @param message The error message.
    * @param e The exception to be wrapped in a WddxException.
    */
    public WddxException (String message, Exception e)
    {
        super();
        m_message = message;
        m_exception = e;
    }


    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////

  /**
    * Return a detail message for this exception.
    *
    * <p>If there is an embedded exception, and if the WddxException
    * has no detail message of its own, this method will return
    * the detail message from the embedded exception.</p>
    *
    * @return The error message.
    */
    public String getMessage ()
    {
        if (m_message == null && m_exception != null) 
        {
            return m_exception.getMessage();
        } 
        else 
        {
            return m_message;
        }
    }


  /**
    * Return the embedded exception, if any.
    *
    * @return The embedded exception, or null if there is none.
    */
    public Exception getException ()
    {
        return m_exception;
    }


  /**
    * Convert the exception to a string.
    *
    * @return A string version of this exception.
    */
    public String toString ()
    {
        return getMessage();
    }
}
