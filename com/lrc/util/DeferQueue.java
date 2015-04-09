package com.lrc.util;

import java.util.*;


/**
 * defer queue class
 */
public class DeferQueue implements Runnable {
    private Thread batchThread = null;
    private List batchQueue = Collections.synchronizedList(new LinkedList());

    public DeferQueue(String name, int priority) {
        batchThread = new Thread(this, "DeferQueue: "+name);
        batchThread.setDaemon(true);
        batchThread.setPriority(priority);
        batchThread.start();
    }

    public DeferQueue(String name) {
        this(name, Thread.NORM_PRIORITY);
    }

    public DeferQueue() {
        this("<Anonymous>");
    }

    public void addDefer(Defer d) {
        synchronized (batchQueue) {
            batchQueue.add(d);
            batchQueue.notify();
        }
    }

    public void addDefer(Runnable r) {
        synchronized (batchQueue) {
            batchQueue.add(r);
            batchQueue.notify();
        }
    }

    /**
     * Batch processing
     */
    public void run() {
        List local = new LinkedList();

        while (true) {
            synchronized (batchQueue) {
                try {
                    if (batchQueue.isEmpty())
                        batchQueue.wait();

                    local.addAll(batchQueue);
                    batchQueue.clear();
                } catch (InterruptedException ie) {}
            }

            if (!local.isEmpty()) {
                Iterator it = local.iterator();

                while (it.hasNext()) {
                    try {
                        Object o = it.next();

                        if (o instanceof Defer)
                            ((Defer) o).exec();
                        else if (o instanceof Runnable)
                            ((Runnable) o).run();
                        else
                            System.out.println("DeferQueue error: "
                                               +o.getClass().getName()
                                               +" is not an instance of Runnable or Defer.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        it.remove();
                    }
                }
            } else

                break; // if we have nothing to process, then exit the thread.

            // just in case we have defers adding themselves back to the queue!
            if (!batchQueue.isEmpty())
                try {
                    Thread.sleep(200);
                } catch (InterruptedException iee2) {}
        }
    }
}
