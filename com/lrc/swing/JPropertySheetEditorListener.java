/*
 * JPropertySheetEditorListener.java
 *
 * Created on April 29, 2001, 2:24 PM
 */
package com.lrc.swing;

/**
 * DOCUMENT ME!
 *
 * @author fred
 * @version
 */
public interface JPropertySheetEditorListener {
    /**
     * When the user finishes editing the bean properties and clicks "dismiss", this is
     * called to notify listener of dismissal.
     */
    public void dismissed(final java.lang.Object bean);
}
