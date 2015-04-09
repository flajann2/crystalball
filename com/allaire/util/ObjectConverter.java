package com.allaire.util;
import java.util.Vector;
import java.lang.reflect.Array;

/**
 * Converts objects from one type to another.
 * This class is useful for converting WDDX object types to
 * the strictly defined object types required as arguments
 * in the methods of JavaBeans.
 *
 * <p><b>Supported Conversions:</b>
 * <table border="1">
 *   <TR><TH>From</TH> <TH>To</TH>
 *   <TR><TD rowspan='5'>java.lang.Double</TD><TD>java.lang.Integer</TD>
 *   <TR><TD>java.lang.Long</TD>
 *   <TR><TD>java.lang.Float</TD>
 *   <TR><TD>java.lang.Double</TD>
 *   <TR><TD>java.lang.Short</TD>
 *   <TR><TD rowspan='1'>java.util.Vector</TD><TD>type[]</TD>
 *   <TR><TD rowspan='7'>java.lang.String</TD><TD>java.lang.Integer</TD>
 *   <TR><TD>java.lang.Long</TD>
 *   <TR><TD>java.lang.Float</TD>
 *   <TR><TD>java.lang.Double</TD>
 *   <TR><TD>java.lang.Short</TD>
 *   <TR><TD>java.lang.Character</TD>
 *   <TR><TD>java.lang.Boolean</TD>
 * </table>
 *
 * @author Spike Washburn (spike@allaire.com)
 * @version 1.0
 */
public class ObjectConverter {
    private ObjectConverter() {
        //no instances
    }

    /**
     * Convert an Object from one type to another.
     * @param value the object to convert
     * @param type the destination type
     * @returns the coverted object as its new type
     * @throws ObjectConversionException if the coversion failed.
     */
    public static Object convert(Object value, Class type) throws ObjectConversionException{
        if (type.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (value instanceof Double) {
            value = convert((Double)value, type);
        }
        else if (value instanceof String) {
            value = convert((String)value, type);
        }
        else if (value instanceof Vector) {
            value = convert((Vector)value, type);
        }
        return value;
    }

    private static Object convert(Double value, Class type) throws ObjectConversionException{
        try {
            if (type == Integer.TYPE || type.equals(Integer.class)) {
                return new Integer(value.intValue());
            }
            else if (type == Long.TYPE || type.equals(Long.class)) {
                return new Long(value.longValue());
            }
            else if (type == Float.TYPE || type.equals(Float.class)) {
                return new Float(value.floatValue());
            }
            else if (type == Double.TYPE || type.equals(Double.class)) {
                return new Double(value.doubleValue());
            }
            else if (type == Short.TYPE || type.equals(Short.class)) {
                return new Short(value.shortValue());
            }
        }
        catch (Exception e) {
            throw new ObjectConversionException("Failed to convert Double to type:" + type.getName(), e);
        }
        throw new ObjectConversionException("Failed to convert Double to type:" + type.getName());
    }

    private static Object convert(String value, Class type) throws ObjectConversionException{
        try {
            if (type == Integer.TYPE || type.equals(Integer.class)) {
                return new Integer(value);
            }
            else if (type == Long.TYPE || type.equals(Long.class)) {
                return new Long(value);
            }
            else if (type == Float.TYPE || type.equals(Float.class)) {
                return new Float(value);
            }
            else if (type == Double.TYPE || type.equals(Double.class)) {
                return new Double(value);
            }
            else if (type == Short.TYPE || type.equals(Short.class)) {
                return new Short(value);
            }
            else if (type == Character.TYPE || type.equals(Character.class)) {
                if (value.length() != 1) throw new ObjectConversionException("Can't convert String to char: " + value);
                return new Character(value.charAt(0));
            }
            else if (type == Boolean.TYPE || type.equals(Boolean.class)) {
                return new Boolean(value);
            }
        }
        catch (Exception e) {
            throw new ObjectConversionException("Failed to convert String to type:" + type.getName(), e);
        }
        throw new ObjectConversionException("Failed to convert String to type:" + type.getName());
    }

    private static Object convert(Vector value, Class type) throws ObjectConversionException{
        try {
            if (type.isArray()) {
                //get the array element type
                //the type that was sent in will actually be the actual array type
                //rather than the type of the elements in the array.
                Class elementType = getClassBySignature(type.getName().substring(1));

                int size = value.size();
                Object array = Array.newInstance(elementType, size);
                for (int i=0; i<size; i++) {
                    Array.set(array, i, convert(value.elementAt(i), elementType));
                }
                return array;
            }
        }
        catch (Exception e) {
            throw new ObjectConversionException("Failed to convert Vector to type:" + type.getName(), e);
        }
        throw new ObjectConversionException("Failed to convert Vector to type:" + type.getName());
    }

    /**
     * Returns the class defined by the JNI Type signature
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
