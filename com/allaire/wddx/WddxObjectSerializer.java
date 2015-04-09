package com.allaire.wddx;

import java.io.IOException;

/**
 * Provides an arbitrary object serialization interface
 *
 * <p>The interface provides a means by which any object can be 
 * serialized to a WDDX output stream. Objects implementing this 
 * interface are registered with the WDDX serializer factory.</p>
 *
 * @author Simeon Simeonov (simeons@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 */
public interface WddxObjectSerializer
{
    /**
     * Write an object to an output stream.
     *
     * @param out The WDDX output stream to use.
     * @param obj The object to serialize.
     *
     * @exception java.io.IOException The exception can be generated during serialization
     */
    void writeObject(WddxOutputStream out, Object obj) throws IOException;
}