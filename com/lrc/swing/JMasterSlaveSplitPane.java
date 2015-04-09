package com.lrc.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * Allows multiple splits vertically or horizonally.
 * 
 * <p>
 * This simple implementation does not have elaborate dividers at all, but a thin-line
 * seperator. The pointer is changed appropriately when positioned over the drag point.
 * Currently, there is no keyboard  equivalents to move seperators around.
 * </p>
 * 
 * <p>
 * The organization is that of a one master pane
 * </p>
 */
public class JMasterSlaveSplitPane extends JComponent {
    public static boolean debug = false;
    static private final Cursor overCursor = new Cursor(Cursor.S_RESIZE_CURSOR);
    static private final Cursor defCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private MasterSlaveSplitPaneLayout layout;
    private boolean dragging = false;
    private int index = -1;
    private int beginSqueak = 0;

    /** Utility field used by event firing mechanism. */
    private javax.swing.event.EventListenerList listenerList = null;

    /** Holds value of property dividerLocation. */
    private int[] dividerLocation;

    /** Utility field used by bound properties. */
    private java.beans.PropertyChangeSupport propertyChangeSupport =
        new java.beans.PropertyChangeSupport(this);

    /** Utility field used by constrained properties. */
    private java.beans.VetoableChangeSupport vetoableChangeSupport =
        new java.beans.VetoableChangeSupport(this);

    public JMasterSlaveSplitPane() {
        setLayout(layout = new MasterSlaveSplitPaneLayout(this));
        addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    beginDrag(e);
                }

                public void mouseReleased(MouseEvent e) {
                    endDrag(e);
                }

                public void mouseExited(MouseEvent e) {
                    cursorUpdate(e);
                }
            });

        addMouseMotionListener(new MouseMotionListener() {
                public void mouseDragged(MouseEvent e) {
                    dragging(e);
                }

                public void mouseMoved(MouseEvent e) {
                    cursorUpdate(e);
                }
            });

        //setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     *
     * @return index of division, or -1.
     */
    private int divNear(MouseEvent e) {
        int squeak = e.getPoint().y;
        int[] div = layout.getDividers();

        for (int i = 1; i < div.length; ++i)
            if ((squeak >= div[i]) && (squeak < (div[i]+layout.getSeperation())))
                return i;

        return -1;
    }

    /**
     * Change cursor if over a divider
     *
     * @param e DOCUMENT ME!
     */
    private void cursorUpdate(MouseEvent e) {
        if (!dragging) {
            index = divNear(e);

            if (index != -1)
                setCursor(overCursor);
            else
                setCursor(defCursor);

            if (debug)
                System.out.println("over = "+index);
        }
    }

    private void beginDrag(MouseEvent e) {
        int idx = divNear(e);

        if (idx != -1)
            index = idx;

        dragging = true;
        beginSqueak = e.getY();

        if (debug)
            System.out.println("begin index = "+index+" sq = "+beginSqueak);
    }

    private void dragging(MouseEvent e) {
        // Animation of dragging goes here.
        if (debug)
            System.out.println("dragging = "+index+" move = "+(e.getY()-beginSqueak));
    }

    private void endDrag(MouseEvent e) {
        dragging = false;

        int movement = e.getY()-beginSqueak;
        layout.moveDivider(index, movement);

        if (debug)
            System.out.println("end = "+index+" move = "+movement);

        cursorUpdate(e);
        invalidate();
        layout();
        fireDividerPropertyChange(index, beginSqueak, e.getY());
    }

    public void setDividerSize(int sz) {
        layout.setSeperation(sz);
    }

    public int getDividerSize() {
        return layout.getSeperation();
    }

    public static void main(String[] av) {
        JDialog dia = new JDialog();
        dia.setSize(500, 400);
        dia.setVisible(true);
        dia.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        final JMasterSlaveSplitPane sp = new JMasterSlaveSplitPane();
        dia.getContentPane().setLayout(new BorderLayout());
        dia.getContentPane().add(sp, BorderLayout.CENTER);
        dia.validate();

        dia.addWindowListener(new WindowAdapter() {
                public void windowClosed(WindowEvent e) {
                    System.exit(0);
                }
            });

        for (int i = 0; i < 6; ++i) {
            final int iid = i;
            final Button b = new Button((i == 0) ? "Master" : ("Slave "+i));
            sp.add(b);
            b.addActionListener(new ActionListener() {
                    int id = iid;
                    Button but = b;

                    public void actionPerformed(ActionEvent e) {
                        if (id > 0)
                            sp.remove(but);
                        else {
                            final Button nb = new Button("New Slave");
                            sp.add(nb);
                            nb.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        sp.remove(nb);
                                        sp.validate();
                                    }
                                });
                        }

                        sp.validate();
                    }
                });
        }

        dia.validate();
    }

    private void fireDividerPropertyChange(int index, int _old, int _new) {
        java.beans.PropertyChangeEvent ev =
            new java.beans.PropertyChangeEvent(this, "Divider["+index+"]",
                                               new Integer(_old), new Integer(_new));
        firePropertyChangeListenerPropertyChange(ev);
    }

    /**
     * Registers PropertyChangeListener to receive events.
     *
     * @param listener The listener to register.
     */
    public synchronized void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        if (listenerList == null) {
            listenerList = new javax.swing.event.EventListenerList();
        }

        listenerList.add(java.beans.PropertyChangeListener.class, listener);
    }

    /**
     * Removes PropertyChangeListener from the list of listeners.
     *
     * @param listener The listener to remove.
     */
    public synchronized void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        listenerList.remove(java.beans.PropertyChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners about the event.
     *
     * @param event The event to be fired
     */
    private void firePropertyChangeListenerPropertyChange(java.beans.PropertyChangeEvent event) {
        if (listenerList == null)
            return;

        Object[] listeners = listenerList.getListenerList();

        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == java.beans.PropertyChangeListener.class) {
                ((java.beans.PropertyChangeListener) listeners[i+1]).propertyChange(event);
            }
        }
    }

    /**
     * Add a VetoableChangeListener to the listener list.
     *
     * @param l The listener to add.
     */
    public void addVetoableChangeListener(java.beans.VetoableChangeListener l) {
        vetoableChangeSupport.addVetoableChangeListener(l);
    }

    /**
     * Removes a VetoableChangeListener from the listener list.
     *
     * @param l The listener to remove.
     */
    public void removeVetoableChangeListener(java.beans.VetoableChangeListener l) {
        vetoableChangeSupport.removeVetoableChangeListener(l);
    }

    /**
     * Indexed getter for property dividerLocation.
     *
     * @param index Index of the property.
     *
     * @return Value of the property at <CODE>index</CODE>.
     */
    public int getDividerLocation(int index) {
        return layout.getDividerLocation(index);
    }

    /**
     * Indexed setter for dividerLocation.
     *
     * @param index Index of the property.
     * @param dividerLocation New value of the property at <CODE>index</CODE>.
     *
     * @throws java.beans.PropertyVetoException
     */
    public void setDividerLocation(int index, int dividerLocation)
                            throws java.beans.PropertyVetoException {
        int oldDividerLocation = layout.getDividerLocation(index);
        int move = dividerLocation-oldDividerLocation;

        layout.moveDivider(index, move);
        fireDividerPropertyChange(index, oldDividerLocation, dividerLocation);

        try {
            vetoableChangeSupport.fireVetoableChange("dividerLocation", null, null);
        } catch (java.beans.PropertyVetoException vetoException) {
            layout.moveDivider(index, -move);
            throw vetoException;
        }

        propertyChangeSupport.firePropertyChange("dividerLocation", null, null);
    }
}
