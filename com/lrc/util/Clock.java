package com.lrc.util;

import java.util.*;


//import java.util.Calendar;
//import java.util.GregorianCalendar;

/**
 * Useful utilities for time and scheduling. This class is NOT thread-safe!  Only one
 * thread may use it at any given time!
 * 
 * <P></p>
 */
public final class Clock {
    static private WeakHashMap tempsMap = new WeakHashMap(89);
    static private ThreadLocal tls_cal =
        new ThreadLocal() {
            protected Object initialValue() {
                return new GregorianCalendar();
            }
        };

    static private Calendar startup = new GregorianCalendar();

    static {
        startup.setTime(new Temps());
    }
     // startup time

    static private long one_minute = 1000*60; // milliseconds equivalent to one minute

    static {
        Thread clockThread =
            new Thread(new Runnable() {
                    public void run() {
                        clockRun();
                    }
                }, "Clock Thread: "+Clock.class.getName());
        clockThread.setDaemon(true);
        clockThread.start();
    }

    private static List jobs = Collections.synchronizedList(new LinkedList());

    static private Calendar cal() {
        return (Calendar) tls_cal.get();
    }

    /**
     * Adds a Temps date object to be kept up to date, I.E., will be bumped up one day at
     * transistion time (midnight). Said bumping will respect holidays and weekends;
     * that is, will skip days that are weekends and holidays.
     * 
     * <p>
     * The references to the Temps objects are held weakly, so that when all references
     * to a given Temp object goes away, it is automatically dropped here.
     * </p>
     *
     * @param t DOCUMENT ME!
     */
    static public void addTempsToBump(Temps t) {
        tempsMap.put(t, null);
    }

    /**
     * One-shot "cron job" facility.
     * 
     * <p>
     * Note that the time resolution is no greater than one minute for the launching of a
     * job. So, if there are tasks that need to be performed serially, then they should
     * be spaced greater than a minute apart.
     * </p>
     * 
     * <p>
     * Each job is launched in its own seperate daemon thread, and is terminated when the
     * run() method returns.
     * </p>
     * 
     * <p>
     * If the launchTime has already passed, the job will be launched almost immediately
     * (within a minute of the call).
     * </p>
     * 
     * <p>
     * This launch is a "one-shot" -- it launches the job once, then removes the job from
     * the queue. If subsequent launches are needed, then the job itself may resubmit
     * itself. Keep in mind that there is the possibility that a "relaunch" may occur
     * during the running of the job if the old job is still running at the new launch
     * time.
     * </p>
     * 
     * <p></p>
     *
     * @param job DOCUMENT ME!
     * @param launchTime DOCUMENT ME!
     */
    public static void addJob(Runnable job, Date launchTime) {
        jobs.add(new ChronJob(launchTime, job));
    }

    /**
     * daemon thread to keep the clock.
     */
    static private void clockRun() {
        List tq = new LinkedList();
        Calendar tcal = new GregorianCalendar();
        boolean tempsUpdated = false;

        while (true) {
            try {
                Thread.sleep(one_minute);

                Date now = new Temps();
                cal().setTime(now);

                //// Update Temps objects for next day! Do this at 1 AM.
                if (!tempsUpdated // not updated yet?
                        && (cal().get(Calendar.HOUR_OF_DAY) == 1) // advance at 1 AM
                        && (startup.get(Calendar.DAY_OF_MONTH) != cal().get(Calendar.DAY_OF_MONTH))) // we don't advance on the same day of startup!!!
                 {
                    Object[] tar = tempsMap.keySet().toArray();

                    for (int i = 0; i < tar.length; ++i) {
                        if (tar[i] != null) {
                            Temps t = (Temps) tar[i];
                            tcal.setTime(t);

                            do {
                                tcal.add(Calendar.DAY_OF_MONTH, 1);
                            } while (!isWorkday(tcal));

                            t.setTime(tcal.getTime().getTime());
                        }
                    }

                    tempsUpdated = true;
                } else if (tempsUpdated && (cal().get(Calendar.HOUR_OF_DAY) == 2))
                    tempsUpdated = false; // reset so next morning can execute.

                //// Handle scheduled jobs!
                synchronized (jobs) {
                    Iterator jit = jobs.iterator();

                    while (jit.hasNext()) {
                        ChronJob cj = (ChronJob) jit.next();

                        if (now.after(cj.time)) {
                            tq.add(cj);
                            jit.remove();
                        }
                    }
                }

                // now that we are out of the synchronized block, we can safely invoke the jobs!
                Iterator it = tq.iterator();

                while (it.hasNext()) {
                    ChronJob cj = (ChronJob) it.next();
                    it.remove();

                    Thread t = new Thread(cj.job);
                    t.setDaemon(true);
                    t.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Puts calling thread to sleep until the time in Date is reached.
     * 
     * <p>
     * There is a bug, apparently, in the Winodows version of the JVM,  in so that long
     * wait times fail to "wake up." The way I will now get around this problem is to
     * have it  wake up every minute and check to see if the current time is past the
     * wakeup time. This will give it an imprecise resolution (about a minute), but will
     * fix the bug.
     * </p>
     * 
     * <p>
     * I have implemented the resolution.
     * </p>
     *
     * @param d DOCUMENT ME!
     *
     * @throws InterruptedException DOCUMENT ME!
     */
    static public void dormezJusqua(final Date d) throws InterruptedException {
        Date curtime = new Temps();
        long timeToSleep = d.getTime()-curtime.getTime();
        boolean loop = true;

        while (loop) {
            curtime = new Temps();

            if (curtime.before(d)) {
                timeToSleep = d.getTime()-curtime.getTime();

                if (timeToSleep > (one_minute*10L))
                    Thread.sleep(one_minute*9L); // one less to allow time for catching a greater resolution
                else
                    Thread.sleep(timeToSleep);
            } else
                loop = false;
        }
    }

    /**
     * Adjust time in Date to have the requested hour/min/sec.
     *
     * @param today DOCUMENT ME!
     * @param hms DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static public Date aujourdHuiA(Date today, Jour hms) {
        return aujourdHuiA(today, hms, true);
    }

    /**
     * Adjust time in Date to have the requested hour/min/sec.  Allow selection of
     * whether or not date will be bumped automatically on following days.
     *
     * @param today DOCUMENT ME!
     * @param hms DOCUMENT ME!
     * @param bump DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static public Date aujourdHuiA(Date today, Jour hms, boolean bump) {
        Date d = null;

        // We need to get a fix on what time of day it is, and when to jump into the schedule.
        cal().setTime(today);
        cal().set(Calendar.AM_PM, Calendar.AM);
        cal().set(Calendar.HOUR, hms.getHours());
        cal().set(Calendar.MINUTE, hms.getMinutes());
        cal().set(Calendar.SECOND, hms.getSeconds());
        cal().set(Calendar.MILLISECOND, 0);
        d = new Temps(cal().getTime());

        if (bump)
            addTempsToBump((Temps) d);

        return d;
    }

    /**
     * Adjust time in Date to have the requested hour/min/sec for TOMORROW. Sautez plue
     * de Samedi et Dimanche.
     *
     * @param today DOCUMENT ME!
     * @param hms DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static public Date demain(Date today, Jour hms) {
        return demain(today, hms, true);
    }

    /**
     * Adjust time in Date to have the requested hour/min/sec for TOMORROW. Sautez plue
     * de Samedi et Dimanche.
     *
     * @param today DOCUMENT ME!
     * @param hms DOCUMENT ME!
     * @param bump DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static public Date demain(Date today, Jour hms, boolean bump) {
        // We call aujourdHuiA to set cal to today's time.
        aujourdHuiA(today, hms);

        Temps t = null;

        // We now add a day, checking for Saturday and Sunday.
        do {
            cal().add(Calendar.DAY_OF_MONTH, 1);
        } while (!isWorkday(cal()));

        t = new Temps(cal().getTime());

        if (bump)
            addTempsToBump(t);

        return t;
    }

    /**
     * Adjust time in Date to have the requested hour/min/sec for YESTERDAY.
     *
     * @param today DOCUMENT ME!
     * @param hms DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static public Date heir(Date today, Jour hms) {
        return heir(today, hms, true);
    }

    /**
     * Adjust time in Date to have the requested hour/min/sec for YESTERDAY.
     *
     * @param today DOCUMENT ME!
     * @param hms DOCUMENT ME!
     * @param bump DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static public Date heir(Date today, Jour hms, boolean bump) {
        // We call aujourdHuiA to set cal to today's time.
        aujourdHuiA(today, hms);

        Temps t = null;

        // We now subtract a day, checking for Saturday and Sunday.
        do {
            cal().add(Calendar.DAY_OF_MONTH, -1);
        } while (!isWorkday(cal()));

        t = new Temps(cal().getTime());

        if (bump)
            addTempsToBump(t);

        return t;
    }

    /**
     * Checks to see if current date is on a legal holiday as recognized by the financial
     * markets in the United States.
     * 
     * <p>
     * The holidays checked for are as follows: <code>New Years's Day Martin Luther
     * King's day President's Day Memorial Day Independence Day Labor Day Thanksgivings
     * Day Christmas Day </code>
     * </p>
     * 
     * <p>
     * Good Friday is NOT checked for yet, due to the complexity of that particular
     * holiday (it's based on a Catholic calendar). Later, that holiday will be
     * addressed.
     * </p>
     *
     * @param cal DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static public boolean isUSHoliday(final Calendar cal) {
        // Get the vital information we need to check holidays.
        int month = cal.get(Calendar.MONTH); // first month (JANUARY) is 0
        int date = cal.get(Calendar.DAY_OF_MONTH); // first day is 1
        int week = cal.get(Calendar.WEEK_OF_MONTH); // first week is 1
        int wday = cal.get(Calendar.DAY_OF_WEEK); // first day (SUNDAY) is 1
        int fdofm = (((wday-1)-((date-1)%7)+7)%7)+1; // first day of month
        int nthday = (wday >= fdofm) ? week : (week-1); // nth weekday in month

        // First, we check for holidays that are fixed to day of month.
        if ((month == Calendar.JANUARY) && (date == 1))
            return true; // New Year's

        if ((month == Calendar.JULY) && (date == 4))
            return true; // Independence Day

        if ((month == Calendar.DECEMBER) && (date == 25))
            return true; // Christmas Day

        // Now for the annoying holidays that fall on monday
        if (wday == Calendar.MONDAY) {
            if ((month == Calendar.JANUARY) && (nthday == 3))
                return true; // MLK

            if ((month == Calendar.FEBRUARY) && (nthday == 3))
                return true; // President's

            if ((month == Calendar.MAY)
                    && ((nthday == 5) || ((nthday == 4) && ((date+7) > 31))))
                return true; // Memorial 

            if ((month == Calendar.SEPTEMBER) && (nthday == 1))
                return true; // Labor
        }

        // And for the rest
        if ((month == Calendar.NOVEMBER) && (wday == Calendar.THURSDAY) && (nthday == 4))
            return true; // Thanksgiving

        // No holidays hit -- must be OK.
        return false;
    }

    static public boolean isWeekend(final Calendar cal) {
        return (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
               || (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
    }

    static public boolean isWorkday(final Calendar cal) {
        return !isUSHoliday(cal) && !isWeekend(cal);
    }
}



class ChronJob {
    Date time;
    Runnable job;

    ChronJob(Date t, Runnable j) {
        time = t;
        job = j;
    }
}
