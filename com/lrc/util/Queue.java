package com.lrc.util;

import java.util.*;


/**
 * This class is the basis of all queue and stack clasess. It allows you to add or remove
 * from the head or tail of the queue, thereby allowing you to implement FIFOs, LIFOs,
 * etc.
 * 
 * <p>
 * Queue is thread-safe, and you may have multiple threads adding and removing Queue
 * elements.
 * </p>
 * 
 * <p>
 * The Queue is implemented as follows:
 * </p>
 * 
 * <p>
 * <pre>
 *  .   push >--                     --< shove
 *  .           \                   /
 *  .            \                 /
 *  .     peek -: \.............../ :- look
 *  .             /               \
 *  .            /                 \
 *  .           /                   \
 *  .   pop  <--                     --> pull
 *  </pre>
 * </p>
 */
public class Queue {
    Node base = new Node(null);

    /**
     * push object on front of queue
     *
     * @param ob DOCUMENT ME!
     */
    public void push(Object ob) {
        base.addNext(new Node(ob));
    }

    /**
     * peek at object on front of queue
     *
     * @return DOCUMENT ME!
     */
    public Object peek() {
        return base.next.ob;
    }

    /**
     * pop object off the front of queue
     *
     * @return DOCUMENT ME!
     *
     * @throws QueueException DOCUMENT ME!
     */
    public Object pop() throws QueueException {
        return base.next.remove();
    }

    /**
     * shove object on back of queue
     *
     * @param ob DOCUMENT ME!
     */
    public void shove(Object ob) {
        base.addPrev(new Node(ob));
    }

    /**
     * look at object on back of queue
     *
     * @return DOCUMENT ME!
     */
    public Object look() {
        return base.prev.ob;
    }

    /**
     * pull object from the back of queue
     *
     * @return DOCUMENT ME!
     *
     * @throws QueueException DOCUMENT ME!
     */
    public Object pull() throws QueueException {
        return base.prev.remove();
    }

    public boolean isEmpty() {
        return base == base.next;
    }

    class Node {
        Node prev;
        Node next;
        final Object ob;

        {
            prev = next = this;
        }

        Node(Object o) {
            this.ob = o;
        }

        Object getObject() {
            return ob;
        }

        void addNext(Node n) // add new node after us.
         {
            synchronized (Queue.this) {
                n.prev = this;
                n.next = next;

                next.prev = n;
                next = n;
            }
        }

        void addPrev(Node n) // add new node before us.
         {
            synchronized (Queue.this) {
                n.next = this;
                n.prev = prev;

                prev.next = n;
                prev = n;
            }
        }

        Object remove() throws QueueException // Remove self
         {
            if (base != this)
                synchronized (Queue.this) {
                    next.prev = prev;
                    prev.next = next;
                    next = prev = this;
                }
            else
                throw new QueueException("Empty queue");

            return ob;
        }
    }
}
