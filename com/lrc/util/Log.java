package com.lrc.util;

import java.io.*;

import java.text.SimpleDateFormat;

import java.util.*;


/**
 * Logging class -- allows logging to file and / or class listeners.
 * 
 * <p>
 * We not append logs to files with same day status.
 * </p>
 */
public final class Log {
    private static boolean writeToFile = true;
    private static File globalroot = new File("./"); // Directory to write log file to.
    private final static transient Object lock = new Object();
    private final static transient List listeners = new LinkedList();
    static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss,");
    private boolean timeStampIt = false;
    private boolean keepFileOpen = false;
    private String name = "default.log"; // log file name to write to.
    private File root = globalroot;
    private RandomAccessFile raf = null;

    public Log(String pathname) {
        name = pathname;
    }

    public Log() {}

    public static void setWriteToFile(boolean t) {
        writeToFile = t;
    }

    public static boolean isWriteToFile() {
        return writeToFile;
    }

    public boolean isTimeStampMode() {
        return timeStampIt;
    }

    public void setTimeStampMode(boolean t) {
        timeStampIt = t;
    }

    public boolean isKeepFileOpen() {
        return keepFileOpen;
    }

    public void setKeepFileOpen(boolean t) {
        keepFileOpen = t;
    }

    public static void setGlobalRoot(File r) {
        globalroot = r;
        System.out.println("Log: (global) root set to "+globalroot);
    }

    public static File getGlobalRoot() {
        return globalroot;
    }

    public void setRoot(File r) {
        root = r;
        System.out.println("Log: (local) root set to "+root);
    }

    public File getRoot() {
        return root;
    }

    public static void addLogListener(LogListener ll) {
        synchronized (lock) {
            listeners.add(ll);
        }
    }

    public static void removeLogListener(LogListener ll) {
        synchronized (lock) {
            listeners.remove(ll);
        }
    }

    protected static void fireLog(Object o) {
        synchronized (lock) {
            Iterator i = listeners.iterator();

            while (i.hasNext())
                ((LogListener) i.next()).logging(o);
        }
    }

    public void writeBuffer(byte[] buf, int off, int bytes) {
        log(new String(buf, off, bytes));
    }

    public void log(Object o) {
        fireLog(o);

        File file = new File(root, name);

        if (file != null)
            synchronized (lock) {
                //RandomAccessFile raf = null;
                try {
                    if (isWriteToFile()) {
                        if (raf == null) {
                            if (!file.exists())
                                file.createNewFile();

                            raf = new RandomAccessFile(file, "rw");
                            raf.seek(raf.length());
                        }

                        if (timeStampIt)
                            raf.writeBytes(sdf.format(new Temps()));

                        raf.writeBytes(o.toString()+"\n");

                        //raf.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if ((raf != null) && !keepFileOpen)
                            raf.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }

                    if (!keepFileOpen)
                        raf = null;
                }
            }
    }

    public static void main(String[] av) {
        Log foonts = new Log("foo.nts.log"); // no time stamp
        Log foots = new Log("foo.ts.log"); // time stamp
        foonts.setTimeStampMode(false);
        foots.setTimeStampMode(true);
        foots.setKeepFileOpen(true);

        for (int i = 0; i < 50; ++i) {
            foonts.log("Thls little bird #"+i+" with no time");
            foots.log("Thls little bird #"+i+" has all the time");
        }
    }
}
