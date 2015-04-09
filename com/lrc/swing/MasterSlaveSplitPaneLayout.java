/*
 * MasterSlaveSplitPaneLayout.java
 *
 * Created on January 16, 2002, 10:14 AM
 */
package com.lrc.swing;

import java.awt.*;

import java.util.*;

import javax.swing.*;


/**
 * Layout for the JMasterSlaveSplitPane.
 *
 * @author root
 * @version
 */
class MasterSlaveSplitPaneLayout implements java.awt.LayoutManager2 {
    private static final boolean debug = false;
    private static final int[] emptyDiv = new int[0];
    private JMasterSlaveSplitPane s = null;
    private HashMap slaveSize = new HashMap(89);
    private boolean vertical = true; // align vertically if true (default)
    private int[] dividers = emptyDiv;
    private int div_sep = 4; // division seperation
    private int min_master = 100;

    /**
     * Creates new MasterSlaveSplitPaneLayout.
     *
     * @param s component
     */
    public MasterSlaveSplitPaneLayout(JMasterSlaveSplitPane s) {
        this.s = s;
    }

    /**
     * Component in location 0 is the master. All other components are slaves.
     *
     * @param s DOCUMENT ME!
     */
    public void layoutContainer(java.awt.Container s) {
        Component[] car = s.getComponents();
        Dimension d = s.getSize();
        int space = vertical ? d.height : d.width;
        dividers = new int[car.length];

        if (dividers.length > 0)
            dividers[0] = 0;
        else

            return; // nothing to do!

        int divSpace = div_sep*(car.length-1);
        int slaveSpace = slaveSpace(car);
        int realSlaveSpace = slaveSpace;
        double adj = 1.0;

        for (int i = 1; i < car.length; ++i) // minimum size slaveSpace
         {
            Dimension m = car[i].getMinimumSize();
            int min = vertical ? m.height : m.width;

            if (getSlave(car[i]) < min)
                putSlave(car[i], min);
        }

        if ((slaveSpace+min_master) > space) // not enough room?
         {
            realSlaveSpace = space-min_master;
            adj = (double) (realSlaveSpace-divSpace)/(double) (slaveSpace-divSpace);

            if (adj < 0.1)
                adj = 0.1;
        }

        adjustSlaveSpace(car, adj);
        slaveSpace = slaveSpace(car);

        int masterSpace = space-slaveSpace;
        putSlave(car[0], masterSpace); // just to have it there!

        int sp = masterSpace;

        for (int i = 1; i < car.length; ++i) {
            dividers[i] = sp;
            sp += (getSlave(car[i])+div_sep);
        }

        // Do final layout based on divider positions
        for (int i = 0; i < car.length; ++i) {
            car[i].setBounds(vertical ? 0 : (dividers[i]+((i == 0) ? 0 : div_sep)), //x
                             (!vertical) ? 0 : (dividers[i]+((i == 0) ? 0 : div_sep)), //y
                             vertical ? d.width : getSlave(car[i]), // width
                             (!vertical) ? d.height : getSlave(car[i])); // height

            car[i].doLayout();
        }
    }

    private int getSlave(Component c) {
        return ((Integer) slaveSize.get(c)).intValue();
    }

    private void putSlave(Component c, int size) {
        slaveSize.put(c, new Integer(size));
    }

    /**
     * Compute the extent of slave space.
     *
     * @param car DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private int slaveSpace(Component[] car) {
        int sp = 0;

        for (int i = 1; i < car.length; ++i)
            sp += (div_sep+getSlave(car[i]));

        return sp;
    }

    private void adjustSlaveSpace(Component[] car, double adj) {
        if (adj < 1.0) {
            for (int i = 1; i < car.length; ++i) {
                double slave = getSlave(car[i]);
                slave *= adj;
                putSlave(car[i], (int) slave);
            }
        }
    }

    public void addLayoutComponent(java.awt.Component c, java.lang.Object obj) {
        if (obj == null) {
            Dimension d = c.getPreferredSize();
            obj = new Integer(vertical ? d.height : d.width);
        }

        slaveSize.put(c, (Integer) obj);
    }

    public void addLayoutComponent(java.lang.String str, java.awt.Component c) {
        addLayoutComponent(c, null);
    }

    public float getLayoutAlignmentX(java.awt.Container s) {
        return 0f;
    }

    public float getLayoutAlignmentY(java.awt.Container s) {
        return 0f;
    }

    public void invalidateLayout(java.awt.Container s) {
        dividers = emptyDiv;
    }

    public java.awt.Dimension maximumLayoutSize(java.awt.Container s) {
        return new Dimension(5000, 5000);
    }

    public java.awt.Dimension minimumLayoutSize(java.awt.Container s) {
        Dimension d = new Dimension(0, 0);
        Component[] car = s.getComponents();

        for (int i = 0; i < car.length; ++i) {
            d.width = Math.max(d.width, car[i].minimumSize().width);
            d.height += car[i].minimumSize().height;
        }

        d.height += (div_sep*(car.length-1));

        return d;
    }

    public java.awt.Dimension preferredLayoutSize(java.awt.Container s) {
        Dimension d = new Dimension(0, 0);
        Component[] car = s.getComponents();

        for (int i = 0; i < car.length; ++i) {
            d.width = Math.max(d.width, car[i].preferredSize().width);
            d.height += car[i].preferredSize().height;
        }

        d.height += (div_sep*(car.length-1));

        return d;
    }

    public void removeLayoutComponent(java.awt.Component c) {
        slaveSize.remove(c);
        dividers = emptyDiv;
    }

    public int[] getDividers() {
        return dividers;
    }

    public int getDividerLocation(int index) {
        return dividers[index];
    }

    public int getSeperation() {
        return div_sep;
    }

    public void setSeperation(int sep) {
        div_sep = sep;
    }

    /**
     * Move location of divider.
     *
     * @param index DOCUMENT ME!
     * @param movement DOCUMENT ME!
     */
    public void moveDivider(int index, int movement) {
        // What we really want to do is to change size of the
        // component slave.
        if (index >= 0) {
            Component c = s.getComponent(index);
            putSlave(c, getSlave(c)-movement);
        } else if (debug)
            System.out.println(getClass().getName()
                               +".moveDivider() -- index is incorrect! ("+index+")");
    }
}
