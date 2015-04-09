package com.lrc.util;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public interface SortNotify {
    public void sn_pause(int index1, int index2);

    public void sn_completed(); // called when sort is completed.
}
