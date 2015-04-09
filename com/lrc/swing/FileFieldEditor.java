/*
 * FileFieldEditor.java
 *
 * Created on June 3, 2001, 12:12 PM
 */
package com.lrc.swing;

import java.awt.*;

import java.io.File;

import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;


/**
 * Editor for JPropertySheetEditor
 *
 * @author fred
 * @version
 */
class FileFieldEditor extends FileField implements TableCellEditor {
    File oldFile = null;

    /**
     * Creates new FileFieldEditor
     */
    public FileFieldEditor() {}

    public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table,
                                                          java.lang.Object value,
                                                          boolean isSelected, int row,
                                                          int col) {
        oldFile = getFile();

        if (value instanceof File)
            setFile((File) value);
        else if (value != null)
            setFile(new File(value.toString()));
        else
            setFile(new File("./"));

        return this;
    }

    public void cancelCellEditing() {
        setFile(oldFile);
    }

    public boolean stopCellEditing() {
        return true;
    }

    public java.lang.Object getCellEditorValue() {
        return getFile();
    }

    public boolean shouldSelectCell(java.util.EventObject p1) {
        return true;
    }

    public boolean isCellEditable(java.util.EventObject p1) {
        return true;
    }
}
