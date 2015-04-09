// Source file: com/allaire/wddx/DeserializerWorker.java

package com.allaire.wddx;


import org.xml.sax.*;
import java.util.Vector;
import java.util.NoSuchElementException;
import java.io.IOException;


/**
 * DeserializerWorker performs WDDX deserialization.
 *
 * <p>The class is a SAX document handler that monitors SAX XML parsing
 * events and instantiates the appropriate WDDX handler objects. A
 * stack of active handlers is maintained internally.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see org.xml.sax.HandlerBase
 * @see com.allaire.wddx.DeserializationContext
 * @see com.allaire.wddx.WddxElement
 * @see com.allaire.wddx.WddxDeserializer
 */
public class DeserializerWorker extends HandlerBase implements DeserializationContext 
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private static final String ROOT_ELEMENT_NAME = "wddxPacket";    
    private static final String VERSION_ATTRIBUTE_NAME = "version";    

    private static ElementFactoryMgr m_elementFactoryMgr = null;

    private ElementFactory m_elementFactory = null;
    private Vector m_stackElements = new Vector();
    private Parser m_parser = null;
    private Locator m_locator = null;
    private Object m_result = null;
    
    
    ///////////////////////////////////////////////////////////////////////
    //
    // Construction/Finalization
    //
    ///////////////////////////////////////////////////////////////////////


    private DeserializerWorker() 
    {
        // do nothing
    }
    
    /**
     * Constructs a DeserializerWorker with the given SAX parser class.
     *
     * @param parserClass 
     *      Name of SAX parser class to use. This class must support
     *      the org.xml.sax.Parser interface.
     * @exception com.allaire.wddx.WddxDeserializationException 
     *      Any WDDX deserialization exception, possibly wrapping another exception.
     * @see org.xml.sax.Parser
     */
    DeserializerWorker(String parserClass) throws WddxDeserializationException
    {
        // Make sure the element factory manager is instantiated
        createElementFactoryMgr();
        
        // Create a SAX parser
        try
        {
            Class saxParserClass = Class.forName(parserClass);
            m_parser = (Parser)(saxParserClass.newInstance());
        }
        catch(ClassNotFoundException e)
        {
            throwException(
                "Cannot find SAX parser class " + parserClass, 
                e);
        }
        catch(ClassCastException e)
        {
            throwException(
                "Parser class " + parserClass + " does not implement org.xml.sax.Parser interface",
                e);
        }
        catch(IllegalAccessException e)
        {
            throwException(
                "No permission to load SAX parser class " + parserClass,
                e);
        }
        catch(InstantiationException e)
        {
            throw new WddxDeserializationException(
                "Cannot instantiate SAX parser class " + parserClass,
                e);
        }
        
        // Register yourself with parser as event handler
        m_parser.setDocumentHandler(this);
        m_parser.setErrorHandler(this);        
    }
    
    
    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


  /**
    * Receive a Locator object for document events.
    *
    * @param locator A locator for all SAX document events.
    * @see org.xml.sax.DocumentHandler#setDocumentLocator
    * @see org.xml.sax.Locator
    */
    public void setDocumentLocator(Locator locator) 
    {
        m_locator = locator;
    }
    
    
  /**
    * Receive notification of the start of an element.
    *
    * @param name The element type name.
    * @param attributes The specified or defaulted attributes.
    * @exception org.xml.sax.SAXException Any SAX exception, possibly
    *            wrapping another exception.
    * @see org.xml.sax.DocumentHandler#startElement
    */
    public void startElement(String name, AttributeList atts) throws SAXException 
    {
        try
        {
            // If this is the first element, perform WDDX version initialization
            if (m_stackElements.isEmpty())
            {
                // Check the validity of the root element
                if (! name.equals(ROOT_ELEMENT_NAME) ||
                    atts.getLength() != 1 ||
                    ! atts.getName(0).equals(VERSION_ATTRIBUTE_NAME))
                {
                    throwSAXException("Invalid WDDX packet: root element in not wddxPacket");
                }
                
                // Obtain an element factory for this version
                m_elementFactory = m_elementFactoryMgr.getElementFactory(atts.getValue(0));
            }
            
            // If necessary, notify parent element of child
            if (! m_stackElements.isEmpty())
            {
                getTopElement().onBeforeChild(name, atts);
            }

            // Create a WDDX element object for current element, hook up context
            WddxElement currentElement = m_elementFactory.makeWddxElement(name);
            currentElement.setDeserializationContext(this);
            
            // Sink start element event to current element
            currentElement.onStartElement(name, atts);

            // Add current element to element stack
            pushElement(currentElement);
        }
        catch(WddxDeserializationException e)
        {
            throwSAXException(e);
        }
    }
    
    
  /**
    * Receive notification of the end of an element.
    *
    * @param name The element type name.
    * @param attributes The specified or defaulted attributes.
    * @exception org.xml.sax.SAXException Any SAX exception, possibly
    *            wrapping another exception.
    * @see org.xml.sax.DocumentHandler#endElement
    */
    public void endElement(String name) throws SAXException 
    {
        try
        {
            // Pop top element from stack
            WddxElement currentElement = popElement();
            
            // Sink end element event to element
            currentElement.onEndElement();
            
            if (! m_stackElements.isEmpty())
            {
                // There is a parent element, notify of child
                getTopElement().onAfterChild(currentElement);
            }
            else
            {
                // This is the root element
                // Extract and store deserialization result
                m_result = currentElement.getValue();
            }
        }
        catch(WddxDeserializationException e)
        {
            throwSAXException(e);
        }
    }
    
    
  /**
    * Receive notification of character data inside an element.
    *
    * @param ch The characters.
    * @param start The start position in the character array.
    * @param length The number of characters to use from the
    *               character array.
    * @exception org.xml.sax.SAXException Any SAX exception, possibly
    *            wrapping another exception.
    * @see org.xml.sax.DocumentHandler#characters
    */
    public void characters(char ch[], int start, int length) throws SAXException 
    {
        try
        {
            // Sink event to current element handler
            getTopElement().onCharacters(ch, start, length);
        }
        catch(WddxDeserializationException e)
        {
            throwSAXException(e);
        }
    }
    
    
  /**
    * Receive notification of a recoverable parser error.
    *
    * @param e The error information encoded as an exception.
    * @exception org.xml.sax.SAXException Any SAX exception, possibly
    *            wrapping another exception.
    * @see org.xml.sax.ErrorHandler#error
    * @see org.xml.sax.SAXParseException
    */
    public void error(SAXParseException exception) throws SAXException 
    {
        throwSAXException(exception);
    }
    
    
  /**
    * Report a fatal XML parsing error.
    *
    * @param e The error information encoded as an exception.
    * @exception org.xml.sax.SAXException Any SAX exception, possibly
    *            wrapping another exception.
    * @see org.xml.sax.ErrorHandler#fatalError
    * @see org.xml.sax.SAXParseException
    */
    public void fatalError(SAXParseException exception) throws SAXException 
    {
        throwSAXException(exception);
    }

    
    /**
     * Deserializes the contents of a WDDX packet
     *  
     * @return Top-level object in WDDX packet
     * @param source The WDDX packet to process
     * @exception com.allaire.wddx.WddxDeserializationException
     *      Any WDDX deserialization exception, possibly wrapping another exception
     * @exception java.lang.IOException
     *      Any I/O exception that may have been generated by the source parameter
     * @see org.xml.sax.InputSource
     * @see com.allaire.wddx.WddxDeserializer
     */
    public synchronized Object deserialize(InputSource source) throws WddxDeserializationException, IOException
    {
        Object result = null;
        try
        {
            try
            {
                m_parser.parse(source);
            }
            catch(SAXException e)
            {
                throwException(e);
            }
            
            result = m_result;
        }
        finally
        {
            // Release objects
            m_stackElements.setSize(0);
            m_result = null;
            m_locator = null;
            m_elementFactory = null;            
        }
        
        return result;
    }
    
    
    /**
     * Returns the WddxElement object that is the parent of the
     * WddxElement object processing an onStartElement handler.
     *
     * <p>Note that this method will return the expected information
     * if and only if it is called from the onStartElement or 
     * onEndElement handlers.</p>
     *
     * @exception com.allaire.wddx.WddxDeserializationException A WDDX exception indicating
     *            that this is the first element on the element stack.
     * @see com.allaire.wddx.WddxElement
     */
    public WddxElement getParentElement() throws WddxDeserializationException
    {
        return getTopElement();
    }
    
    
    ///////////////////////////////////////////////////////////////////////
    //
    // Helper functions
    //
    ///////////////////////////////////////////////////////////////////////


    /**
       @roseuid 361A877D00EA
     */
    private WddxElement getTopElement() throws WddxDeserializationException
    {
        WddxElement topElement = null;
        
        try
        {
            topElement = (WddxElement)m_stackElements.lastElement();
        }
        catch(NoSuchElementException e)
        {
            throwException("Attempting to access empty element stack", e);
        }
        catch(ClassCastException e)
        {
            throwException("Invalid element on stack", e);
        }
        
        return topElement;
    }

    
    private void pushElement(WddxElement element)
    {
        m_stackElements.addElement(element);
    }
    
    
    private WddxElement popElement() throws WddxDeserializationException
    {
        WddxElement topElement = getTopElement();        
        m_stackElements.setSize(m_stackElements.size() - 1);
        return topElement;
    }
    
    
    private synchronized void createElementFactoryMgr() throws WddxDeserializationException
    {
        if (m_elementFactoryMgr == null)
        {
            m_elementFactoryMgr = new ElementFactoryMgr();
        }
    }

    
    private WddxDeserializationException createWddxDeserializationException(String message, Exception e)
    {
        WddxDeserializationException wddxSerializationException;
        
        if (m_locator != null)
        {
            wddxSerializationException = new WddxParseException(
                message,
                m_locator.getPublicId(), 
                m_locator.getSystemId(),
                m_locator.getLineNumber(), 
                m_locator.getColumnNumber(), 
                e);
        }
        else
        {
            wddxSerializationException = new WddxDeserializationException(message, e);
        }
        
        return wddxSerializationException;
    }
    
    
    private void throwSAXException(String message) throws SAXException
    {
        throwSAXException(message, null);
    }
    
    
    private void throwSAXException(String message, Exception e) throws SAXException
    {
        throwSAXException(createWddxDeserializationException(message, e));
    }
    
    
    private void throwSAXException(SAXParseException e) throws SAXException
    {
        WddxDeserializationException wddxDeserializationException = new WddxParseException(
            e.getMessage(),
            e.getPublicId(), 
            e.getSystemId(),
            e.getLineNumber(), 
            e.getColumnNumber(),
            e);
        throwSAXException(wddxDeserializationException);
    }
    
    
    private void throwSAXException(WddxDeserializationException e) throws SAXException
    {
        throw new SAXException(e);
    }
    
    
    private void throwException(String message, Exception e) throws WddxDeserializationException
    {
        throw createWddxDeserializationException(message, e);
    }

    
    private void throwException(SAXException e) throws WddxDeserializationException
    {
        Exception heldException = e.getException();
        if (heldException != null)
        {
            try
            {
                WddxDeserializationException wddxDeserializationException = (WddxDeserializationException)heldException;
                throw wddxDeserializationException;
            }
            catch(ClassCastException eTemp)
            {
                throw new WddxDeserializationException(e.getMessage(), heldException);
            }
        }
        else
        {
            throw new WddxDeserializationException(e.getMessage());
        }
    }
}
