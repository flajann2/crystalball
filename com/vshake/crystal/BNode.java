/*
 * Created on Aug 5, 2003
 *
 */
package com.vshake.crystal;

//import java.awt.Image;

import java.util.*;


/**
 * Node in a network.
 *
 * <p>
 * This represents a node in a network. The implementation need not keep all nodes in a
 * network in memory at all times. The implementor should make use of weak references so
 * that the JVM automatically cleans up portions of the network no longer being viewed.
 * </p>
 *
 * @author fred
 */
public interface BNode {
    /**
     * Get a collection of immediate neighbors.
     *
     * @return a collection of BNode neighbors.
     */
    public Collection getNeighbors(LinkType lt);

    public Collection getLinkTypes();

    /**
     * Get the graphical representation of this node. This allows the node to be
     * appropriately scaled and transformed.
     *
     * Note that if this returns null, the subsystem will try to call getImageRepresentation
     *
     * @return Shape or Image that is a representation of the node, or null. If any other type of object is returned, a CastClass excpetion will be thrown.
     *
     */
    public Object getRepresentation();

    /**
     * Get a short textual discription of node.
     *
     * @return DOCUMENT ME!
     */
    public String getCaption();

    /**
     * get a lengthy discription of node.
     *
     * @return DOCUMENT ME!
     */
    public String getDescription();
}
