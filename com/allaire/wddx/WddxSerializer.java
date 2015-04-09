package com.allaire.wddx;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.IOException;


/**
 * Provides WDDX serialization services
 *
 * <p>The class manages WDDX serialization.
 * It allows serialization to be performed using different object
 * serialization factories. This feature can be used to add serialization
 * capabilities for arbitrary Java objects.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 */
public class WddxSerializer extends java.lang.Object
{
    ///////////////////////////////////////////////////////////////////////
    //
    // Implementation data
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Default object serializer factory.
     */
    private static WddxObjectSerializerFactory m_staticSerializerFactory = new WddxObjectSerializerFactory();
    
    /**
     * Version number of the WDDX packets the serializer will be producing.
     */
    protected String m_version;
    
    /**
     * The object serializer factory this instance is using.
     */
    protected WddxObjectSerializerFactory m_serializerFactory;


    ///////////////////////////////////////////////////////////////////////
    //
    // Construction/Finalization
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Construct a WDDX serializer object using the default object serializer factory.
     *
     * @exception java.lang.IllegalArgumentException The exception can be generated during initialization
     *
     */
    public WddxSerializer() throws IllegalArgumentException
    {
        init("1.0", m_staticSerializerFactory);
    }

    /**
     * Construct a WDDX serializer object using a given object serializer factory.
     *
     * @param serializerFactory The object serializer factory to use.
     *
     * @exception java.lang.IllegalArgumentException The exception can be generated during initialization
     *
     * @see com.allaire.wddx.WddxObjectSerializerFactory
     */
    public WddxSerializer(WddxObjectSerializerFactory serializerFactory) throws IllegalArgumentException
    {
        init("1.0", serializerFactory);
    }


    ///////////////////////////////////////////////////////////////////////
    //
    // Operations
    //
    ///////////////////////////////////////////////////////////////////////


    /**
     * Initializes the serializer.
     *
     * @param version WDDX specification version.
     * @param serializerFactory serialization factory to use
     *
     * @exception java.lang.IllegalArgumentException The exception can be generated because of null or invalid arguments
     *
     * @see com.allaire.wddx.WddxObjectSerializerFactory
     */
    private void init(String version, WddxObjectSerializerFactory serializerFactory) throws IllegalArgumentException
    {
        // Validate version
        if (version == null || version.compareTo("1.0") != 0)
        {
            throw new IllegalArgumentException("Invalid version number");
        }
        
        // Validate serializer factory
        if (serializerFactory == null)
        {
            throw new IllegalArgumentException("Null serializer factory");
        }

        // Initialize
        m_version = version;
        m_serializerFactory = serializerFactory;
    }

    /**
     * Retrives the serializer factory used by the instance.
     *
     * @return The serializer factory used by the instance.
     * @see com.allaire.wddx.WddxObjectSerializerFactory
     */
    public WddxObjectSerializerFactory getSerializerFactory()
    {
        return m_serializerFactory;
    }
    
    /**
     * Retrives the default serializer factory used by the class.
     *
     * @return The default serializer factory.
     * @see com.allaire.wddx.WddxObjectSerializerFactory
     */
    public static WddxObjectSerializerFactory getDefaultSerializerFactory()
    {
        return m_staticSerializerFactory;
    }
    
    /**
     * Serializes an object to a writer.
     *
     * @param obj The object to serialize
     * @param out The Writer to output the WDDX packet to
     *
     * @exception java.lang.NullPointerException The exception can be generated because of a null writer
     * @exception java.io.IOException The exception can be generated during the serialization process
     *
     * @see com.allaire.wddx.WddxOutputStream
     */
    public void serialize(Object obj, Writer out) throws NullPointerException, IOException
    {
        // Validate input
        if (out == null)
        {
            throw new IllegalArgumentException("Null output writer");
        }
        
        // Create a print writer
        PrintWriter pw = new PrintWriter(out);

        // Output the WDDX packet beginning
        pw.print("<wddxPacket version='1.0'><header/><data>");

        // Create a WDDX output stream and serialize the data
        WddxOutputStream os = new WddxOutputStream(this, pw);
        os.writeObject(obj);        
        
        // Close the WDDX packet
        pw.print("</data></wddxPacket>");
    }
}