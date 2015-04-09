package com.lrc.soap;

public class FRect extends FloatingRectangle
{
    /** Create an interpolated FRect based on <code> 0 <= t <= 1 </code>.<p>
    */
    public FRect interpolate(FRect er, double t)
    {
        FRect ir = new FRect();

        ir.x1 = x1 + (er.x1 - x1) * t;
        ir.x2 = x2 + (er.x2 - x2) * t;
        ir.y1 = y1 + (er.y1 - y1) * t;
        ir.y2 = y2 + (er.y2 - y2) * t;

        return ir;
    }
}

