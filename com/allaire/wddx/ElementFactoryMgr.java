// Source file: com/allaire/wddx/ElementFactoryMgr.java

package com.allaire.wddx;


import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;


/**
 * Manages WDDX element handler factories for different WDDX versions.
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.ElementFactory
 * @see com.allaire.wddx.WddxElement
 */
public class ElementFactoryMgr 
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private Hashtable m_elementFactories = new Hashtable();
    private ElementFactory m_defaultFactory;
        
    
    ///////////////////////////////////////////////////////////////////////
    //
    // Construction/Finalization
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Create an element factory manager.
     *
     * <p>Creates one ElementFactory object for every version of WDDX that
     * is supported and registers WDDXElement classes with it.</p>
     *
     * @exception com.allaire.wddx.WddxDeserializationException A WDDX exception, possibly
     *            wrapping a NullPointerException.
     * @see com.allaire.wddx.ElementFactory
     * @see com.allaire.wddx.ElementFactory#registerWddxElement
     */
    ElementFactoryMgr() throws WddxDeserializationException{
        this(WddxDefaults.getProperties());
    }

    /**
     * Create an element factory manager based on the specified properties configuration.
     * Expected properties format-
     * Key: deserializer.[element name]
     * Element: element handler classname
     *
     *
     * <p>Creates one ElementFactory object for every version of WDDX that
     * is supported and registers WDDXElement classes with it.</p>
     *
     * @param config the factory element handler configuration
     * @exception com.allaire.wddx.WddxDeserializationException A WDDX exception, possibly
     *            wrapping a NullPointerException.
     * @see com.allaire.wddx.ElementFactory
     * @see com.allaire.wddx.ElementFactory#registerWddxElement
     */
    ElementFactoryMgr(Properties config) throws WddxDeserializationException
    {
        try
        {
            ElementFactory ef = new ElementFactory();

            Enumeration propNames = config.propertyNames();
            while(propNames.hasMoreElements()){
                String pName = (String)propNames.nextElement();
                if(pName.startsWith("deserializer.")){
                   String elementName = pName.substring("deserializer.".length());
                   String handlerClassname = config.getProperty(pName);
                   ef.registerWddxElement(elementName, handlerClassname);
                }
            }
            
            m_elementFactories.put(new Float("0.9"), ef);
            m_elementFactories.put(new Float("1.0"), ef);
            m_defaultFactory = ef;
        }
        catch(NullPointerException e)
        {
            throw new WddxDeserializationException(
                "Null pointer exception upon instantiating and loading WDDX element factories",
                e);
        }
    }


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
     * @see com.allaire.wddx.ElementFactory
     */
    public ElementFactory getElementFactory(String version) throws WddxDeserializationException
    {
        try
        {
            ElementFactory ef = (ElementFactory)m_elementFactories.get(new Float(version));
            if(ef == null){
                //then this is a WDDX version that the deserializer doesn't explicitly support,
                //so attempt to use the default factory.
                //WARNING: defaulting like this opens the deserializer to possibly bombing out
                //while deserializing elements that aren't supported by the factory.
                ef = m_defaultFactory;
            }
            return ef;
        }
        catch(NullPointerException e)
        {
            throw new WddxDeserializationException(
                "Null pointer exception WDDX version",
                e);
        }
        catch(ClassCastException e)
        {
            throw new WddxDeserializationException(
                "Invalid cast exception for WDDX version " + version,
                e);
        }
    }
}
