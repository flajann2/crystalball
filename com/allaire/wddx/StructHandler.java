package com.allaire.wddx;


import com.allaire.util.*;
import org.xml.sax.AttributeList;
import java.util.Hashtable;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;


/**
  * Handler for WDDX struct element.
  * This handler supports strictly defined types as defined
  * by the type attribute. If this attribute exists, then this handler
  * will create a bean of the defined type and initialize
  * its properties based on the vars of the WddxElement. If this struct
  * is not strictly typed or the creation of the strictly typed instance
  * fails, then this handler will produce a Hashtable that stores the
  * struct data.
  *
  * @author Simeon Simeonov (simeons@allaire.com)
  * @version 1.0
  * @see org.xml.wddx.WddxElement
  */
class StructHandler extends com.allaire.wddx.WddxElement {
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    private static final String NAME_ATTRIBUTE_NAME = "name";
    private static final String TYPE_ATTRIBUTE_NAME = "type";
    private static final String VAR_ELEMENT_NAME = "var";

    private Hashtable m_ht = new Hashtable();
    private String m_currentVarName = null;
    private String m_strictType = null;


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

        // Check for presence of the optional type attribute
        //If present, this attribute will be used to load the struct
        //data into a typed Java object rather than the generic Hashtable.
        m_strictType = attributes.getValue(TYPE_ATTRIBUTE_NAME);
        if(m_strictType != null){
            //store the type attribute in a reserved wddx var
            m_ht.put("_wddx_structAttributes_type", m_strictType);
        }
    }

    /**
     * Receive notification of the end of a struct element.
     * If this is a strictly typed struct, then this method
     * will attempt to convert the struct into an instance of
     * its strict type and set its properties based on the Wddx
     * packet data.
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
        //attempt to determine the strictly defined type of this struct.
        if (m_strictType == null) {
            //this struct is not strictly typed, so represent it as a Hashtable.
            setValue(m_ht);
        }
        else {
            try {
                //this struct is strictly typed, so attempt to create an instance
                //of the defined type, and initialize its properties using
                //the data from the WDDX packet.
                Class beanClass = getClassBySignature(m_strictType);
                Object bean = beanClass.newInstance();
                setBeanProperties(bean, m_ht);
                setValue(bean);
            }
            catch (InstantiationException e) {
                //instantiation failed, so default back to the hashtable value
                setValue(m_ht);
            }
            catch (IllegalAccessException e) {
                //instantiation failed, so default back to the hashtable value
                setValue(m_ht);
            }
            catch (ClassNotFoundException e) {
                //instantiation failed, so default back to the hashtable value
                setValue(m_ht);
            }
            catch(WddxDeserializationException e){
                //instantiation failed, so default back to the hashtable value
                setValue(m_ht);
            }
        }
    }

    /**
     * Receive notification of the start of a child element.
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
        // Verify that the child name is var
        if (!name.equals(VAR_ELEMENT_NAME)) {
            throwException(
                          "Only " + VAR_ELEMENT_NAME +
                          " elements can be nested inside a struct element");
        }

        // Check for presence of name attribute
        m_currentVarName = attributes.getValue(NAME_ATTRIBUTE_NAME);
        if (m_currentVarName == null) {
            throwException(
                          NAME_ATTRIBUTE_NAME + " attribute not provided for " +
                          VAR_ELEMENT_NAME + " element");
        }
    }

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
        // Store variable
        m_ht.put(m_currentVarName, childElement.getValue());
    }

    /**
     * Initialize a bean's properties based on the values in a hashtable.
     * This method is used to initialize an Object's properties using the
     * data from the WDDX struct.
     * Hashtable format-
     *   key--->  String: property name
     *   value->  Object: property value
     *
     * @param bean the bean to initialize
     * @param props table of property names and values.
     * @exception com.allaire.wddx.WddxDeserializationException Any WDDX exception, possibly
     *            wrapping another exception.
     * @author Spike Washburn (spike@allire.com)
     */
    private void setBeanProperties(Object bean, Hashtable props) throws WddxDeserializationException{

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
            PropertyDescriptor[] descriptorArray = beanInfo.getPropertyDescriptors();
            Hashtable descriptors = new Hashtable();
            for (int i=0; i<descriptorArray.length; i++) {
                descriptors.put(descriptorArray[i].getName(), descriptorArray[i]);
            }


            Enumeration propNames = props.keys();
            while (propNames.hasMoreElements()) {
                String propName = (String)propNames.nextElement();
                Object propValue = props.get(propName);

                PropertyDescriptor pd = (PropertyDescriptor)descriptors.get(propName);
                if (pd == null) {
                    //there is no property matching this variable, so just ignore it.
                    continue;
                }
                if (pd instanceof IndexedPropertyDescriptor) {
                    //set the indexed property using the vector constructed by the child element

                    Vector values = (Vector)propValue;
                    IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)pd;
                    Method method = ipd.getIndexedWriteMethod();
                    if (method == null) {
                        //there is no setter for this property
                    }
                    else {
                        //loop through each value in the vector and set it at the appropriate index
                        try {
                            int size = values.size();
                            for (int index=0; index<size; index++) {
                                Object value = values.elementAt(index);

                                //convert the WDDX object value to the type required by method argument
                                Class[] types = method.getParameterTypes();
                                value = ObjectConverter.convert(value, types[1]);

                                //method signature will be: void set<PropName>(int index, <PropType> value)
                                method.invoke(bean, new Object[]{new Integer(index), value});
                            }
                        }
                        catch (IllegalAccessException e) {
                            //setter method is not publicly accessible
                        }
                        catch (InvocationTargetException e) {
                            //exception was thrown by the setter method.
                            throwException(method + " threw an exception", e);
                        }
                        catch (ObjectConversionException e) {
                            //the serialized value is incompatible with the bean's property type.
                            throwException("Incompatible value type for property: " + pd.getName());
                        }
                    }
                }
                else {
                    Method method = pd.getWriteMethod();
                    if (method == null) {
                        //there is no setter for this property
                    }
                    else {
                        try {
                            //set the property using the value constructed by the child element
                            Object value = propValue;

                            //convert the WDDX object value to the type required by method argument
                            Class[] types = method.getParameterTypes();
                            value = ObjectConverter.convert(value, types[0]);

                            //method signature will be: void set<PropName>(<PropType> value)
                            method.invoke(bean, new Object[]{value});
                        }
                        catch (IllegalAccessException e) {
                            //setter method is not publicly accessible
                        }
                        catch (InvocationTargetException e) {
                            //exception was thrown by the setter method.
                            throwException(method + " threw an exception", e);
                        }
                        catch (ObjectConversionException e) {
                            //the serialized value is incompatible with the bean's property type.
                            throwException("Incompatible value type for property: " + pd.getName());
                        }
                    }
                }
            }
        }
        catch (java.beans.IntrospectionException e) {
            throwException("Bean introspection failed", e);
        }
    }

    /**
     * Returns the class defined by the JNI Type signature.
     * See the Java Language specification for details about
     * JNI type signatures.
     */
    private static Class getClassBySignature(String jniTypeSig) throws ClassNotFoundException{
        int index = 0;
        char c = jniTypeSig.charAt(index);
        switch (c) {
        case 'Z':  return Boolean.TYPE;
        case 'B':  return Byte.TYPE;
        case 'C':  return Character.TYPE;
        case 'S':  return Short.TYPE;
        case 'I':  return Integer.TYPE;
        case 'J':  return Long.TYPE;
        case 'F':  return Float.TYPE;
        case 'D':  return Double.TYPE;
        default:
        case 'L':
            String className = jniTypeSig.substring(index + 1, jniTypeSig.length()-1);
            return Class.forName(className);
        case '[':
            className = jniTypeSig.substring(index, jniTypeSig.length());
            return Class.forName(className);
        }
    }
}