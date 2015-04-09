/*
 * Created on Aug 5, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.vshake.crystal;

/**
 * Defines a link type to return. You can define your own custom link types, or use the
 * standard types as listed below.
 *
 * @author fred
 */
public interface LinkType {
    /**
     * All Link types.
     */
    public interface AllLinks extends LinkType {}

    /**
     * Heirarchial Link Types.
     */
    public interface HeirarchialLinks extends LinkType {}
}
