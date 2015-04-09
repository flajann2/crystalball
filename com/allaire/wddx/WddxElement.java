// Source file: com/allaire/wddx/WddxElement.java

package com.allaire.wddx;


import org.xml.sax.AttributeList;
import java.lang.*;


/**
  * Handler for WDDX elements.
  *
  * <p>This is the base class for WDDX element handlers. It maintains
  * the deserialized value and a reference to the deserialization 
  * context. It handles start/end element, CDATA, and before/after
  * child element events.</p>
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see com.allaire.wddx.DeserializerWorker
  */
public class WddxElement 
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private Object m_value = null;
    private DeserializationContext m_context = null;
    
    
    ///////////////////////////////////////////////////////////////////////
    //
    // Construction/Finalization
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Construct a WddxElement.
     *
     * <p>This method is protected to make sure that only subclasses can
     * create WddxElement objects.</p>
     */
    protected WddxElement() 
    {
        // do nothing
    }


    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Receive notification of the start of an element.
     *
     * <p>By default, do nothing.  You may override this
     * method in a subclass to take specific actions at the start of
     * each element such as extracting information from the attributes.</p>
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
        // do nothing
    }
    
    
    /**
     * Receive notification of the end of an element.
     *
     * <p>By default, do nothing.  You may override this
     * method in a subclass to take specific actions at the start of
     * each element such as transferring your value into your parent.</p>
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
        // do nothing
    }
    
    
    /**
     * Receive notification of character data inside an element.
     *
     * <p>By default, do nothing.  You may override this
     * method to take specific actions for each chunk of character data
     * (such as adding the data to a buffer).</p>
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
        // do nothing
    }
    
    
    /**
     * Receive notification of the start of a child element.
     *
     * <p>By default, do nothing.  You may override this
     * method in a subclass to take specific actions at the start of
     * each child element (such as using its attribute information).</p>
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
        // do nothing
    }
    
    
    /**
     * Receive notification past the end tag processing of a child element.
     *
     * <p>By default, do nothing.  You may override this
     * method in a subclass to take specific actions (such as transferring
     * the value from the child element).</p>
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
        // do nothing
    }

    
    /**
     * Get the value stored in this element.
     *
     * @see com.allaire.wddx.WddxElement#setValue
     */
    public Object getValue() 
    {
        return m_value;
    }
    
    
    /**
     * Set the value stored in this element.
     *
     * @param newValue Value to set in the element.
     * @see com.allaire.wddx.WddxElement#getValue
     */
    public void setValue(Object newValue) 
    {
        m_value = newValue;
    }

    
    /**
     * Get the deserialization context of this element.
     *
     * @see com.allaire.wddx.DeserializationContext
     */
    public DeserializationContext getDeserializationContext() 
    {
        return m_context;
    }

    
    /**
     * Set the deserialization context of this element.
     *
     * @see com.allaire.wddx.DeserializationContext
     */
    public void setDeserializationContext(DeserializationContext context) 
    {
        m_context = context;
    }


    ///////////////////////////////////////////////////////////////////////
    //
    // Helper functions
    //
    ///////////////////////////////////////////////////////////////////////


    protected void throwException(String message) throws WddxDeserializationException
    {
        throw new WddxDeserializationException(message, null);
    }


    protected void throwException(String message, Exception e) throws WddxDeserializationException
    {
        throw new WddxDeserializationException(message, e);
    }
}
