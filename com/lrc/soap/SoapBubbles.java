package com.lrc.soap;

import com.lrc.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.util.*;

import javax.swing.*;


/**
 * Creates 'fractal' 'bubble net' representations of objects and their heirarchial
 * relationships.
 * 
 * <p>
 * Child 'Bubble' objects are created on the fly, as needed for display. The parent
 * objects specifiy an array of Class types and number to represent its children, which
 * will be created and given an instantiation object with with to set itself up for.
 * </p>
 * 
 * <p>
 * Bubbles have an upper max of radial children (usually 8). If the parent bubble
 * requires more than this, SoapBubbles automatically creates intermediate group
 * objects, as necessary.
 * </p>
 * 
 * <p>
 * Users will be able to click on a bubble and automatically that bubble will move to the
 * center of view and zoom will occur. User will also be able to zoom out, as necessary.
 * </p>
 * 
 * <p>
 * Bubbles will be notified of user clicks and qureries, so that may react in a manner
 * they deem appropriate.
 * </p>
 * 
 * <p>
 * <b>Bubble Scaling</b> (Note that the following is not necesary to understand to USE
 * this component -- it is necessary to understand if modifications in the layout
 * behavior is desired.) -- Bubble scaling will be controlled by a simple self-avoiding
 * scaling shema. I will breifly mention the scaling equations here -- for greater
 * understanding, contact the Master.
 * </p>
 * 
 * <p>
 * Think of a given bubble. The distance from its center to its child's center is
 * <i>s</i>. The raidus of the bubble is denoted by <i>c</i>, and the scaling factor is
 * <i>r</i>.
 * </p>
 * 
 * <p>
 * As you go from parent to child, the child's radiur is <i>rc</i> and as you go from
 * child to the child's child, the distance seperating their centers is <i>rs</i>. For a
 * bubble to have equally-spaced <i>N</i> children, the scaling rate <i>r</i> is given
 * by:
 * <pre>
 *  .                        1
 *  .
 *  .        r = -------------------------------
 *  .
 *  .                              1
 *  .                1   +   --------------
 *  .                         sin (pi / N)
 *  </pre>
 * and the optimal radius c is given by (or can be no bigger than):
 * <pre>
 *  .                         s
 *  .                          n
 *  .
 *  .            c   <   -------------
 *  .             n
 *  .                        r + 1
 *  .
 *  .
 *  .            For n = 1, 2, 3, ... (child levels)
 *  </pre>
 * Staying within this framework, the bubbles are gaurenteed to be self-avoiding at all
 * possible scales.
 * </p>
 * 
 * <p>
 * For bubble peers at the top level (<i>n = 1</i>), the connection will have to be
 * extended beyond twice the Bubble Family Radius <i>L</i>:
 * <pre>
 *  .            (infinity)
 *  .              ______
 *  .              \                                 s
 *  .               \                n - 1            1
 *  .        L   =   >           s  r        =   -----------
 *  .               /             1
 *  .              /_____                           1 - r
 *  .               n = 1
 *  </pre>
 * For peering among children, it is suggested that the peers themselves be treated like
 * children anyway, so as to not cause overlap problems.
 * </p>
 * 
 * <p>
 * Conversely, since these equations are based on carrying <i>n</i> to infinity, there is
 * actually more 'room' around children in the real sense, which might be exploited.
 * </p>
 * 
 * <p>
 * <b>NOTES</b>
 * </p>
 * 
 * <p>
 * The root soap bubble is taken to be a radius of 1. All of the smaller bubbles will be
 * of smaller radii.
 * </p>
 * 
 * <p></p>
 *
 * @author Fred Mitchell, Cisco Systems.
 * @version 1.0
 *
 * @see Bubble
 * @see BubbleEvent
 */
public class SoapBubbles extends JComponent implements Runnable, java.io.Serializable {
    static private long threadCount;

    //// Management
    Object viewLock = new Object(); // our own internal synchronization for views
    AnimData animLock = new AnimData(); // animation lock/sync + data

    //// Data
    final int spokes = 7; // number of spokes per bubble (note that one spoke is for parent)
    final int visible_children = 2; // number of levels o visible children on mainline bubbles
    double r; // scaling factor - calculated by readySoap()
    double s; // size spoke length of root soap - calculated by readySoap()
    final double s_scale = 1.15; // used by readySoap() to compute s    
    Circle c = new Circle(); // radius and location of root circle
    Angle ang = new Angle(); // Angle of orientation of root circle 
    FRect focus; // current focus rectangle
    FRect newFocus; // newly-selected focus
    Object reference = null; // Reference object that is passed ot bubbles
    Class root = null; // Startup or root bubble

    /** Top-level soap bubble */
    Soap rootSoap = null;

    /** Soap that has the caret (has focus, or is in the center of screen) */
    Soap caretSoap = null;

    /**
     * for JavaBeans
     */
    public SoapBubbles() {
        setLayout(new BubbleLayout());
        add(new View(), "SE");
        add(new View(), "Main");

        Thread t = new Thread(this, "SoapBubbles-" + threadCount);
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    /**
     * SoapBubbles needs the appropriate starter class, and a reference to pass to then
     * upon initilazation.
     *
     * @param reference Reference object that will be passed to all subsequent bubbles.
     * @param starter Root bubble class from which all other bubbles stream.
     *
     * @throws InstantiationException DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     */
    public SoapBubbles(Object reference, Class starter)
                throws InstantiationException, IllegalAccessException {
        this();
        setReference(reference);
        setStarter(starter);
        readySoap();
    }

    /**
     * Set the reference object that will be passed to all Bubble instances.
     *
     * @param reference DOCUMENT ME!
     */
    public void setReference(Object reference) {
        this.reference = reference;
    }

    /**
     * Set the root Bubble class
     *
     * @param starter DOCUMENT ME!
     */
    public void setStarter(Class starter) {
        this.root = starter;
    }

    /**
     * Animation thread.
     */
    public void run() {
        synchronized (animLock) {
            for (;;) {
                try {
                    animLock.wait();
                } catch (Exception e) {}

                doAnimation();
            }
        }
    }

    private void doAnimation() {
        synchronized (viewLock) {
            for (double t = 0.1; t <= 1.0; t += 0.1) {
                FRect ir = animLock.oldr.interpolate(animLock.newr, t);
                animLock.v.setUcs(ir);

                //((View.ViewLayout) animLock.v.getLayout()).layoutView(animLock.v);
                animLock.v.setBubblePhysicalCoords();
                animLock.v.paint(animLock.g);

                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
        }

        // finalize
        caretSoap = animLock.newSoap;

        try {
            layoutBubbles();
        } catch (Exception x) {}

        validate();
        animLock.clear();
        animLock.notify();
    }

    /**
     * Checks to make sure all necessary setup is in place for the layout. May also do
     * some Soap- pruning, as necessary.
     *
     * @throws InstantiationException DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     */
    void readySoap() throws InstantiationException, IllegalAccessException {
        // Set up initial parameters.
        r = 1.0/(1.0+(1.0/Math.sin(Math.PI/spokes)));
        s = c.r*(r+1.0)*s_scale; // s > c(r+1), make s s_scale greater (typicaly around 1.15)

        // check to make sure the caret is set
        if (caretSoap == null) {
            if (rootSoap == null) // do we need to establish the root Soap object?

                rootSoap = new Soap((Bubble) root.newInstance());

            caretSoap = rootSoap;
            rootSoap.setAngle(ang);
            rootSoap.setCircle(c);
            rootSoap.setSpokeLength(s);
        }

        // More to come, as needed.
    }

    /**
     * enumerate all the soaps in the system.
     *
     * @return DOCUMENT ME!
     */
    public Enumeration soaps() {
        return new Enumeration() {
                class Ts {
                    Soap s = null;
                    int i = 1;

                    Ts(Soap s) {
                        this.s = s;
                    }
                }

                com.lrc.util.Queue q;

                {
                    q = new com.lrc.util.Queue();
                    q.push(new Ts(rootSoap));
                }

                public boolean hasMoreElements() {
                    return !q.isEmpty();
                }

                public Object nextElement() {
                    try {
                        while (hasMoreElements()) {
                            Ts ts = (Ts) q.peek();

                            if (ts.i > ts.s.getSoaps()) // no more children to check!
                             {
                                q.pop();

                                if (hasMoreElements())
                                    ((Ts) q.peek()).i++;

                                return ts.s;
                            } else if (ts.s.isSoap(ts.i)) // children to look at!

                                q.push(new Ts(ts.s.getSoap(ts.i)));
                            else // childless entry in array
                             {
                                ts.i++;
                            }
                        }
                    } catch (Exception e) {}

                    return null; // should never get here!
                }
            };
    }

    /**
     * Layout engine for bubbles.
     * 
     * <p>
     * Called by the BubbleLayout manager, which is tightly coupled to SoapBubbles.
     * </p>
     * 
     * <p>
     * <code> caretSoap </code> is expected to be set to the appropriate Soap that now
     * "has the focus". If null, will be automatically set to the root Soap.
     * </p>
     * 
     * <p></p>
     */
    void layoutBubbles() {
        try {
            readySoap();
            lb_blowBubbles();
            lb_setViewOfBubbles();
            lb_pruneBubbles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    View getMainView() {
        return ((BubbleLayout) getLayout()).getView("Main");
    }

    /**
     * Create and/or position bubbles from the root down to the caret.
     * 
     * <p>
     * Note that main-line bubbles will always have at least <i>visible_children</i>
     * level of children 'visible'.
     * </p>
     * 
     * <p></p>
     *
     * @throws InstantiationException DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     * @throws QueueException DOCUMENT ME!
     */
    void lb_blowBubbles()
                 throws InstantiationException, IllegalAccessException, QueueException {
        // We start by making a list of mainline bubbles. We do this
        // by starting at the caret and working our way back to the root.
        com.lrc.util.Queue q = new com.lrc.util.Queue();
        Vector mainline = new Vector();

        for (Soap p = caretSoap; p != null; p = p.getParent())
            q.shove(p); // pull() these back out for LIFO operation.

        while (!q.isEmpty())
            mainline.addElement(q.pull());

        // All children are now instantiated. Perform actual 'fractal layout'
        // calculations on this constellation of children down the mainline.
        rootSoap.setCircle(c);
        rootSoap.setAngle(ang);
        rootSoap.setSpokeLength(s);

        // Calculate all children of the mainline
        for (Enumeration e = mainline.elements(); e.hasMoreElements();)
            ((Soap) e.nextElement()).layoutChildren(visible_children-1);
    }

    /**
     * set our proper coodinate view system of the bubbles.
     */
    void lb_setViewOfBubbles() {
        Component[] cview = getComponents(); // every component should be a view.

        for (int i = 0; i < cview.length; ++i) {
            View v = (View) cview[i];
            v.removeAll();

            for (Enumeration e = soaps(); e.hasMoreElements();) {
                Soap s = (Soap) e.nextElement();

                if (s.getBubble() != null)
                    v.add(s.createProxyBubble());
            }
        }
    }

    /**
     * Get rid of extraneous children.
     *
     * @throws InstantiationException DOCUMENT ME!
     * @throws IllegalAccessException DOCUMENT ME!
     */
    void lb_pruneBubbles() throws InstantiationException, IllegalAccessException {
        int exclude = 0;

        for (Soap p = caretSoap; p != null; p = p.getParent()) {
            p.pruneChildren(visible_children, exclude);
            exclude = p.getLaba().getIndex();
        }
    }

    /**
     * Create animation to newSoap, then set newSoap as the caret. Note that newSoap must
     * be in the hierarchy.
     * 
     * <p>
     * At this time, we only animate the main view.
     * </p>
     * 
     * <p></p>
     *
     * @param newSoap DOCUMENT ME!
     */
    void animateToNewSoap(Soap newSoap) {
        // set up animation
        synchronized (animLock) {
            animLock.v = getMainView();
            animLock.newr = animLock.v.calcUcs(newSoap);
            animLock.oldr = animLock.v.getUcs();
            animLock.g = getGraphics();
            animLock.newSoap = newSoap;

            //animLock.notify();
            doAnimation();
        }
    }

    ////
    class AnimData {
        View v;
        FRect newr;
        FRect oldr;
        Graphics g;
        Soap newSoap;

        void clear() {
            newSoap = null;
            g = null;
            v = null;
        }
    }


    //// Inner Classes

    /**
     * This embodies SoapBubbles' view of the individual Bubble.
     */
    class Soap implements java.io.Serializable {
        //int n; // level (first is 1) get this from laba.size()
        double s; // spoke length
        Circle circle = null; // position and radius
        Angle angle = null; // Angle of the position 0
        boolean areWeAGroup = false;
        boolean initCalled = false;
        boolean disposeCalled = false;

        /**
         * Position 0 is reserved for a parent reference.
         * 
         * <p>
         * If at top level (level 0), the the array MAY be one greater than normal.
         * </p>
         */
        Soap[] children = new Soap[spokes+1];
        Bubble bubble;
        Laba laba; // address of this bubble
        Image icon;
        public View lastView = null; // bookeeping - last view to resize us.

        /**
         * Root level soap
         *
         * @param b DOCUMENT ME!
         */
        Soap(Bubble b) {
            laba = new Laba(1);
            bubble = b;
            laba.setIndex(1, 1);
            bubble.init(laba, reference);
        }

        /**
         * Create a child soap bubble.
         * 
         * <p></p>
         *
         * @param parent parent soap
         * @param b associated bubble
         *
         * @throws java.lang.NullPointerException DOCUMENT ME!
         */
        Soap(Soap parent, Bubble b) {
            //this(b, p.getLaba().getTuple() + 1);
            children[0] = parent;

            if ((bubble = b) == null)
                throw new java.lang.NullPointerException();

            int i;

            for (i = 1; i < parent.children.length; ++i)
                if (parent.children[i] == null) {
                    parent.children[i] = this;

                    break;
                }

            laba = new Laba(parent.laba, i);
        }

        /**
         * free up resources, this Soap is being discarded.
         */
        void dispose() {
            for (int i = 1; i < children.length; ++i)
                if (children[i] != null) {
                    children[i].dispose();
                    children[i] = null;
                }

            bubble.dispose();
            children = null;
        }

        public Laba getLaba() {
            return laba;
        }

        /** */
        public void setSpokeLength(double s) {
            this.s = s;
        }

        public double getSpokeLength() {
            return s;
        }

        /** */
        public void setCircle(Circle pos) {
            circle = pos;
        }

        public Circle getCircle() {
            return circle;
        }

        /** */
        public void setAngle(Angle ang) {
            angle = ang;
        }

        public Angle getAngle() {
            return angle;
        }

        /**
         * get our associated bubble
         *
         * @return DOCUMENT ME!
         */
        public Bubble getBubble() {
            return bubble;
        }

        public ProxyBubble createProxyBubble() {
            return new ProxyBubble();
        }

        /**
         * get number of children (first child index starts at 1)
         *
         * @return DOCUMENT ME!
         */
        public int getSoaps() {
            return children.length-1;
        }

        /**
         * Is there a child Soap currently instantiated? Use this function to avoid
         * auto-instantiantion of child soaps.
         *
         * @param index DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public boolean isSoap(int index) {
            return children[index] != null;
        }

        /**
         * get child or parent Soap. Parent is at index 0.
         *
         * @param index DOCUMENT ME!
         *
         * @return Soap or null, if there is no child.
         *
         * @throws InstantiationException DOCUMENT ME!
         * @throws IllegalAccessException DOCUMENT ME!
         */
        public Soap getSoap(int index)
                     throws InstantiationException, IllegalAccessException {
            if ((children[index] == null) && (index > 0)) // 0 index refers back to the parent Soap. Can only be null at the root.
             {
                // create the soap!
                Object[] oar = bubble.queryChildren();
                Object o = (index < oar.length) ? oar[index] : null;
                Bubble b = null; // child bubble

                if (o instanceof Class) {
                    b = (Bubble) ((Class) o).newInstance();
                    b.init(new Laba(laba, index), reference);
                } else if (o != null)
                    b = (Bubble) o; // the actual bubble (will throw an exception here if wrong type!)

                if (b != null)
                    children[index] = new Soap(this, b);
            }

            return children[index];
        }

        /**
         * Get rid of children. Exclude child at exclude.
         *
         * @param level DOCUMENT ME!
         * @param exclude DOCUMENT ME!
         */
        public void pruneChildren(int level, int exclude) {
            for (int i = 1; i < children.length; ++i)
                if ((i != exclude) && (children[i] != null)) {
                    if (level == 0) {
                        children[i].dispose();
                        children[i] = null;
                    } else
                        children[i].pruneChildren(level-1);
                }
        }

        /**
         * Prune all children at given level (this == level 0)
         *
         * @param level DOCUMENT ME!
         */
        public void pruneChildren(int level) {
            pruneChildren(level, 0);
        }

        /**
         * Prune all children.
         */
        public void pruneChildren() {
            pruneChildren(0, 0);
        }

        /**
         * This method exists solely for convience.  If you need to override, override
         * getSoap() instead.
         *
         * @return DOCUMENT ME!
         *
         * @throws InstantiationException DOCUMENT ME!
         * @throws IllegalAccessException DOCUMENT ME!
         */
        final public Soap getParent()
                             throws InstantiationException, IllegalAccessException {
            return getSoap(0);
        }

        /**
         * public void paint(Graphics g) { Rectangle re = getBounds();
         * g.setColor(Color.black); g.drawOval(0, 0, re.width, re.height); } //
         *
         * @param recurse DOCUMENT ME!
         *
         * @throws InstantiationException DOCUMENT ME!
         * @throws IllegalAccessException DOCUMENT ME!
         */
        /**
         * LAYOUT of immediate children. Note that this does not cause the children to
         * layout its own children!!!
         * 
         * <p></p>
         *
         * @param recurse recursion factor, > 0 will make children draw children with
         *        recurse - 1<p>
         *
         * @throws InstantiationException DOCUMENT ME!
         * @throws IllegalAccessException DOCUMENT ME!
         */
        void layoutChildren(int recurse)
                     throws InstantiationException, IllegalAccessException {
            double a0 = angle.getRadian();
            double ar = (2.0*Math.PI)/children.length;

            for (int i = 1; i < children.length; ++i) {
                Angle ai = new Angle((ar*i)+a0);
                Soap child = getSoap(i);

                if (child != null) {
                    child.setAngle(new Angle(Math.PI+(ar*i)+a0));
                    child.setSpokeLength(r*s);
                    child.setCircle(new Circle(circle.x+(s*ai.x), circle.y+(s*ai.y),
                                               circle.r*r));

                    if (recurse > 0)
                        child.layoutChildren(recurse-1);
                }
            }
        }

        /**
         * Class to use in views that accesses Bubble
         */
        public class ProxyBubble extends Component implements Bubble {

            { // set up bubble to receive events!
                enableEvents(AWTEvent.ACTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK
                             | AWTEvent.ACTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK
                             | AWTEvent.FOCUS_EVENT_MASK | AWTEvent.TEXT_EVENT_MASK
                             | AWTEvent.ITEM_EVENT_MASK);
            }

            /**
             * Initialize this bubble (establish identity).
             * 
             * <p>
             * The gui aspect of this object is not gaurenteed at this time. All
             * gui-related determinations should be made only during paint()/update()
             * times.
             * </p>
             * 
             * <p></p>
             *
             * @param laba Logical Abstract Bubble Address
             * @param reference special reference object.
             */
            public void init(Laba laba, Object reference) {
                if (!initCalled)
                    bubble.init(laba, reference);

                initCalled = true;
            }

            public void dispose() {
                if (!disposeCalled)
                    bubble.dispose();

                disposeCalled = true;
            }

            public void setBounds(Rectangle r) {
                //System.out.println("setBounds(" +r+ ") for " + this);
                super.setBounds(r);
                bubble.setBounds(r); // should we be calling this???
            }

            public Object[] queryChildren() {
                return bubble.queryChildren();
            }

            public int[] queryChildGroups() {
                return bubble.queryChildGroups();
            }

            public void paint(Graphics g, Rectangle r) {
                System.out.println("paint(g, r) of "+this.getClass().getName()+" called");
                bubble.paint(g, r);
            }

            public void paint(Graphics g) {
                System.out.println("paint(g) of "+this.getClass().getName()+" called");
            }

            public void update(Graphics g) {
                bubble.update(g);
            }

            public void processBubbleEvent(AWTEvent e) {
                bubble.processBubbleEvent(e);
            }

            Soap getSoap() {
                return Soap.this;
            }

            protected void processEvent(AWTEvent e) {
                //System.out.println("processEvent " + e);
                super.processEvent(e);
                processBubbleEvent(e);
            }

            protected void processMouseEvent(MouseEvent e) {
                super.processMouseEvent(e);

                if (e.getID() == MouseEvent.MOUSE_PRESSED)
                    animateToNewSoap(getSoap());
            }
        }
    }


    /**
     * Layout Manager for SoapBubble. This lays out the views.
     * 
     * <p>
     * STRINGS:
     * <pre>
     *  Main    - central mapping
     *  NW      - View is a bird's eye - place it in the NW corner
     *  NE      - View is a bird's eye - place it in the NE corner
     *  SW      - View is a bird's eye - place it in the SW corner
     *  SE      - View is a bird's eye - place it in the SE corner
     *  </pre>
     * </p>
     */
    class BubbleLayout implements LayoutManager {
        Hashtable list = new Hashtable(); // note that the components are used as KEYS, the strings used as VALUES!!
        final int bird = 5; // bird's eye divisor

        public void addLayoutComponent(String name, Component comp) {
            list.put(comp, name); // comp is SUPPOSED to be the key!
        }

        /**
         * get named view
         *
         * @param name DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        View getView(String name) {
            for (Enumeration e = list.keys(); e.hasMoreElements();) {
                View v = (View) e.nextElement();

                if (name.compareTo((String) list.get(v)) == 0)
                    return v;
            }

            return null;
        }

        /**
         * Components added MUST implement the Bubble interface!  This will throw an
         * exception if Component is not right!
         *
         * @param comp DOCUMENT ME!
         */
        public void removeLayoutComponent(Component comp) {
            list.remove(comp);
        }

        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension(100, 50);
        }

        public Dimension preferredLayoutSize(Container parent) {
            return new Dimension(650, 250);
        }

        public void layoutContainer(Container parent) {
            try {
                for (Enumeration e = list.keys(); e.hasMoreElements();) {
                    View v = (View) e.nextElement();
                    String s = (String) list.get(v);
                    Dimension d = parent.getSize();

                    if ("Main".compareTo(s) == 0) {
                        v.setBounds(0, 0, d.width, d.height);
                        v.setUcs(caretSoap);
                    } else if ("NW".compareTo(s) == 0) {
                        v.setBounds(0, 0, d.width/bird, d.height/bird);
                        v.setUcs(caretSoap.getParent());
                    } else if ("NE".compareTo(s) == 0) {
                        v.setBounds(d.width-(d.width/bird), 0, d.width/bird, d.height/bird);
                        v.setUcs(caretSoap.getParent());
                    } else if ("SW".compareTo(s) == 0) {
                        v.setBounds(0, d.height-(d.height/bird), d.width/bird,
                                    d.height/bird);
                        v.setUcs(caretSoap.getParent());
                    } else if ("SE".compareTo(s) == 0) {
                        v.setBounds(d.width-(d.width/bird), d.height-(d.height/bird),
                                    d.width/bird, d.height/bird);
                        v.setUcs(caretSoap.getParent());
                    }
                }
            } catch (IllegalAccessException iae) {
                iae.printStackTrace();
            } catch (InstantiationException ie) {
                ie.printStackTrace();
            }
        }
    }


    /**
     * View component for the indicated class. The bubbles are laied out in Uiversal
     * Coordiate Space. You need Views on that coordinate space to map the object to a
     * viewable region.
     * 
     * <p>
     * There can be multiple views, and for the purposes of animation, begin and end
     * views will be created. Also, a 'bird's eye' view may be created which will allow
     * a bird's eye viewing area.
     * </p>
     * 
     * <p></p>
     */
    class View extends Container implements java.io.Serializable {
        FRect ucs = null; // Universal Coordinate Space region to be viewed.
        Image offScreen = null; // place to do double-buffering.

        View() {
            setLayout(new ViewLayout());
        }

        public void update(Graphics g) {
            super.update(g);

            //paint(g);
        }

        /**
         * note that this method must not be executed concurrently with any other view!
         *
         * @param g DOCUMENT ME!
         */
        public void paint(Graphics g) {
            //System.out.println("View paint for " + this);
            synchronized (viewLock) {
                if (needNewOffscreenBuffer())
                    offScreen = createImage(getSize().width, getSize().height);

                // Set up and clean buffer
                Graphics goff = offScreen.getGraphics();

                try {
                    if (goff instanceof Graphics2D)
                        ((Graphics2D) goff).setRenderingHint(RenderingHints.KEY_RENDERING,
                                                             RenderingHints.VALUE_RENDER_SPEED);
                } catch (NoClassDefFoundError e) {} // if running under 1.1, catch this error!

                goff.setClip(0, 0, getSize().width, getSize().height);
                goff.clearRect(0, 0, getSize().width, getSize().height);

                setBubblePhysicalCoords();
                renderBubbles(goff);
                g.drawImage(offScreen, 0, 0, Color.white, this);
            }
        }

        private boolean needNewOffscreenBuffer() {
            Dimension size = getSize();

            return ((offScreen == null) || (size.width != offScreen.getWidth(this))
                   || (size.height != offScreen.getHeight(this)));
        }

        void setBubblePhysicalCoords() {
            setBubblePhysicalCoords(rootSoap);
        }

        void renderBubbles(Graphics g) {
            renderBubbles(g, rootSoap);
        }

        /**
         * render this Bubble and all child Bubbles
         *
         * @param g DOCUMENT ME!
         * @param s DOCUMENT ME!
         */
        void renderBubbles(Graphics g, Soap s) {
            Bubble b;

            // render children first so spokes may be properly obscured.
            for (int i = 1; i <= s.getSoaps(); ++i)
                if (s.isSoap(i))
                    try {
                        renderBubbles(g, s.getSoap(i));
                    } catch (Exception e) {} // should never throw an exception!

            // now render ourself.
            if ((b = s.getBubble()) != null) {
                Rectangle r = b.getBounds();

                if (g.getClipBounds().intersects(r)) {
                    renderSpoke(g, r, s);
                    b.paint(g, r);
                }
            }
        }

        /**
         * Render the spoke of the child back to parent
         *
         * @param g DOCUMENT ME!
         * @param r DOCUMENT ME!
         * @param s DOCUMENT ME!
         */
        void renderSpoke(Graphics g, Rectangle r, Soap s) {
            try {
                Soap p; // parent

                if ((p = s.getParent()) != null) { // draw a line from the center of child rectangle to the center of the parent rectangle

                    Rectangle pr = p.getBubble().getBounds();
                    g.setColor(Color.black);
                    g.drawLine(r.x+(r.width/2), r.y+(r.height/2), pr.x+(pr.width/2),
                               pr.y+(pr.height/2));
                }
            } catch (Exception e) {}
        }

        /**
         * Bounding box for the Soap object for this View
         *
         * @param s DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        Rectangle computeBoundingBox(Soap s) {
            // The bounding box for the Soap object is taken to be the
            // square that encloses the bubble, that is, a square that
            // will be 2r on a side, and will have an upper corner at
            // (x-r, y-r).
            Circle c = s.getCircle();
            FPoint p = new FPoint();
            FRect box = new FRect();

            // translate central point of circle for this view.
            p.x = c.x-ucs.x1;
            p.y = c.y-ucs.y1;

            // create the box coordinates
            box.x1 = p.x-c.r;
            box.y1 = p.y-c.r;
            box.x2 = p.x+c.r;
            box.y2 = p.y+c.r;

            // Scale to view
            Rectangle rv = getBounds();
            Rectangle r = new Rectangle();
            r.x = (int) ((box.x1*rv.width)/(ucs.x2-ucs.x1));
            r.y = (int) ((box.y1*rv.height)/(ucs.y2-ucs.y1));
            r.width = (int) ((2.0*c.r*rv.width)/(ucs.x2-ucs.x1));
            r.height = (int) ((2.0*c.r*rv.height)/(ucs.y2-ucs.y1));

            return r;
        }

        /**
         * run through all Bubbles and set their physical coordinates
         *
         * @param s DOCUMENT ME!
         *
         * @deprecated
         */
        void setBubblePhysicalCoords(Soap s) {
            ((ViewLayout) getLayout()).layoutView(this);
        }

        /**
         * Set Universal Coordinate System based on given Soap.
         *
         * @param s DOCUMENT ME!
         */
        void setUcs(Soap s) {
            if (s == null)
                s = rootSoap;

            ucs = calcUcs(s);
        }

        void setUcs(FRect ur) {
            ucs = ur;
        }

        FRect getUcs() {
            return ucs;
        }

        /**
         * calculate Universal Coordinates based on given Soap.
         *
         * @param s DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        FRect calcUcs(Soap s) {
            FRect ucs = new FRect();

            // Currently, we set the width or height (whichever is smaller) to
            // be 2  sum( s.spokeLength() * r ** i, i = 0, visible_children )
            double length;
            double rr;
            int i;

            for (i = 0, rr = 1.0, length = 0.0; i <= (visible_children+1);
                     ++i, rr *= r)
                length += (s.getSpokeLength()*rr);

            //length *= 2.0;
            if (getSize().width < getSize().height) // width (x-axis) is the narrower one
             {
                ucs.x1 = s.getCircle().x-length;
                ucs.x2 = s.getCircle().x+length;

                double hlength =
                    (length*(double) getSize().height)/(double) getSize().width;
                ucs.y1 = s.getCircle().y-hlength;
                ucs.y2 = s.getCircle().y+hlength;
            } else // height (y-axis) is the narrower one (or equiv).
             {
                ucs.y1 = s.getCircle().y-length;
                ucs.y2 = s.getCircle().y+length;

                double wlength =
                    (length*(double) getSize().width)/(double) getSize().height;
                ucs.x1 = s.getCircle().x-wlength;
                ucs.x2 = s.getCircle().x+wlength;
            }

            return ucs;
        }

        /**
         * Given various Floating-based coords, return with the actual coordinates to be
         * passed on to Graphics.
         *
         * @see Soap
         */
        protected Rectangle getPhysical(Circle cir) {
            Rectangle re = new Rectangle();
            Rectangle b = getBounds();

            // first, convert the circle into rectangle coords.
            double x1 = cir.x-cir.r;
            double x2 = cir.x+cir.r;
            double y1 = cir.y-cir.r;
            double y2 = cir.y+cir.r;

            // next, do the conversion
            re.x = (int) ((((x1-focus.x1)*b.width)/(focus.x2-focus.x1))+b.x);
            re.y = (int) ((((y1-focus.y1)*b.height)/(focus.y2-focus.y1))+b.y);
            re.width = (int) (((x2-x1)*b.width)/(focus.x2-focus.x1));
            re.height = (int) (((y2-y1)*b.height)/(focus.y2-focus.y1));

            return re;
        }

        protected Point getPhysical(FPoint fp) {
            Point p = new Point();
            Rectangle b = getBounds();

            p.x = (int) ((((fp.x-focus.x1)*b.width)/(focus.x2-focus.x1))+b.x);
            p.y = (int) ((((fp.y-focus.y1)*b.height)/(focus.y2-focus.y1))+b.y);

            return p;
        }

        /**
         * Layout Manager for SoapBubble. We need to be able to get at SoapBubble's
         * parameters. That's why we must be an inner class.
         */
        class ViewLayout implements LayoutManager {
            /**
             * Components added MUST implement the Bubble interface!  This will throw an
             * exception if Component is not right!
             *
             * @param name DOCUMENT ME!
             * @param comp DOCUMENT ME!
             */
            public void addLayoutComponent(String name, Component comp) {
                Bubble b = (Bubble) comp;
            }

            /**
             * Components added MUST implement the Bubble interface!  This will throw an
             * exception if Component is not right!
             *
             * @param comp DOCUMENT ME!
             */
            public void removeLayoutComponent(Component comp) {
                Bubble b = (Bubble) comp;
            }

            public Dimension minimumLayoutSize(Container parent) {
                return new Dimension(100, 50);
            }

            public Dimension preferredLayoutSize(Container parent) {
                return new Dimension(650, 250);
            }

            public void layoutContainer(Container parent) {
                layoutBubbles();
                layoutView((View) parent);
            }

            void layoutView(View v) {
                synchronized (viewLock) {
                    Component[] proxy = v.getComponents();

                    for (int i = 0; i < proxy.length; ++i) {
                        Rectangle r;
                        Soap.ProxyBubble pb = (Soap.ProxyBubble) proxy[i];
                        pb.setBounds(r = v.computeBoundingBox(pb.getSoap()));
                    }
                }
            }
        }
    }
}
