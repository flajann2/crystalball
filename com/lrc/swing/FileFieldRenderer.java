/*
 * FileFieldRenderer.java
 *
 * Created on June 3, 2001, 12:26 AM
 */
package com.lrc.swing;

import java.awt.*;

import java.io.File;

import javax.swing.*;
import javax.swing.table.*;


/**
 * DOCUMENT ME!
 *
 * @author fred
 * @version
 */
class FileFieldRenderer extends FileField implements TableCellRenderer {
    /**
     * Creates new FileFieldRenderer
     */
    public FileFieldRenderer() {}

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int col) {
        if ((value != null) && (col == 1))
            setFile(new File(value.toString()));

        return this;
    }
}
