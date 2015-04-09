/*
 * Math.java
 *
 * Created on October 22, 2001, 5:51 PM
 */
package com.lrc.util;

/**
 * Collection of math functions.
 *
 * @author Fred
 * @version
 */
public final class Mathe {
    private Mathe() {}

    /**
     * Hyberbolic Tangent.
     *
     * @param x DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static double tanh(double x) {
        double ex2 = java.lang.Math.exp(x);
        ex2 *= ex2; // square it

        return (ex2-1.0)/(ex2+1.0);
    }
}
