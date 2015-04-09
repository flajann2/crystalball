package com.lrc.util;

import java.text.SimpleDateFormat;

import java.util.*;


/**
 * Useful class for manipulating time
 */
public final class Temps extends java.util.Date {
    static private final Jour bod = new Jour("00:00:00"); // Beginning of Day
    static public final long second = 1000;
    static public final long minute = second*60;
    static public final long hour = minute*60;
    static public final long day = hour*24;
    static public final long week = day*7;
    static public final String sdf_date = "yyyy-MM-dd";
    static private final String sdf_datejour = "yyyy-MM-dd''HH:mm:ss''";
    public static boolean debug = false;
    private static long spoofFactor = 0L;
    private static Calendar cal = new GregorianCalendar();
    private transient long dbhh = 9;
    private transient long dbmm = 30;
    private transient long daybegins = (dbhh*hour)+(dbmm*minute);
    private transient long dehh = 16;
    private transient long demm = 0;
    private transient long dayends = (dehh*hour)+(demm*minute);
    private transient boolean skipWeekends = true;
    private transient boolean skipHolidays = true;
    private transient Date beginningOfDay = null;

    /**
     * "Current" system time is embodied by this constructor. Temps is slick enough to
     * allow time spoofing. For time spoofing to work, one must call Temps.timeSpoof()
     * with what the "current" time is supposed to be. After that, all future creations
     * through this constructor will be adjusted relative to the spoofed time.
     * 
     * <p>
     * Know then, that ALL code you want to be spoofed MUST use new Temps() instead of
     * the usual new Date().
     * </p>
     * 
     * <p></p>
     *
     * @see #timeSpoof()
     */
    public Temps() {
        super(); // for clarity
        setTime(getTime()+spoofFactor);

        if (debug)
            System.out.println("new Temps(): "+this+", spoofFactor = "+spoofFactor);
    }

    public Temps(String yyyymmdd) throws java.text.ParseException {
        this(new SimpleDateFormat(sdf_date).parse(yyyymmdd));
    }

    public Temps(long temps) {
        super(temps);
    }

    public Temps(Date date) {
        super();
        setTime(date.getTime());
    }

    /**
     * Takes a Date and a Jour, and creates a new Date set to daily time of Jour.
     *
     * @param date DOCUMENT ME!
     * @param jour DOCUMENT ME!
     *
     * @throws java.text.ParseException DOCUMENT ME!
     */
    public Temps(Date date, Jour jour) throws java.text.ParseException {
        this(new SimpleDateFormat(sdf_datejour).parse(new SimpleDateFormat(sdf_date)
                                                      .format(date)+jour));
    }

    public void setDayBegins(long hh, long mm) {
        daybegins = (hh*hour)+(mm*minute);
        dbhh = hh;
        dbmm = mm;
    }

    public void setDayEnds(long hh, long mm) {
        dayends = (hh*hour)+(mm*minute);
        dehh = hh;
        demm = mm;
    }

    public void setSkipWeekends(boolean t) {
        skipWeekends = t;
    }

    public boolean isSkipWeekends() {
        return skipWeekends;
    }

    public void setSkipHolidays(boolean t) {
        skipHolidays = t;
    }

    public boolean isSkipHolidays() {
        return skipHolidays;
    }

    /**
     * Spoof the "system" time to be relative to the given time. The given time is
     * counted as "now", and the spoofed time is calculated as an offset based on the
     * difference between the ACTUAL current  time and the spoofed time.
     * 
     * <p></p>
     *
     * @param spoof Time to spoof for or null to eliminate spoofing.
     */
    public static void timeSpoof(Date spoof) {
        if (spoof == null)
            spoofFactor = 0L;
        else {
            Date real = new Date();
            spoofFactor = spoof.getTime()-real.getTime();
        }
    }

    public static void noTimeSpoof() {
        timeSpoof(null);
    }

    public static boolean isTimeSpoofed() {
        return spoofFactor != 0L;
    }

    /**
     * Increments this Temps for the indicated period.
     *
     * @param period DOCUMENT ME!
     *
     * @return this Temps.
     */
    public Temps increment(long period) {
        if (beginningOfDay == null)
            beginningOfDay = Clock.aujourdHuiA(this, bod);

        if ((period < day) && ((getTime()+period) > (beginningOfDay.getTime()+dayends))) {
            beginningOfDay = Clock.demain(this, bod);
            setTime(beginningOfDay.getTime()+daybegins);
        } else
            setTime(getTime()+period);

        if (doWeSkip())
            incDay();

        return this;
    }

    private boolean doWeSkip() {
        if (skipHolidays || skipWeekends) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(this);

            if ((skipWeekends && Clock.isWeekend(cal))
                    || (skipHolidays && Clock.isUSHoliday(cal)))
                return true;
        }

        return false;
    }

    /**
     * Returns true if the time is on a business day. Normally, thie business day is
     * defined as a day the American stock markets are open.
     * 
     * <p>
     * Ignores the skip settings.
     * </p>
     *
     * @return DOCUMENT ME!
     */
    public boolean isBusinessDay() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(this);

        return !Clock.isWeekend(cal) && !Clock.isUSHoliday(cal);
    }

    /**
     * Decrements this Temps for the indicated period.
     *
     * @param period DOCUMENT ME!
     *
     * @return this Temps.
     */
    public Temps decrement(long period) {
        if (beginningOfDay == null)
            beginningOfDay = Clock.aujourdHuiA(this, bod);

        if ((period < day) && ((getTime()-period) < (beginningOfDay.getTime()+daybegins))) {
            beginningOfDay = Clock.heir(this, bod);
            setTime(beginningOfDay.getTime()+dayends);
        } else
            setTime(getTime()-period);

        if (doWeSkip())
            decDay();

        return this;
    }

    public Temps incSecond() {
        return increment(second);
    }

    public Temps decSecond() {
        return decrement(second);
    }

    public Temps incSecond(int s) {
        return increment(second*s);
    }

    public Temps decSecond(int s) {
        return decrement(second*s);
    }

    public Temps incMinute() {
        return increment(minute);
    }

    public Temps decMinute() {
        return decrement(minute);
    }

    public Temps incMinute(int min) {
        return increment(minute*min);
    }

    public Temps decMinute(int min) {
        return decrement(minute*min);
    }

    public Temps incHour() {
        return increment(hour);
    }

    public Temps decHour() {
        return decrement(hour);
    }

    public Temps incHour(int h) {
        return increment(hour*h);
    }

    public Temps decHour(int h) {
        return decrement(hour*h);
    }

    public Temps incDay() {
        return increment(day);
    }

    public Temps decDay() {
        return decrement(day);
    }

    public Temps incVigil(int vigil, int count) {
        while (count-- > 0)
            incMinute(vigil);

        return this;
    }

    public Temps decVigil(int vigil, int count) {
        while (count-- > 0)
            decMinute(vigil);

        return this;
    }

    /**
     * Note that this increments the days one at a time to allow for skipping of weekends
     * and holidays.
     *
     * @param d DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Temps incDay(int d) {
        while (d-- > 0)
            increment(day);

        return this;
    }

    /**
     * Note that this decrements the days one at a time to allow for skipping of weekends
     * and holidays.
     *
     * @param d DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Temps decDay(int d) {
        while (d-- > 0)
            decrement(day);

        return this;
    }

    /**
     * Set date to day of the calendar, preserving time of day.
     *
     * @param jour DOCUMENT ME!
     */
    public void jeuAJour(Calendar jour) {
        // create a calendar for ourselves
        Calendar cela = Calendar.getInstance();
        cela.setTime(this);

        // now, set cela to year, month, and date.
        cela.set(Calendar.YEAR, jour.get(Calendar.YEAR));
        cela.set(Calendar.MONTH, jour.get(Calendar.MONTH));
        cela.set(Calendar.DATE, jour.get(Calendar.DATE));

        // reset to cela time
        this.setTime(cela.getTime().getTime());
    }

    /**
     * Set date to day of the calendar, preserving time of day.
     * 
     * <P>
     * Convience function.
     * </p>
     *
     * @param d_jour DOCUMENT ME!
     */
    public void jeuAJour(Date d_jour) {
        Calendar cest = Calendar.getInstance();
        cest.setTime(d_jour);
        jeuAJour(cest);
    }

    public static long getTZOffset() {
        return cal.get(Calendar.ZONE_OFFSET); /* + cal.get(Calendar.DST_OFFSET); */
    }

    public long getLocalTime() {
        return getTime()+getTZOffset();
    }

    public void setLocalTime(long mills) {
        setTime(mills-getTZOffset());
    }
}
