package com.lrc.soap;

/**
 * The angle is represented as a sin-cos (or basis vector) pair. This will facillicate
 * fast calculations of lines, etc.
 * 
 * <p>
 * Note that ax2 + ay2 must allways equal 1.
 * </p>
 *
 * @see SoapBubbles
 */
public class Angle extends FloatingPair {

    {
        x = 1.0;
        y = 0.0;
    }

    Angle() {}

    Angle(double rad) {
        x = Math.sin(rad);
        y = Math.cos(rad);
    }

    Angle(double sin, double cos) {
        x = sin;
        y = cos;
    }

    double getRadian() {
        double r = Math.acos(y);

        // if the sin is negative, that means 
        // that the angle is on the other side of
        // the circle and we must substract r from 2Pi to adjust.
        if (x < 0.0)
            r = (2.0*Math.PI)-r;

        return r;
    }
}
