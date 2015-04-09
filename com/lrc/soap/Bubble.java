package com.lrc.soap;

import com.lrc.util.*;

import java.awt.*;

import java.util.*;


/**
 * Classes that implement Bubble are instantiated by SoapBubbles. Your class is then
 * passed a reference object that you may use to instantiate yourself appropriately.
 * 
 * <p>
 * Classes that implement the Bubble interface must be derived from java.awt.Component or
 * java.awt.Container so that it can be used  as a lightweight component in SoapBubble's
 * Panel.
 * </p>
 * 
 * <p></p>
 *
 * @see java.awt.Container
 * @see java.awt.Component
 */
public interface Bubble {
    /**
     * Initialize this bubble (establish identity).
     * 
     * <p>
     * The gui aspect of this object is not gaurenteed at this time. All gui-related
     * determinations should be made only during paint()/update() times.
     * </p>
     * 
     * <p></p>
     *
     * @param laba Logical Abstract Bubble Address
     * @param reference special reference object.
     */
    void init(Laba laba, Object reference);

    /**
     * Shut down yourself (go away). Will normally be used to flush out unused
     * (non-visible or off-screen) objects.
     * 
     * <p>
     * It is possible that in the future, this will be replaced by a serialization
     * mechanism.
     * </p>
     * 
     * <p></p>
     */
    void dispose();

    public void setBounds(Rectangle r); // do not implement -- derived in Component of implementor

    public Rectangle getBounds(); // do not implement -- derived in Component of implementor

    /**
     * Get children classes we need to instantiate.
     * 
     * <p>
     * Please keep like classes/objects adjacent so that we SoapBubbles can group them,
     * if need be. Classes are checked for object equivalence ( == ) so use the same
     * instance of Class (the Class object, not the actual instantiation of the object
     * that Class referrs to!) for the same class.
     * </p>
     * 
     * <p></p>
     *
     * @return An array of Bubble interfaces or Class objects is returned. Class objects
     *         must be classes that implements the Bubble interface.
     */
    Object[] queryChildren();

    /**
     * Get indices of where each distinct group begins.
     * 
     * <p></p>
     *
     * @return an array of indices to where each logical grouping begins in the array
     *         returned by queryChildren(). Can return null, in which case SoapBubbles
     *         will do its own grouping.
     */
    int[] queryChildGroups();

    /**
     * The paint method.
     */
    public void paint(Graphics g, Rectangle r);

    /**
     * The update method, as normally implemented by Components.
     */
    public void update(Graphics g);

    /**
     * Events to be handed off to the bubble for processing
     */
    public void processBubbleEvent(AWTEvent e);
}
