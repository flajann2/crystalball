package com.lrc.swing;

import java.awt.*;
import java.awt.event.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.*;

import javax.swing.*;


/**
 * "Widget" For selecting a date.
 */
public class JCalendar extends JComponent implements java.io.Serializable, ItemSelectable {
    public static boolean debug = false;
    protected Calendar cal = null;
    protected DateFormat year = new SimpleDateFormat("yyyy");
    protected DateFormat month = new SimpleDateFormat("MMM");
    protected DateFormat day = new SimpleDateFormat("dd");
    private java.util.List actionListeners = new LinkedList();
    private java.util.List itemListeners = new LinkedList();

    // general layout
    private Box navigator = Box.createHorizontalBox();
    private Box paper = Box.createVerticalBox(); // 5 rows
    private Box control = Box.createHorizontalBox();

    // paper
    private JToggleButton[][] tog = new JToggleButton[6][7];
    private Box[] weeks = new Box[6];
    private ButtonGroup bg = new ButtonGroup();

    // navigator
    JButton navMFwd = new JButton(">");
    JButton navMRev = new JButton("<");
    JButton navYFwd = new JButton(">>");
    JButton navYRev = new JButton("<<");
    JLabel navMonthField = new JLabel("January");
    JLabel navYearField = new JLabel("2001");

    // control
    JButton ctrlDismiss = new JButton("Dismiss");
    private JFrame externalFrame = null;

    public JCalendar(Calendar cal) {
        this.cal = cal;

        // General layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(2));
        add(navigator);
        add(Box.createVerticalStrut(2));
        add(paper);
        add(Box.createVerticalStrut(2));
        add(control);
        add(Box.createVerticalStrut(2));

        // navigator setup
        navigator.add(Box.createHorizontalStrut(2));
        navigator.add(navYRev);
        navigator.add(Box.createHorizontalStrut(2));
        navigator.add(navMRev);
        navigator.add(Box.createHorizontalGlue());
        navigator.add(navMonthField);
        navigator.add(Box.createHorizontalStrut(5));
        navigator.add(navYearField);
        navigator.add(Box.createHorizontalGlue());
        navigator.add(navMFwd);
        navigator.add(Box.createHorizontalStrut(2));
        navigator.add(navYFwd);
        navigator.add(Box.createHorizontalStrut(2));

        navYRev.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    navYRevClicked();
                }
            });
        navYFwd.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    navYFwdClicked();
                }
            });
        navMRev.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    navMRevClicked();
                }
            });
        navMFwd.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    navMFwdClicked();
                }
            });

        // paper setup
        paper.add(Box.createVerticalGlue());

        for (int i = 0; i < weeks.length; ++i) {
            weeks[i] = Box.createHorizontalBox();
            paper.add(weeks[i]);

            if (i < (weeks.length-1))
                paper.add(Box.createVerticalStrut(2));
        }

        paper.add(Box.createVerticalGlue());

        for (int w = 0; w < tog.length; ++w) {
            weeks[w].add(Box.createHorizontalStrut(2));

            for (int d = 0; d < tog[w].length; ++d) {
                tog[w][d] = new JToggleButton(""+w+d);
                bg.add(tog[w][d]);
                weeks[w].add(tog[w][d]);

                if (d < (tog[w].length-1))
                    weeks[w].add(Box.createHorizontalStrut(2));

                tog[w][d].addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            dayClicked(e);
                        }
                    });
            }

            weeks[w].add(Box.createHorizontalStrut(2));
        }

        // control setup
        control.add(ctrlDismiss);
        ctrlDismiss.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ctrlDismissClicked();
                }
            });

        setSize(363, 238);

        //setOpaque(true);
        setBackground(Color.white);
        setDoubleBuffered(true);
    }

    public JCalendar() {
        this(Calendar.getInstance());
    }

    /**
     * Update calendar display with current calendar.
     */
    public void update() {
        Date time = cal.getTime();
        int mday = cal.get(Calendar.DAY_OF_MONTH);

        // Set the month and year.
        navMonthField.setText(month.format(time));
        navYearField.setText(year.format(time));

        // Find out what day the first of the month starts with.
        Calendar wcal = (Calendar) cal.clone();
        wcal.set(Calendar.DAY_OF_MONTH, 1);
        clearDays();

        for (int m = wcal.get(Calendar.MONTH); wcal.get(Calendar.MONTH) == m;
                 wcal.add(Calendar.DATE, 1)) {
            int w = wcal.get(Calendar.WEEK_OF_MONTH)-1;
            int d = wcal.get(Calendar.DAY_OF_WEEK)-1;

            //System.out.println("w=" + w + ", d=" + d + ", month=" + wcal.get(Calendar.MONTH));
            tog[w][d].setText(day.format(wcal.getTime()));
            tog[w][d].setEnabled(true);

            if (wcal.get(Calendar.DAY_OF_MONTH) == mday)
                tog[w][d].setSelected(true);
        }

        fireItemEvent(0, null);
    }

    /**
     * Set all days to disabled and clear text
     */
    void clearDays() {
        for (int w = 0; w < tog.length; ++w)
            for (int d = 0; d < tog[w].length; ++d) {
                tog[w][d].setText("00");
                tog[w][d].setEnabled(false);
            }
    }

    private void navYRevClicked() {
        cal.add(Calendar.YEAR, -1);
        update();
    }

    private void navYFwdClicked() {
        cal.add(Calendar.YEAR, 1);
        update();
    }

    private void navMRevClicked() {
        cal.add(Calendar.MONTH, -1);
        update();
    }

    private void navMFwdClicked() {
        cal.add(Calendar.MONTH, 1);
        update();
    }

    private void ctrlDismissClicked() {
        fireActionEvent("clicked");
    }

    private void dayClicked(ActionEvent e) {
        String t = ((JToggleButton) e.getSource()).getText();
        int day = Integer.valueOf(t).intValue();
        cal.set(Calendar.DAY_OF_MONTH, day);

        fireItemEvent(day, this);
    }

    void fireItemEvent(int day, ItemSelectable source) {
        ItemEvent ie = new ItemEvent(this, day, source, 1);
        Iterator it = itemListeners.iterator();

        while (it.hasNext())
            ((ItemListener) it.next()).itemStateChanged(ie);
    }

    void fireActionEvent(String cmd) {
        ActionEvent ae = new ActionEvent(this, 0, cmd);
        Iterator it = actionListeners.iterator();

        while (it.hasNext())
            ((ActionListener) it.next()).actionPerformed(ae);
    }

    public void addActionListener(ActionListener al) {
        actionListeners.add(al);
    }

    public void removeActionListener(ActionListener al) {
        actionListeners.remove(al);
    }

    public void addItemListener(ItemListener il) {
        itemListeners.add(il);
    }

    public void removeItemListener(ItemListener il) {
        itemListeners.remove(il);
    }

    public Object[] getSelectedObjects() {
        return null;
    }

    public Calendar getCalendar() {
        return cal;
    }

    public void setCalendar(Calendar cal) {
        this.cal = cal;
        update();
    }

    public void setDismissText(String t) {
        ctrlDismiss.setText(t);
    }

    public JFrame getExternalFrame() {
        return externalFrame;
    }

    public static JCalendar createWithExternalFrame(final String title,
                                                    final String dismissText) {
        final JCalendar cal = new JCalendar();
        final JFrame f = new JFrame(title);
        cal.externalFrame = f;

        f.setDefaultCloseOperation(f.HIDE_ON_CLOSE);
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(cal, BorderLayout.CENTER);
        f.setSize(250, 150);
        f.pack();

        //f.setVisible(true);
        cal.setDismissText(dismissText);
        cal.update();
        cal.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (debug)
                        System.out.println("Clicked - date is "
                                           +cal.getCalendar().getTime());

                    cal.externalFrame.setVisible(false);
                }
            });

        cal.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (debug)
                        System.out.println("Changed - date is "
                                           +cal.getCalendar().getTime());
                }
            });

        return cal;
    }

    public static void main(String[] av) {
        //debug = true;
        final JCalendar cal =
            JCalendar.createWithExternalFrame("Test cal", "Go With it!");
        final JFrame f = cal.getExternalFrame();

        f.setVisible(true);

        cal.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.out.println("TEST: Clicked - date is "
                                       +cal.getCalendar().getTime());
                    System.exit(0);
                }
            });

        cal.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    System.out.println("TEST: Changed - date is "
                                       +cal.getCalendar().getTime());
                }
            });
    }
}
