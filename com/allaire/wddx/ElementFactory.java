// Source file: com/allaire/wddx/ElementFactory.java

package com.allaire.wddx;


import java.util.Hashtable;


/**
 * Constructs WDDX element handlers for given WDDX version.
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.ElementFactoryMgr
 * @see com.allaire.wddx.WddxElement
 */
public class ElementFactory 
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private Hashtable m_elementClasses = new Hashtable();
        
    
    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Create a WddxElement object for a given element type
     *
     * <p>Looks up the Class object for the element type and instantiates
     * a new WddxElement object via the newInstance() method.</p>
     *
     * @param name The element type name.
     * @exception com.allaire.wddx.WddxDeserializationException A WDDX exception, possibly
     *            wrapping a NullPointerException, ClassCastException,
     *            IllegalAccessException, or an Instantiation exception.
     *            In all cases, an exception generated from within
     *            makeWddxElement() is a sign of an internal error.
     * @see com.allaire.wddx.ElementFactory#registerWddxElement
     * @see com.allaire.wddx.WddxElement
     */
    public WddxElement makeWddxElement(String name) throws WddxDeserializationException 
    {
        try
        {
            Class wddxElementClass = (Class)m_elementClasses.get(name);
            return (WddxElement)(wddxElementClass.newInstance());
        }
        catch(NullPointerException e)
        {
            throw new WddxDeserializationException(
                "Null pointer exception for element " + name,
                e);
        }
        catch(ClassCastException e)
        {
            throw new WddxDeserializationException(
                "Invalid cast exception for element " + name,
                e);
        }
        catch(IllegalAccessException e)
        {
            throw new WddxDeserializationException(
                "No permission to load class for element " + name,
                e);
        }
        catch(InstantiationException e)
        {
            throw new WddxDeserializationException(
                "Cannot instantiate class for element " + name,
                e);
        }
    }
    
    
    /**
     * Register a mapping between an element type name and a WddxElement 
     * class name.
     *
     * <p>Gets a Class object for className and adds it to the hash table
     * under the name key.</p>
     *
     * @param name The element type name.
     * @param className The name of the WddxElement class to handle elements
     *        of this type.
     * @exception com.allaire.wddx.WddxDeserializationException A WDDX exception, possibly
     *            wrapping a NullPointerException, or a ClassNotFound exception.
     *            In all cases, an exception generated from within
     *            registerWddxElement() is a sign of an internal error.
     * @see com.allaire.wddx.ElementFactory#makeWddxElement
     * @see com.allaire.wddx.WddxElement
     */
    public void registerWddxElement(String name, String className) throws WddxDeserializationException
    {
        try
        {
            Class wddxElementClass = Class.forName(className);
            m_elementClasses.put(name, wddxElementClass);
        }
        catch(NullPointerException e)
        {
            throw new WddxDeserializationException(
                "Null pointer exception while registering WDDX elements",
                e);
        }
        catch(ClassNotFoundException e)
        {
            throw new WddxDeserializationException(
                "Cannot find class " + className + " for WDDX element " + name,
                e);
        }
    }
}
