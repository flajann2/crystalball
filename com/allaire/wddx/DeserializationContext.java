// Source file: com/allaire/wddx/DeserializationContext.java


package com.allaire.wddx;

/**
 * Provides context information to element handlers during
 * the deserialization process.
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.DeserializerWorker
 * @see com.allaire.wddx.WddxElement
 */
public interface DeserializationContext 
{    
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
    public WddxElement getParentElement() throws WddxDeserializationException;
}
