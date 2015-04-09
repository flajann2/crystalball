package com.lrc.soap;

/**
 * This represents a circle to SoapBubbles.
 *
 * @see SoapBubbles
 */
public class Circle extends FloatingPair {
    /** Radius of circle */
    public double r = 1.0;

    Circle() {}

    Circle(double x, double y, double r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }
}
