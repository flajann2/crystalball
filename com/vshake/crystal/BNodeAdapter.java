/*
 * Created on Aug 12, 2003
 */
package com.vshake.crystal;

import java.util.*;
import java.awt.*;
import java.awt.image.*;

/**
 * Convient class to implement BNode objects from.
 * @author fred
 *
 */
public class BNodeAdapter implements BNode {
    protected String caption;
    protected String description;
    protected BNode reference = null;
    protected Collection neighbors = null;
    protected Collection linkTypes = null;

    /**
 * @see com.vshake.crystal.BNode#getNeighbors(com.vshake.crystal.LinkType)
 */
    public Collection getNeighbors(LinkType lt) {
        if (neighbors == null) {
            neighbors = new HashSet();
        }

        return neighbors;
    }

    /**
     * @see com.vshake.crystal.BNode#getLinkTypes()
     */
    public Collection getLinkTypes() {
        if (linkTypes == null) {
            linkTypes = new HashSet();
        }

        return linkTypes;
    }

    private BufferedImage rep = null;
    /** Creates a basic representation based on the caption.<b>
     * Override to create something more definitive.<b>
 	 * @see com.vshake.crystal.BNode#getRepresentation()
 	 */
    public Object getRepresentation() {
        // TODO Return some default Shape to use here!
        if (rep == null) {
            rep = new BufferedImage(60, 20,  BufferedImage.TYPE_INT_RGB);
            Graphics2D g = rep.createGraphics();
            g.setColor(Color.WHITE);
            g.fill3DRect(0, 0, 60, 20, true);
            g.setColor(Color.BLACK);
            g.drawString(caption, 3, 12);
            g.dispose();
        }
        return rep;
    }

    /**
 * @see com.vshake.crystal.BNode#getCaption()
 */
    public String getCaption() {
        return caption;
    }

    /**
 * @see com.vshake.crystal.BNode#getDescription()
 */
    public String getDescription() {
        return description;
    }

    /**
 	* Adds a neighbor to the collection of neighbors.<p>
 	* If the neighbor is derived from BNodeAdapter, will add a backreference
 	* to that node.<p>
 	* 
 	* @param bn
 	*/
    public void addNeighbor(BNode bn) {
        if (neighbors == null) {
            neighbors = new HashSet();
        }

        if (bn instanceof BNodeAdapter) {
            BNodeAdapter bna = (BNodeAdapter) bn;

            if (bna.neighbors == null) {
                bna.neighbors = new HashSet();
            }

            bna.neighbors.add(this);
        }

        // We must now add this new one to ourselves, and ourselves to its list.
        neighbors.add(bn);
    }
    
	public String toString() {
		return caption;
	}

    
}
