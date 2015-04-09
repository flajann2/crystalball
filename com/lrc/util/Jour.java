/*
 * Jour.java
 *
 * Created on August 10, 2001, 11:10 PM
 */
package com.lrc.util;

import java.text.*;

import java.util.*;


/**
 * Jour represents daytime in milliseconds since midnight. This is for database purposes.
 *
 * @author fred
 * @version
 */
public final class Jour extends java.sql.Time {
    /** Milliseconds per second */
    public static final long MPS = 1000L;

    /** Milliseconds Per Minute */
    public static final long MPM = MPS*60L;

    /** minutes per Jour (DAY) */
    public static final int JDAY = 60*24;

    /** Milliseconds per DAY */
    public static final long MDAY = JDAY*MPM;

    /** Minutes per Hour */
    public static final long MinPH = 60L;
    private static final String sdf_hhmmss = "HH:mm:ss";
    private static final String sdf_hhmm = "HH:mm";
    private static Calendar cal = new GregorianCalendar();

    /**
     * Creates new Jour
     *
     * @param jour DOCUMENT ME!
     *
     * @throws JourException DOCUMENT ME!
     * @throws Jour.JourException DOCUMENT ME!
     */
    public Jour(int jour) throws JourException {
        super(jour*MPM);

        if (getJour() >= JDAY)
            throw new Jour.JourException("Cannot be longer than a JDAY: "+getJour());
    }

    public Jour(long milliseconds) throws JourException {
        super(milliseconds);

        if (getTime() >= MDAY)
            throw new Jour.JourException("Cannot be longer than a MDAY: "+getTime());
    }

    /**
     * Convert a Date object into a Jour object.
     *
     * @param d DOCUMENT ME!
     */
    public Jour(Date d) {
        super(0);
        setFromString(new SimpleDateFormat(sdf_hhmmss).format(d));
    }

    public Jour(String hhmmss) {
        super(0);
        setFromString(hhmmss);
    }

    public Jour(int hh, int mm, int ss) {
        super(0);

        long t1 = ((long) hh*MinPH*MPM)+(mm*MPM)+(ss*MPS);
        setLocalTime(t1);
    }

    void setFromString(String hhmmss) {
        try {
            if (hhmmss.split(":", 0).length == 2)
                hhmmss += ":00";

            setTime(new SimpleDateFormat(sdf_hhmmss).parse(hhmmss).getTime());
        } catch (java.text.ParseException pe) {
            pe.printStackTrace();
        }
    }

    public int getHours() {
        return super.getHours();
    }

    public int getMinutes() {
        return super.getMinutes();
    }

    public int getSeconds() {
        return super.getSeconds();
    }

    /**
     * return time value in terms of minutes past midnight!
     *
     * @return DOCUMENT ME!
     */
    public int getJour() {
        return (int) (getLocalTime()/MPM);
    }

    public int obtenezDeJour() {
        return getJour();
    }

    public String toString() {
        return "'"+super.toString()+"'";
    }

    public final String stringValue() {
        return super.toString();
    }

    public static void main(String[] av) {
        try {
            Jour j1 = new Jour("24:00:00");
            Jour j2 = new Jour("09:30:00");
            Jour j3 = new Jour(14, 35, 27);
            Jour j4 = new Jour("24:00"); // this will cause a format error but none will be tossed.

            System.out.println("\n\nstring      j1="+j1+", j2="+j2+", j3="+j3
                               +", j4 (maliagned)="+j4);
            System.out.println("millisecond j1="+j1.getLocalTime()+", j2="
                               +j2.getLocalTime()+", j3="+j3.getLocalTime());
            System.out.println("Market time j1="+j1.getJour()+", j2="+j2.getJour()
                               +", j3="+j3.getJour()+"\n\n");
            System.out.println("getTZOffset() = "+(Jour.getTZOffset()/MPM/MinPH));

            // this is supposed to throw an exception here!
            new Jour(JDAY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long getTZOffset() {
        return cal.get(Calendar.ZONE_OFFSET);
    }

    public long getLocalTime() {
        return (getTime()+getTZOffset())%MDAY;
    }

    public void setLocalTime(long mills) {
        setTime((mills-getTZOffset()+MDAY)%MDAY);
    }

    public long getTime() {
        return super.getTime()%MDAY;
    }

    public void setTime(long t) {
        super.setTime(t%MDAY);
    }

    /**
     * DOCUMENT ME!
     *
     * @param when DOCUMENT ME!
     *
     * @return true if this time of day is before specified time of day.
     */
    public boolean before(Date when) {
        return getTime() < new Jour(when).getTime();
    }

    /**
     * DOCUMENT ME!
     *
     * @param when DOCUMENT ME!
     *
     * @return true if this time of day is after specified time of day.
     */
    public boolean after(Date when) {
        return getTime() > new Jour(when).getTime();
    }

    /**
     * DOCUMENT ME!
     *
     * @param when DOCUMENT ME!
     *
     * @return true if this time of day is equal to specified time of day.
     */
    public boolean equals(Date when) {
        return !before(when) && !after(when);
    }

    public int compareTo(Date when) {
        if (before(when))
            return -1;

        if (after(when))
            return 1;

        return 0;
    }

    //public int compareTo(Object o) {
    //    return compareTo((Date) o);
    //}

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision: 1.2 $
     */
    public class JourException extends java.lang.Exception {
        JourException(String mess) {
            super(mess);
        }
    }
}
