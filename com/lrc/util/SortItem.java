package com.lrc.util;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public interface SortItem {
    /**
     * This method will be called to compare this with another item si. returns -1, 0, 1
     * if
     *
     * @return DOCUMENT ME!
     */
    public int compare(SortItem si);
}
