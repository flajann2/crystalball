package com.allaire.wddx;

import java.util.Map;
import java.util.Iterator;
import java.io.IOException;
import java.util.Vector;

import java.lang.reflect.*;
import java.beans.*;


/**
 * Serializes a Java Bean
 *
 * <p>An instance of this class registered with the WDDX 
 * object serializer factory can be used to serialize 
 * instances of java.lang.Object</p>
 *
 * @author Spike Washburn (spike@allaire.com)
 * @version 1.0
 * @see com.allaire.wddx.WddxObjectSerializerFactory
 * @see com.allaire.wddx.WddxOutputStream
 * @see com.allaire.wddx.WddxObjectSerializer
 * @see java.util.Map
 */
class BeanSerializer implements WddxObjectSerializer {


public void writeObject(WddxOutputStream out, Object obj) throws IOException{
        Class cls = obj.getClass();
        try {
            if (cls.isArray()) {
                //if this object is an array, then serialize it as a WDDX array.
                Object[] array = (Object[])obj;
                out.writeArrayBegin(array.length);
                for (int i=0; i<array.length; i++) {
                    out.writeObject(array[i]);
                }
                out.writeArrayEnd();
            }
            else {
                //serialize this bean and its properties as a struct.

                //a BeanInfo is used to examine the properties of a bean
                BeanInfo binfo = Introspector.getBeanInfo(cls, Object.class);
                PropertyDescriptor[] descriptors = binfo.getPropertyDescriptors();

                //Start the strictly typed struct
                out.writeStructBegin(getTypeSignature(obj.getClass()));
                
                for (int i=0; i<descriptors.length; i++) {
                    PropertyDescriptor pd = descriptors[i];
                    String name = pd.getName();
                    try {
                        if (pd instanceof IndexedPropertyDescriptor) {
                            IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)pd;

                            //extract all indexed values and write them as a WDDX array
                            Vector values = new Vector();//used to store indexed values

                            //locate the getter for this property
                            Method getter = ipd.getIndexedReadMethod();
                            try{
                               getter.setAccessible(true);
                            }
                            catch(Error e){
                                //occurs on pre Java2 JVM's
                            }
                            if (getter != null) {
                                try {
                                    //invoke the reader for each index and store it in the vector.
                                    for (int index=0; true; index++) {
                                        Object value = getter.invoke(obj, new Object[]{new Integer(index)});
                                        values.addElement(value);
                                    }
                                }
                                catch (IllegalAccessException e) {
                                    //this is not a public reader, so its value will not be serialized
                                }
                                catch (InvocationTargetException e) {
                                    if (e.getTargetException() instanceof IndexOutOfBoundsException) {
                                        //loop break condition
                                    }
                                    else {
                                        //exception was thrown by getter method
                                        throw new IOException("Error reading property " + cls.getName() + "." + name + ": " + e.getTargetException().getMessage());
                                    }
                                }

                                //serialize the vector that contains all of the indexed property values
                                int size = values.size();
                                out.writeVarBegin(name);
                                out.writeArrayBegin(size);
                                for (int index=0; index<size; index++) {
                                    out.writeObject(values.elementAt(index));
                                }
                                out.writeArrayEnd();
                                out.writeVarEnd();
                            }
                            else {
                                //this is not a readable property so it value will not be serialized
                            }
                        }
                        else {
                            //invoke the getter for this property and serialize its value.
                            Method getter = pd.getReadMethod();
                            try{
                               getter.setAccessible(true);
                            }
                            catch(Error e){
                                //occurs on pre Java2 JVM's
                            }
                            if (getter != null) {
                                Object value = getter.invoke(obj, new Object[0]);
                                if (value != null) {
                                    out.writeVarBegin(name);
                                    out.writeObject(value);
                                    out.writeVarEnd();
                                }
                            }
                            else {
                                //this is not a readable property so it value will not be serialized
                            }
                        }
                    }
                    catch (IllegalAccessException e) {
                        //this is not a public getter, so its value will not be serialized
                        //e.printStackTrace();
                    }
                    catch (InvocationTargetException e) {
                        //exception was thrown by getter method
                        throw new IOException("Error reading property " + cls.getName() + "." + name + ": " + e.getTargetException().getMessage());
                    }
                }
                out.writeStructEnd();
            }
        }
        catch (IntrospectionException e) {
            throw new IOException("IntrospectionException: " + cls.getName());
        }


    }

    static String getTypeSignature(Class cls) {
        StringBuffer sig = new StringBuffer();
        if (cls.isArray()) {
            return cls.getName();
        }
        if (cls.isPrimitive()) {
            if (cls == Integer.TYPE) {
                sig.append('I');
            }
            else if (cls == Byte.TYPE) {
                sig.append('B');
            }
            else if (cls == Long.TYPE) {
                sig.append('J');
            }
            else if (cls == Float.TYPE) {
                sig.append('F');
            }
            else if (cls == Double.TYPE) {
                sig.append('D');
            }
            else if (cls == Short.TYPE) {
                sig.append('S');
            }
            else if (cls == Character.TYPE) {
                sig.append('C');
            }
            else if (cls == Boolean.TYPE) {
                sig.append('Z');
            }
        }
        else {
            sig.append('L');
            sig.append(cls.getName());
            sig.append(';');
        }
        return sig.toString();
    }

}
