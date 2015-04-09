package com.lrc.soap;

/**
 * Logical Abstract Bubble Address  Tuple indices start at one, as is the mathematical
 * convention.
 */
public class Laba implements java.io.Serializable, Cloneable {
    int[] addr;

    public Laba(int tuple) {
        addr = new int[tuple];
    }

    /**
     * Create a Laba with one more extra tuple than current.
     *
     * @param l DOCUMENT ME!
     * @param index DOCUMENT ME!
     */
    public Laba(Laba l, int index) {
        this(l.addr.length+1);
        System.arraycopy(l.addr, 0, addr, 0, l.addr.length);
        addr[addr.length-1] = index;
    }

    /**
     * Clone
     *
     * @param l DOCUMENT ME!
     */
    private Laba(Laba l) {
        this(l.addr.length);
        System.arraycopy(l.addr, 0, addr, 0, l.addr.length);
    }

    /**
     * get the index of given tuple
     *
     * @param t DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getIndex(int t) {
        return addr[t-1];
    }

    /**
     * Get the index of last tuple
     *
     * @return DOCUMENT ME!
     */
    public int getIndex() {
        return addr[getTuple()-1];
    }

    public void setIndex(int t, int index) {
        addr[--t] = index;
    }

    public int getTuple() {
        return addr.length;
    }

    protected Object clone() throws CloneNotSupportedException {
        return new Laba(this);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[");

        for (int i = 0; i < addr.length; ++i)
            sb.append(""+addr[i]).append((i < (addr.length-1)) ? "." : "");

        sb.append("]");

        return sb.toString();
    }

    public int hashCode() {
        int h = 117;

        for (int i = 0; i < addr.length; ++i)
            h += (((addr[i]*89) << i)+(addr[i]*101));

        return h;
    }

    public boolean equals(Object o) {
        if (o instanceof Laba) {
            Laba l = (Laba) o;

            if (l.addr.length == addr.length) {
                for (int i = 0; i < addr.length; ++i)
                    if (addr[i] != l.addr[i])
                        return false;

                return true;
            }
        }

        return false;
    }
}
