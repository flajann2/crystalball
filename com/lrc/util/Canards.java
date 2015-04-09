/*
 * Canards.java
 *
 * Created on July 25, 2001, 6:07 PM
 */
package com.lrc.util;

import java.util.*;


/**
 * The "Duck" threading class. Because executing some tasks may lead to deep problems
 * such as  deadly embrace, etc, and may not return, a dispatching thread could be
 * locked, thus halting all further dispatching.
 * 
 * <p>
 * Canards allows you to splice off the task to seperate threads automatically, in a
 * fashion that is more efficient than just creating seperate threads. Threads are
 * created as needed and held in a queue until needed again. When a task is completed,
 * the thread is recovered and sits in the queue until needed again. This way, creation
 * of threads is minimized and are only done on a as-needed basis.
 * </p>
 * 
 * <p>
 * Also, job requests are queued, with the next available threads grabbing the next entry
 * off of the queue. There is no limit to the number of queueable requests.
 * </p>
 * 
 * <p>
 * If a thread does not return from its task within 60 seconds, it is said to be "locked
 * up", and thus will not be returned to the availability queue. A throwable object may
 * then be tossed into the locked thread to trigger a stack dump that can be useful for
 * debugging.
 * </p>
 * 
 * <p>
 * Messages will be displayed on stdout and stderr if locked threads exceed the time
 * threshold. Also, if a thread tosses an exception, that exception is caught and
 * displayed too.
 * </p>
 * 
 * <p></p>
 *
 * @author Fred
 * @version
 */
public final class Canards extends java.lang.Object {
    public static boolean debug = false;
    private static Canards cest = new Canards();
    private static ThreadGroup group = new ThreadGroup("Canards_Threads");
    private static volatile int tcount = 0; // thread count/id 
    private static int maxthreads = 15;
    private static int threadcount = 0;
    private static boolean dump = false;
    LinkedList readyQueue = new LinkedList();
    LinkedList activeQueue = new LinkedList();
    LinkedList jobQueue = new LinkedList();
    LinkedList serialJobQueue = new LinkedList();
    ArrayList allQuacks = new ArrayList();
    public long maxage = 1000L*60L*2L; // Warning age at 2 minutes
    public long trashage = 1000L*60L*10L; // Interrupt at this age!!!!

    private Canards() {
        Thread t =
            new Thread(group,
                       new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException ie) {}

                            if (debug)
                                System.out.println("Canard dump: "+stat());

                            Object[] oar = allQuacks.toArray();

                            for (int i = 0; i < oar.length; ++i) {
                                Quack q = (Quack) oar[i];
                                Date stamp = q.stamp;

                                if (stamp != null) {
                                    Date now = new Temps();
                                    long age = now.getTime()-stamp.getTime();

                                    if (debug && (age > maxage))
                                        System.err.println("Canard warning: "+q.getName()
                                                           +" is "+(age/1000L)
                                                           +" seconds old.");

                                    if (age > trashage) {
                                        System.err.println("Canard error: interrupting "
                                                           +q.getName());
                                        q.stop(new CanardsException("Tossed Exception"));
                                        q.stamp = null;
                                    }
                                }
                            }
                        }
                    }
                }, "Canards Master Thread");
        t.setDaemon(true);
        t.start();
    }

    public static void setMaxThreads(int max) {
        maxthreads = max;
    }

    public static int getMaxThreads() {
        return maxthreads;
    }

    public static void setDebugging(boolean t) {
        debug = t;

        if (!t)
            dump = false;
    }

    public static boolean isDebugging() {
        return debug;
    }

    public static void setDumping(boolean t) {
        dump = t;

        if (t)
            debug = true;
    }

    public static boolean isDumping() {
        return dump;
    }

    public static int getReadyCount() {
        return cest.readyQueue.size();
    }

    public static int getActiveCount() {
        return cest.activeQueue.size();
    }

    public static int getJobCount() {
        return cest.jobQueue.size();
    }

    String stat() {
        return "ready="+getReadyCount()+", active="+getActiveCount()+", jobs="
               +getJobCount();
    }

    public static void run(Runnable r) {
        cest.runJob(r, false);
    }

    public static void runSerial(Runnable r) {
        cest.runJob(r, true);
    }

    private void runJob(Runnable r, boolean serialjob) {
        Quack q = null;

        synchronized (readyQueue) {
            if (!readyQueue.isEmpty() && !serialjob) {
                q = (Quack) readyQueue.getFirst();

                if (q.r != null) // we have a problem. We must scan queue for next avail.
                 {
                    // Now we hit do something time-consuming.
                    // we scan the entire readyQueue for a free
                    // entry, if we find one.
                    q = null;

                    for (int i = 0; i < readyQueue.size(); ++i)
                        if (((Quack) readyQueue.get(i)).r == null) {
                            q = (Quack) readyQueue.get(i);

                            break;
                        }
                }
            }

            if (q == null) // we don't have a quack yet?
             {
                if (threadcount <= maxthreads)
                    q = new Quack();
                else if (!serialjob) // add job to job queue
                 {
                    synchronized (jobQueue) {
                        jobQueue.addFirst(r);
                        jobQueue.notifyAll();
                    }

                    r = null;
                }
            }

            if (serialjob && (r != null)) {
                synchronized (serialJobQueue) {
                    serialJobQueue.addFirst(r);
                    serialJobQueue.notifyAll();
                }

                r = null;
            }

            if ((r != null) && (q != null) && (q.r == null)) {
                q.r = r;
                r = null;
            }

            if (r != null) // if we still have a job on our hands, we're in trouble!
             {
                System.out.println("Canards: Can't get rid of job: "
                                   +r.getClass().getName());
                Thread.currentThread().dumpStack();
            }

            readyQueue.notifyAll();
        }
    }

    public static void setTimeout(long seconds) {
        cest.trashage = seconds*1000L;
        cest.maxage = (seconds*1000L)/2L;
    }

    class Quack extends Thread {
        Runnable r = null;
        boolean die = false;
        boolean serial = false;
        Date stamp = null;

        Quack() {
            super(group, "Quack-"+(++tcount));
            serial = tcount == 1; // only the first Quack gets the honor!

            setDaemon(true);
            allQuacks.add(this);
            start();

            if (debug)
                System.out.println("Canards: new Quack-"+tcount+" "+stat());

            ++threadcount;
        }

        public void run() {
            synchronized (readyQueue) {
                readyQueue.addLast(this);
            }

            while (!die) {
                synchronized (readyQueue) {
                    if (r == null)
                        try {
                            readyQueue.wait(); // we wait for a job!

                            if (r == null) // we still don't have a job?
                             {
                                if (serial)
                                    synchronized (serialJobQueue) {
                                        if (!serialJobQueue.isEmpty())
                                            r = (Runnable) serialJobQueue.removeLast();
                                    }

                                if (r == null)
                                    synchronized (jobQueue) {
                                        if (!jobQueue.isEmpty())
                                            r = (Runnable) jobQueue.removeLast();
                                    }
                            }

                            if (r != null) // if we have a job already...!?

                                readyQueue.remove(this); // then remove ourself from the ready list
                        } catch (InterruptedException ie) {}
                }

                if (r != null) // do we have a job?
                 {
                    synchronized (readyQueue) {
                        readyQueue.remove(this);
                    }

                    synchronized (activeQueue) {
                        activeQueue.addLast(this);
                    }

                    while (r != null) // do we really really have a job?
                     {
                        try {
                            stamp = new Temps();
                            r.run(); // execute our job already!
                        } catch (Throwable e) {
                            System.err.println("Canards: caught exception "+e);
                            e.printStackTrace();
                        } finally {
                            r = null; // job is done! get rid of reference!

                            if (serial)
                                synchronized (serialJobQueue) {
                                    if (!serialJobQueue.isEmpty()) // any more jobs for us?

                                        r = (Runnable) serialJobQueue.removeLast();
                                }

                            if (r == null)
                                synchronized (jobQueue) {
                                    if (!jobQueue.isEmpty()) // any more jobs for us?

                                        r = (Runnable) jobQueue.removeLast();
                                }
                        }
                    }

                    stamp = null;

                    if (r == null) // if we have no more jobs to do...
                     {
                        // add ourselves back to the ready queue!
                        synchronized (activeQueue) {
                            activeQueue.remove(this);
                        }

                        synchronized (readyQueue) {
                            readyQueue.addLast(this);
                        }
                    }

                    if (dump)
                        System.out.println("return of "+getName()+", "+stat());
                }
            }

            System.out.println("Canads Thread:"+Thread.currentThread()+" is dead.");
        }
    }
}



class CanardsException extends java.lang.Exception {
    CanardsException(String m) {
        super(m);
    }
}
