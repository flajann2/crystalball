// $Header :$

package com.allaire.wddx;

/**
  * Encapsulate a WDDX parse error or warning.
  *
  * <p>This exception will include information for locating the error
  * in the original XML document.</p>
  *
  * <p>Since this exception is a subclass of WddxDeserializationException, it
  * inherits the ability to wrap another exception.</p>
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see org.xml.wddx.WddxDeserializationException
  */
public class WddxParseException extends com.allaire.wddx.WddxDeserializationException 
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private String m_publicId;
    private String m_systemId;
    private int m_lineNumber;
    private int m_columnNumber;

    
    ///////////////////////////////////////////////////////////////////////
    //
    // Construction/Finalization
    //
    ///////////////////////////////////////////////////////////////////////


  /**
    * Create a new WddxParseException.
    *
    * @param message The error or warning message.
    * @param publicId The public identifer of the entity that generated
    *                 the error or warning.
    * @param systemId The system identifer of the entity that generated
    *                 the error or warning.
    * @param lineNumber The line number of the end of the text that
    *                   caused the error or warning.
    * @param columnNumber The column number of the end of the text that
    *                     cause the error or warning.
    */
    public WddxParseException (
        String message, 
        String publicId, 
        String systemId,
        int lineNumber, 
        int columnNumber)
    {
        super(message);
        m_publicId = publicId;
        m_systemId = systemId;
        m_lineNumber = lineNumber;
        m_columnNumber = columnNumber;
    }


  /**
    * Create a new WddxParseException with an embedded exception.
    *
    * @param message The error or warning message, or null to use
    *                the message from the embedded exception.
    * @param publicId The public identifer of the entity that generated
    *                 the error or warning.
    * @param systemId The system identifer of the entity that generated
    *                 the error or warning.
    * @param lineNumber The line number of the end of the text that
    *                   caused the error or warning.
    * @param columnNumber The column number of the end of the text that
    *                     cause the error or warning.
    * @param e Another exception to embed in this one.
    */
    public WddxParseException (
        String message, 
        String publicId, 
        String systemId,
        int lineNumber, 
        int columnNumber, 
        Exception e)
    {
        super(message, e);
        m_publicId = publicId;
        m_systemId = systemId;
        m_lineNumber = lineNumber;
        m_columnNumber = columnNumber;
    }


    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


  /**
    * Get the public identifier of the entity where the exception occurred.
    *
    * @return A string containing the public identifier, or null
    *         if none is available.
    */
    public String getPublicId ()
    {
        return m_publicId;
    }


  /**
    * Get the system identifier of the entity where the exception occurred.
    *
    * <p>If the system identifier is a URL, it will be resolved
    * fully.</p>
    *
    * @return A string containing the system identifier, or null
    *         if none is available.
    */
    public String getSystemId ()
    {
        return m_systemId;
    }


  /**
    * The line number of the end of the text where the exception occurred.
    *
    * @return An integer representing the line number, or -1
    *         if none is available.
    */
    public int getLineNumber ()
    {
        return m_lineNumber;
    }


  /**
    * The column number of the end of the text where the exception occurred.
    *
    * <p>The first column in a line is position 1.</p>
    *
    * @return An integer representing the column number, or -1
    *         if none is available.
    */
    public int getColumnNumber ()
    {
        return m_columnNumber;
    }
}
