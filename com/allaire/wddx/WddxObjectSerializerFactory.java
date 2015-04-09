package com.allaire.wddx;

import java.util.Dictionary;
import java.util.Properties;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Date;

import java.io.IOException;
import java.io.InputStream;
import com.allaire.util.RecordSet;
import com.allaire.util.SimpleRecordSet;


//--------------- change history ----------------------------------//
// 09/16/99 spike: add CharacterSerializer
// 09/16/99 spike: support mapping serializers to superclasses and interfaces
// 10/04/99 spike: Pull serializer bindings into Java-version specific property file
//-----------------------------------------------------------------//
/**
 * Serializes a boolean
 *
 * <p>An instance of this class registered with the WDDX 
 * object serializer factory can be used to serialize 
 * instances of java.lang.Boolean.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 * @see com.allaire.wddx.WddxObjectSerializer
 * @see java.lang.Boolean
 */
class BooleanSerializer implements WddxObjectSerializer
{
    /**
     * Write a boolean to the output stream.
     *
     * @param out The WDDX output stream to use.
     * @param obj The object to serialize. Must be an instance of java.lang.Boolean.
     *
     * @exception java.io.IOException The exception can be generated during 
     *      serialization or if the obj is not an instance of java.lang.Boolean
     * 
     * @see java.lang.Boolean
     */
    public void writeObject(WddxOutputStream out, Object obj) throws IOException
    {
        try
        {
            out.writeBoolean(((Boolean)obj).booleanValue());
        }
        catch(ClassCastException e)
        {
            throw new IOException(
                "Invalid cast exception for boolean");
        }
    }
}


/**
 * Serializes a number
 *
 * <p>An instance of this class registered with the WDDX 
 * object serializer factory can be used to serialize 
 * instances of java.lang.Number.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 * @see com.allaire.wddx.WddxObjectSerializer
 * @see java.lang.Number
 */
class NumberSerializer implements WddxObjectSerializer
{
    /**
     * Write a number to the output stream.
     *
     * @param out The WDDX output stream to use.
     * @param obj The object to serialize. Must be an instance of java.lang.Number.
     *
     * @exception java.io.IOException The exception can be generated during 
     *      serialization or if the obj is not an instance of java.lang.Number
     * 
     * @see java.lang.Number
     */
    public void writeObject(WddxOutputStream out, Object obj) throws IOException
    {
        try
        {
            out.writeDouble(((Number)obj).doubleValue());
        }
        catch(ClassCastException e)
        {
            throw new IOException(
                "Invalid cast exception for number");
        }
    }
}

/**
 * Serializes a character
 *
 * <p>An instance of this class registered with the WDDX 
 * object serializer factory can be used to serialize 
 * instances of java.lang.Character.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 * @see com.allaire.wddx.WddxObjectSerializer
 * @see java.lang.String
 */
class CharacterSerializer implements WddxObjectSerializer {
    /**
     * Write a character to the output stream.
     *
     * @param out The WDDX output stream to use.
     * @param obj The object to serialize. Must be an instance of java.lang.String.
     *
     * @exception java.io.IOException The exception can be generated during 
     *      serialization or if the obj is not an instance of java.lang.String
     * 
     * @see java.lang.String
     */
    public void writeObject(WddxOutputStream out, Object obj) throws IOException
    {
        try {
            out.writeChars(((Character)obj).toString());
        }
        catch (ClassCastException e) {
            throw new IOException(
                                 "Invalid cast exception for character");
        }
    }
}

/**
 * Serializes a string
 *
 * <p>An instance of this class registered with the WDDX 
 * object serializer factory can be used to serialize 
 * instances of java.lang.String.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 * @see com.allaire.wddx.WddxObjectSerializer
 * @see java.lang.String
 */
class StringSerializer implements WddxObjectSerializer
{
    /**
     * Write a string to the output stream.
     *
     * @param out The WDDX output stream to use.
     * @param obj The object to serialize. Must be an instance of java.lang.String.
     *
     * @exception java.io.IOException The exception can be generated during 
     *      serialization or if the obj is not an instance of java.lang.String
     * 
     * @see java.lang.String
     */
    public void writeObject(WddxOutputStream out, Object obj) throws IOException
    {
        try
        {
            out.writeChars((String)obj);
        }
        catch(ClassCastException e)
        {
            throw new IOException(
                "Invalid cast exception for string");
        }
    }
}


/**
 * Serializes a date
 *
 * <p>An instance of this class registered with the WDDX 
 * object serializer factory can be used to serialize 
 * instances of java.util.Date.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 * @see com.allaire.wddx.WddxObjectSerializer
 * @see java.util.Date
 */
class DateSerializer implements WddxObjectSerializer
{
    /**
     * Write a date to the output stream.
     *
     * @param out The WDDX output stream to use.
     * @param obj The object to serialize. Must be an instance of java.util.Date.
     *
     * @exception java.io.IOException The exception can be generated during 
     *      serialization or if the obj is not an instance of java.util.Date
     * 
     * @see java.util.Date
     */
    public void writeObject(WddxOutputStream out, Object obj) throws IOException
    {
        try
        {
            out.writeDate((Date)obj);
        }
        catch(ClassCastException e)
        {
            throw new IOException(
                "Invalid cast exception for date");
        }
    }
}


/**
 * Serializes a vector
 *
 * <p>An instance of this class registered with the WDDX 
 * object serializer factory can be used to serialize 
 * instances of java.util.Vector.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 * @see com.allaire.wddx.WddxObjectSerializer
 * @see java.util.Vector
 */
class VectorSerializer implements WddxObjectSerializer
{
    /**
     * Write a vector to the output stream.
     *
     * @param out The WDDX output stream to use.
     * @param obj The object to serialize. Must be an instance of java.util.Vector.
     *
     * @exception java.io.IOException The exception can be generated during 
     *      serialization or if the obj is not an instance of java.util.Vector
     * 
     * @see java.util.Vector
     */
    public void writeObject(WddxOutputStream out, Object obj) throws IOException
    {
        try
        {
            out.writeVector((Vector)obj);
        }
        catch(ClassCastException e)
        {
            throw new IOException(
                "Invalid cast exception for vector");
        }
    }
}


/**
 * Serializes a dictionary
 *
 * <p>An instance of this class registered with the WDDX 
 * object serializer factory can be used to serialize 
 * instances of java.util.Dictionary.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 * @see com.allaire.wddx.WddxObjectSerializer
 * @see java.util.Dictionary
 */
class DictionarySerializer implements WddxObjectSerializer
{
    /**
     * Write a dictionary to the output stream.
     *
     * @param out The WDDX output stream to use.
     * @param obj The object to serialize. Must be an instance of java.util.Dictionary.
     *
     * @exception java.io.IOException The exception can be generated during 
     *      serialization or if the obj is not an instance of java.util.Dictionary
     * 
     * @see java.util.Dictionary
     */
    public void writeObject(WddxOutputStream out, Object obj) throws IOException
    {
        try
        {
            out.writeDictionary((Dictionary)obj);
        }
        catch(ClassCastException e)
        {
            throw new IOException(
                "Invalid cast exception for dictionary");
        }
    }
}


/**
 * Serializes a date
 *
 * <p>An instance of this class registered with the WDDX 
 * object serializer factory can be used to serialize 
 * instances of com.allaire.util.RecordSet.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 * @see com.allaire.wddx.WddxObjectSerializer
 * @see com.allaire.util.RecordSet
 */
class RecordSetSerializer implements WddxObjectSerializer
{
    /**
     * Write a recordset to the output stream.
     *
     * @param out The WDDX output stream to use.
     * @param obj The object to serialize. Must be an instance of com.allaire.util.RecordSet.
     *
     * @exception java.io.IOException The exception can be generated during 
     *      serialization or if the obj is not an instance of com.allaire.util.RecordSet
     * 
     * @see com.allaire.util.RecordSet
     */
    public void writeObject(WddxOutputStream out, Object obj) throws IOException
    {
        try
        {
            out.writeRecordSet((RecordSet)obj);
        }
        catch(ClassCastException e)
        {
            throw new IOException(
                                 "Invalid cast exception for WDDX recordset");
        }
    }
}


/**
 * Object serializer factory. This class keeps a registry of objects 
 * implementing the WddxObjectSerializer interface. 
 *
 * <p>When an unknown object
 * needs to be serialized, the factory uses the introspection APIs
 * to get the object's class name. Then it checks whether a serialization
 * object is registered for this class name.</p>
 *
 * <p><b>Note:</b> The current implementation does not check all the 
 * base classes and interfaces that a given object supports.
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializer
 */
public class WddxObjectSerializerFactory extends java.lang.Object
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Registry of objects implementing WddxObjectSerializer
     * keyed by the class/interface name that they operate upon
     */
    private Hashtable m_serializers = new Hashtable();
    private Vector m_registeredClasses = new Vector();


    ///////////////////////////////////////////////////////////////////////
    //
    // Construction/Finalization
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Construct a WDDX object serializer factory
     *
     * Registers serializers for the basic object types supported as defined by the
     * wddx_java.properties file
     *
     */
    public WddxObjectSerializerFactory()
    {
        try{
            init(WddxDefaults.getProperties());
        }
        catch(WddxException e){
            //this should not occur because the default working properties are shipped with the
            //SDK and should not cause any problems.
            System.err.println("WDDX Java SDK bug: invalid default properties");
            e.printStackTrace();
        }
    }

    /**
     * Construct a WDDX object serializer factory using the specified properties configuration.
     * @throws WddxException if an error occurs while loading the factory.
     *
     */
    public WddxObjectSerializerFactory(Properties props) throws WddxException
    {
        init(props);
    }

    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////
    

    /**
     * Register a WDDX serializer object for a given class name.
     *
     * @param className Class name of the object to serialize.
     * @param os Object serializer to apply to instances of this class.
     *
     * @see com.allaire.wddx.WddxObjectSerializer
     */
    public synchronized void registerSerializer(String className, WddxObjectSerializer os) {
        try {
            Class cls = Class.forName(className);
            m_registeredClasses.addElement(cls);
            m_serializers.put(cls.getName(), os);
        }
        catch (ClassNotFoundException e) {
        }
    }
    
    
    /**
     * Register a WDDX serializer object for an object type.
     *
     * <p>The type of the object is determined by introspecting the 
     * class name of the provided object instance. Think of this as
     * 'serialization-by-example'.</p>
     *
     * @param obj Object instance of the type to be serialized.
     * @param os Object serializer to apply to instances of this type.
     *
     * @see com.allaire.wddx.WddxObjectSerializer
     */
    public synchronized void registerSerializer(Object obj, WddxObjectSerializer os) {
        m_registeredClasses.addElement(obj.getClass());
        m_serializers.put(obj.getClass().getName(), os);
    }
    
    
    /**
     * Retrieve a WDDX serializer object for a given class name.
     *
     * @return Object serializer to use or null if one cannot be found.
     * @param className The name of the class to retrieve a serializer for.
     *
     * @see com.allaire.wddx.WddxObjectSerializer
     */
    public synchronized WddxObjectSerializer getSerializer(String className)
    {
        WddxObjectSerializer serializer = null;

        Object obj = m_serializers.get(className);
        if (obj != null)
        {
            try
            {
                serializer = (WddxObjectSerializer)obj;
            }
            catch(ClassCastException e) {}
        }

        return serializer;
    }
    
    
    /**
     * Retrieve a WDDX serializer object for a given object.
     *
     * <p><b>Note:</b> The implementation should be extended to look
     * all all base classes and interfaces that the object implements.
     * Arrays of object types should be handled as well.</p>
     * 
     * <p><b>Suggestion:</b> A WddxSerializable interface can be defined. Object
     * types implementing this interface should know how to serialize
     * themselves.</p>
     *
     * @return Object serializer to use or null if one cannot be found.
     * @param obj The object instance to retrieve a serializer for.
     *
     * @see com.allaire.wddx.WddxObjectSerializer
     */
    public synchronized WddxObjectSerializer getSerializer(Object obj)
    {
        // __SIM: this can be extended to look at all base classes
        // and interfaces to try to find a serializer
        // It should also handle arrays of objects

        Class objClass = obj.getClass();
        String className = objClass.getName();
        WddxObjectSerializer ser = getSerializer(className);
        if (ser != null) {
            //found an explicitly registered serializer for this type.
            return ser;
        }
        else {
            //Check registered serializers for a suitable serializer for this object

            //Enumerate through the list of registered serializers to determine if
            //this object type is assignable to another serializer.
            Enumeration eenum = m_registeredClasses.elements();
            while (eenum.hasMoreElements() && ser == null) {
                Class cls = (Class)eenum.nextElement();
                if (cls.isAssignableFrom(objClass)) {
                    //found a suitable serializer
                    ser = getSerializer(cls.getName());
                }
            }
            if (ser != null) {
                //register this type explicitly to avoid future lookups.
                registerSerializer(objClass.getName(), ser);
            }
        }
        return ser;
    }

    /**
     * Initialize the factory instance using the specified configuration properties.
     *
     * Initialization properties format:
     * serializer.[index]=[class name of the object to serialize]=[class name of serializer to apply to instances of this class]
     * 
     * @param config the configuration properties
     *
     * @see com.allaire.wddx.WddxObjectSerializer
     */
    private void init(Properties config) throws WddxException{

        //load all serializer properties.
        //Note: serializers must be registered in the order specified in the properties file
        //      because the lookup for an object's serializer is based on the registration order.
        for(int i=1; true; i++){
            String configVal = config.getProperty("serializer." + i);
            if(configVal == null){
                break;
            }
            int index = configVal.indexOf("=");

            String type = configVal.substring(0, index);
            String className = configVal.substring(index+1);
            try{
                //attempt to instantiate the serializer
                WddxObjectSerializer ser = (WddxObjectSerializer)Class.forName(className).newInstance();

                //register the serializer
                registerSerializer(type, ser);
            }
            catch(ClassNotFoundException e){
                throw new WddxException("Serializer class not found: " + className, e);
            }
            catch(IllegalAccessException e){
                throw new WddxException("Serializer class not publicly accessible: " + className, e);
            }
            catch(InstantiationException e){
                throw new WddxException("Serializer class load error: " + className, e);
            }
        }
    }
}