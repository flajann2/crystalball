package com.allaire.wddx;

import java.util.List;
import java.io.IOException;

/**
 * Serializes a List
 *
 * <p>An instance of this class registered with the WDDX 
 * object serializer factory can be used to serialize 
 * instances of java.util.List.</p>
 * <I>This serializer is only registered Java 1.2 or later</I>
 *
 * @author Spike Washburn (spike@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 * @see com.allaire.wddx.WddxObjectSerializer
 * @see java.util.List
 */
class ListSerializer implements WddxObjectSerializer {
    /**
     * Write a list to the output stream.
     *
     * @param out The WDDX output stream to use.
     * @param obj The object to serialize. Must be an instance of java.util.List.
     *
     * @exception java.io.IOException The exception can be generated during 
     *      serialization or if the obj is not an instance of java.util.List
     * 
     * @see java.util.List
     */
    public void writeObject(WddxOutputStream out, Object obj) throws IOException
    {
        try {
            if (obj != null) {
                List l = (List)obj;
                out.writeArrayBegin(l.size());
                for (int i = 0; i < l.size(); ++i) {
                    out.writeObject(l.get(i));
                }
                out.writeArrayEnd();
            }
            else {
                out.writeNull();
            }
        }
        catch (ClassCastException e) {
            throw new IOException(
                                 "Invalid cast exception for map");
        }
    }
}
