/*
 * Debug.java
 *
 * Created on September 26, 2002, 6:57 AM
 */
package com.lrc.util;

import java.lang.ref.*;

import java.text.*;

import java.util.*;


/**
 * Control class for embedded debugging in applications.
 * 
 * <p>
 * Allows global control over the information being dumped to standard out for logging
 * and debugging purposes.
 * </p>
 * 
 * <p>
 * Debugging can be switched on and off globally or at the class level. Class-level
 * control allows superclass recoginition.
 * </p>
 *
 * @author fred
 */
public final class Debug {
    /** list of weak references to Debug instances */
    private static final List bugs = Collections.synchronizedList(new LinkedList());
    private static final String sdf_timeFormat = "HH:mm:ss- ";
    private boolean debug = false;
    private boolean verbose = false;
    private boolean inform = true;
    private Class d_class = null;
    private Reference d_instance = null;

    /**
     * Creates a new instance of Debug
     */
    public Debug() {
        bugs.add(new WeakReference(this));
    }

    public Debug(boolean debug, boolean vebose) {
        this();
        this.debug = debug;
        this.verbose = verbose;
    }

    public Debug(boolean debug, boolean verbose, Object cest) {
        this(debug, verbose);

        if (cest instanceof Class)
            d_class = (Class) cest;
        else {
            d_class = cest.getClass();
            d_instance = new WeakReference(cest);
        }
    }

    public Debug(Object cest) {
        this(false, false, cest);
    }

    private void print(Object mess) {
        DateFormat df = new SimpleDateFormat(sdf_timeFormat);
        System.out.println(df.format(new Date())+mess);
    }

    public void printd(Object mess) {
        if (debug)
            print(mess);
    }

    public void printv(Object mess) {
        if (verbose)
            print(mess);
    }

    public void printi(Object mess) {
        if (inform)
            print(mess);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public void setInform(boolean inform) {
        this.inform = inform;
    }

    /**
     * get all of the current Debug instances
     *
     * @return DOCUMENT ME!
     */
    static private Debug[] bugs() {
        ArrayList al = new ArrayList(bugs.size());
        Object[] oar = bugs.toArray();

        for (int i = 0; i < oar.length; ++i) {
            Debug d = (Debug) ((Reference) oar[i]).get();

            if (d != null)
                al.add(d);
        }

        Debug[] dal = new Debug[al.size()];

        return (Debug[]) al.toArray(dal);
    }

    /**
     * Global debug -- really global if cls is null.
     *
     * @param debug DOCUMENT ME!
     * @param cls DOCUMENT ME!
     */
    static public void setDebug(boolean debug, Class cls) {
        Debug[] bugs = bugs();

        for (int i = 0; i < bugs.length; ++i) {
            if ((cls == null) || cls.isAssignableFrom(bugs[i].d_class))
                bugs[i].setDebug(debug);
        }
    }

    /**
     * Global verbose -- really global if cls is null.
     *
     * @param verbose DOCUMENT ME!
     * @param cls DOCUMENT ME!
     */
    static public void setVerbose(boolean verbose, Class cls) {
        Debug[] bugs = bugs();

        for (int i = 0; i < bugs.length; ++i) {
            if ((cls == null) || cls.isAssignableFrom(bugs[i].d_class))
                bugs[i].setVerbose(verbose);
        }
    }

    /**
     * Global inform -- really global if cls is null.
     *
     * @param inform DOCUMENT ME!
     * @param cls DOCUMENT ME!
     */
    static public void setInform(boolean inform, Class cls) {
        Debug[] bugs = bugs();

        for (int i = 0; i < bugs.length; ++i) {
            if ((cls == null) || cls.isAssignableFrom(bugs[i].d_class))
                bugs[i].setInform(inform);
        }
    }
}
