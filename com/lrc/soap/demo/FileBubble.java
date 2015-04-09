package com.lrc.soap.demo;

import com.lrc.soap.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.*;

import java.util.*;

import javax.swing.*;


/**
 * File Bubble Demo
 * 
 * <p>
 * Will read from the "root" of the file heirarchy and display directory tree. In a side
 * bar, displays files in the current directory.
 * </p>
 * 
 * <p>
 * Each FileBubble object represents a directory. The name is a bit of a misnomer.
 * </p>
 * 
 * <p>
 * Laba mapping: first object maps to Laba[1]. Children are directories ONLY.
 * </p>
 */
public class FileBubble extends JComponent implements Bubble {
    static File rootdir = new File("/");
    static final FileFilter dirFilter =
        new FileFilter() {
            public boolean accept(File pn) {
                return pn.isDirectory();
            }
        };

    File dir = null;
    File[] dirChildren = null;
    FileBubble[] children = null;
    Laba laba = null;
    private Color[] color = { Color.red, Color.green, Color.blue, Color.orange };

    public FileBubble() {
        this.dir = rootdir;
    }

    FileBubble(File dir) {
        this.dir = dir;
    }

    public void init(Laba laba, Object reference) {
        this.laba = laba;
    }

    public void dispose() {
        children = null; // just to be sure this class is disposed!
        dir = null;
        dirChildren = null;
    }

    public Object[] queryChildren() {
        if (children == null) {
            dirChildren = dir.listFiles(dirFilter);
            children = new FileBubble[dirChildren.length+1];

            for (int i = 0; i < dirChildren.length; ++i)
                children[i+1] = new FileBubble(dirChildren[i]);
        }

        //System.out.println("qC: for " + dir + " cnt = " + children.length);
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
        try {
            if (laba != null)
                g.setColor(color[laba.getTuple()%color.length]);
            else
                g.setColor(Color.yellow);

            g.fillOval(r.x+1, r.y+1, r.width-1, r.height-1);
            g.setColor(Color.black);
            g.drawOval(r.x, r.y, r.width, r.height);

            String str = (dir != null) ? dir.getCanonicalPath() : "<null>";
            int w = g.getFontMetrics().stringWidth(str);

            if (w > r.width) {
                if (dir != null) {
                    str = ".."+dir.getName();
                    w = g.getFontMetrics().stringWidth(str);
                }
            }

            int x = (r.x+(r.width/2))-(w/2);
            int y = r.y+(r.height/2);
            g.drawString(str, x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Graphics g) {
        // don't call super.update()! We're not attached to anything! 
        paint(g);
    }

    /**
     * Testing for Bubbles
     *
     * @param av DOCUMENT ME!
     */
    public static void main(String[] av) {
        try {
            JFrame f =
                new JFrame() {

                    {
                        enableEvents(WindowEvent.WINDOW_EVENT_MASK);
                    }

                    protected void processWindowEvent(WindowEvent e) {
                        if (e.getID() == WindowEvent.WINDOW_CLOSING)
                            System.exit(0);
                    }
                };

            f.getContentPane().setLayout(new BorderLayout());
            f.setSize(800, 500);

            SoapBubbles sb = new SoapBubbles(null, FileBubble.class);
            f.getContentPane().add(sb, "Center");
            f.setVisible(true);
        } catch (Exception e) {
            System.out.println("Error: "+e);
        }
    }
}
