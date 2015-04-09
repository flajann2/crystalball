package com.lrc.soap;

import java.awt.*;

import java.util.*;


/**
 * Generic Bubble Class
 */
public class GenericBubble extends Canvas implements Bubble {
    Class[] children = new Class[12];
    Laba laba = null;
    private Color[] color = { Color.red, Color.green, Color.blue, Color.orange };

    public void init(Laba laba, Object reference) {
        this.laba = laba;

        for (int i = 0; i < (children.length-laba.getTuple()); ++i)
            children[i] = getClass();
    }

    public void dispose() {
        children = null; // just to be sure this class is disposed!
    }

    public Object[] queryChildren() {
        return children;
    }

    public int[] queryChildGroups() {
        return null;
    }

    public void processBubbleEvent(AWTEvent e) {
        processEvent(e);
    }

    /**
     * paint a simple filled circle based on our size
     *
     * @param g DOCUMENT ME!
     * @param r DOCUMENT ME!
     */
    public void paint(Graphics g, Rectangle r) {
        //System.out.println("paint(g, "+r+") of " + this.getClass().getName() + " called");
        g.setColor(color[laba.getTuple()%color.length]);
        g.fillOval(r.x+1, r.y+1, r.width-1, r.height-1);
        g.setColor(Color.black);
        g.drawOval(r.x, r.y, r.width, r.height);

        String str = "B:"+laba;
        int w = g.getFontMetrics().stringWidth(str);

        if (w <= r.width) {
            int x = (r.x+(r.width/2))-(w/2);
            int y = r.y+(r.height/2);
            g.drawString(str, x, y);
        }
    }

    public void update(Graphics g) {
        // don't call super.update()! We're not attached to anything! 
        paint(g);
    }
}
