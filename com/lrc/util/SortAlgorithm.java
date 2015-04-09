/*
 * @(#)SortAlgorithm.java       1.4 96/12/06
 *
 * Copyright (c) 1994-1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

/**
 * A generic sort demonstration algorithm SortAlgorithm.java, Thu Oct 27 10:32:35 1994
 */
package com.lrc.util;

import java.util.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public abstract class SortAlgorithm {
    private SortNotify parent;
    protected boolean stopRequested = false; // stop sorting when true

    /**
     * Set the parent.
     *
     * @param p DOCUMENT ME!
     */
    public void setParent(SortNotify p) {
        parent = p;
    }

    /**
     * Pause for a while.
     *
     * @throws Exception DOCUMENT ME!
     */
    protected void pause() throws Exception {
        parent.sn_pause(-1, -1);
    }

    /**
     * Pause for a while and mark item 1.
     *
     * @param H1 DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    protected void pause(int H1) throws Exception {
        if (stopRequested) {
            throw new Exception("Sort Algorithm");
        }

        parent.sn_pause(H1, -1);
    }

    /**
     * Pause for a while and mark item 1 & 2.
     *
     * @param H1 DOCUMENT ME!
     * @param H2 DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    protected void pause(int H1, int H2) throws Exception {
        if (stopRequested) {
            throw new Exception("Sort Algorithm");
        }

        parent.sn_pause(H1, H2);
    }

    /**
     * Stop sorting.
     */
    public void stop() {
        stopRequested = true;
    }

    /**
     * Initialize
     */
    public void init() {
        stopRequested = false;
    }

    /**
     * This method will be called to sort an array of items.
     */
    abstract void sort(Vector a) throws Exception;

    /**
     * This method will be called to swap two items.
     *
     * @param v DOCUMENT ME!
     * @param index1 DOCUMENT ME!
     * @param index2 DOCUMENT ME!
     */
    void swap(Vector v, int index1, int index2) {
        Object o1 = v.elementAt(index1);
        Object o2 = v.elementAt(index2);
        v.setElementAt(o1, index2);
        v.setElementAt(o2, index1);
    }

    int compare(Vector v, int index1, int index2) {
        return ((SortItem) v.elementAt(index1)).compare((SortItem) v.elementAt(index2));
    }

    int compare(Vector v, int index1, SortItem si) {
        return ((SortItem) v.elementAt(index1)).compare(si);
    }

    SortItem element(Vector v, int index) {
        return (SortItem) v.elementAt(index);
    }
}
