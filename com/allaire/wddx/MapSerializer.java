package com.allaire.wddx;

import java.util.Map;
import java.util.Iterator;
import java.io.IOException;

/**
 * Serializes a Map
 *
 * <p>An instance of this class registered with the WDDX 
 * object serializer factory can be used to serialize 
 * instances of java.util.Map.</p>
 * <I>This serializer is only registered Java 1.2 or later</I>
 *
 * @author Spike Washburn (spike@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 * @see com.allaire.wddx.WddxObjectSerializer
 * @see java.util.Map
 */
class MapSerializer implements WddxObjectSerializer {
    /**
     * Write a Map to the output stream.
     *
     * @param out The WDDX output stream to use.
     * @param obj The object to serialize. Must be an instance of java.util.Map.
     *
     * @exception java.io.IOException The exception can be generated during 
     *      serialization or if the obj is not an instance of java.util.Map
     * 
     * @see java.util.List
     */
    public void writeObject(WddxOutputStream out, Object obj) throws IOException
    {
        try {
            if (obj != null) {
                Map m = (Map)obj;
                String type = (String)m.get("_wddx_structAttributes_type");
                if(type != null){
                    out.writeStructBegin();
                }
                else{
                    //preserve the reserved type variable as an attribute
                    out.writeStructBegin(type);
                }
                Iterator it = m.keySet().iterator();
                while (it.hasNext()) {
                    Object key = it.next();
                    Object value = m.get(key);
                    //only write the variable if it has not been preserved as an attribute
                    if(value != type){//fast compare for performance.

                       out.writeVarBegin(key.toString());
                       out.writeObject(value);
                       out.writeVarEnd();
                    }
                }
                out.writeStructEnd();
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
