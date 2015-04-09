package com.lrc.util;

import java.lang.reflect.*;

import java.net.*;

import java.text.NumberFormat;

import java.util.*;


/**
 * Utility functions.
 */
public class Util {
    static private char[] digit =
    { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    static final int linelength = 16; // bytes per line

    private Util() {}

    /**
     * converts byte to a 2-digit hex number.
     *
     * @param b DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static String byteToHex(byte b) {
        int lower = b & 0xF;
        int upper = (b >> 8) & 0xF;

        return ""+digit[upper]+digit[lower];
    }

    /**
     * Dump an array of bytes in hexadecimal fashion.
     *
     * @param bar DOCUMENT ME!
     * @param todump DOCUMENT ME!
     */
    public static void dumpBytes(byte[] bar, int todump) {
        if (todump > bar.length)
            todump = bar.length;

        System.out.println("Dumping "+todump+" bytes of a "+bar.length+"-byte array.");

        for (int line = 0; line < ((todump/linelength)+1); ++line) {
            String ls = " ";
            int half = linelength/2;

            for (int i = 0; i < linelength; ++i) {
                int o = i+(linelength*line);

                if (o < bar.length)
                    ls += byteToHex(bar[o]);
                else
                    ls += "..";

                if (i == (half-1))
                    ls += "-";
                else
                    ls += " ";
            }

            ls += "  \"";

            for (int i = 0; i < linelength; ++i) {
                int o = i+(linelength*line);
                char c = (char) ((o < bar.length) ? bar[o] : ' ');

                if ((c < ' ') || (c > 127))
                    c = '.';

                ls += c;
            }

            ls += "\"";
            System.out.println(ls);
        }
    }

    /**
     * Convert numeric to string IP
     *
     * @param ip DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String toStringIPAddr(int ip) {
        return ""+((ip >>> 24) & 0xFF)+"."+((ip >>> 16) & 0xFF)+"."+((ip >>> 8) & 0xFF)
               +"."+(ip & 0xFF);
    }

    public static int ubyteToInt(byte b) {
        return ((int) b) & 0xff;
    }

    /**
     * Convert InetAddress to string IP
     *
     * @param ip DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String toStringIPAddr(InetAddress ip) {
        byte[] a = ip.getAddress();

        return ""+ubyteToInt(a[0])+"."+ubyteToInt(a[1])+"."+ubyteToInt(a[2])+"."
               +ubyteToInt(a[3]);
    }

    /**
     * Convert string to an IP stuffed in an int.
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IPConversionException DOCUMENT ME!
     */
    public static int valueOfIP(String s) throws IPConversionException {
        StringTokenizer st = new StringTokenizer(s, ".");

        // There should be exactly 4 tokens
        if (st.countTokens() != 4)
            throw new IPConversionException("IP Format Error: "+s);

        int ip;

        try {
            //log.warnln("IP string = " + s);
            ip = (Integer.valueOf(st.nextToken()).intValue() & 0xff) << 24;
            ip |= ((Integer.valueOf(st.nextToken()).intValue() & 0xff) << 16);
            ip |= ((Integer.valueOf(st.nextToken()).intValue() & 0xff) << 8);
            ip |= (Integer.valueOf(st.nextToken()).intValue() & 0xff);

            //log.warnln("IP number = " + Util.toStringIPAddr(ip));
        } catch (Exception e) {
            throw new IPConversionException(e.toString());
        }

        return ip;
    }

    public static boolean isDigit(char c) {
        return (c >= '0') && (c <= '9');
    }

    /**
     * Truncate double decimals to indicated precision.
     *
     * @param d double to be converted to string.
     * @param mantissa precision to trim the mantissa portion.
     *
     * @return DOCUMENT ME!
     */
    public static String toString(double d, int mantissa) {
        String s = Double.toString(d);
        StringBuffer sb = new StringBuffer();

        boolean point = false;
        boolean cleared = false;

        int point_count = 0;

        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);

            if (c == '.') // decimal point?

                point = true;

            else if (point && !isDigit(c))
                cleared = true;

            if (!point || (point_count++ <= mantissa) || cleared)
                sb.append(c);
        }

        return sb.toString();
    }

    /**
     * Take an object and a Hashtable, and fill in its fields on the basis of using its
     * field names as keys to the Hashtable.
     * 
     * <p>
     * Values in Hastable must be convertable to the fields in Object
     * </p>
     * 
     * <p>
     * Fields in object must be public for them to be introspected.
     * </p>
     *
     * @param h DOCUMENT ME!
     * @param o DOCUMENT ME!
     *
     * @return target object passed in.
     *
     * @throws IllegalArgumentException DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     *
     * @see #ObjectToHashtable
     */
    public static Object HashtableToObject(Hashtable h, Object o)
                                    throws IllegalArgumentException, 
                                           IllegalAccessException {
        //log.noteln("HashtableToObject: hash="+h+", Object="+o);
        Field[] f = o.getClass().getFields();

        for (int i = 0; i < f.length; ++i) {
            Object hashfield = h.get(f[i].getName());

            if (hashfield != null)
                f[i].set(o, hashfield);

            //else
            //log.warnln("**** **** **** HashtableToObject: null hashfield for " + f[i]);
        }

        return o;
    }

    /**
     * Take an Object and convert its fields to a Hashtable using the Field names as
     * keys.
     * 
     * <p></p>
     *
     * @param o object to be converted.
     * @param h hastable to receive the new fields. Can be null, in which case one will
     *        be newed automatically.
     *
     * @return DOCUMENT ME!
     *
     * @throws IllegalArgumentException DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     *
     * @see #HashtableToObject
     */
    public static Hashtable ObjectToHashtable(Object o, Hashtable h)
                                       throws IllegalArgumentException, 
                                              IllegalAccessException {
        if (h == null)
            h = new Hashtable();

        Field[] f = o.getClass().getFields();

        for (int i = 0; i < f.length; ++i)
            h.put(f[i].getName(), f[i].get(o));

        return h;
    }
}
