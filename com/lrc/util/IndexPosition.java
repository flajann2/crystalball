package com.lrc.util;

/**
 * index tracking.
 */
public class IndexPosition implements java.io.Serializable {
    int index = 0;
    int reset_to_this_index = 0;

    public IndexPosition() {}

    public IndexPosition(int i) {
        reset_to_this_index = index = i;
    }

    public void reset() {
        index = reset_to_this_index;
    }

    public int get() {
        return index;
    }

    public int set(int i) {
        return index = i;
    }

    public int increment() {
        return ++index;
    }

    public int decrement() {
        return --index;
    }

    public String toString() {
        return "IP="+index+", Reset="+reset_to_this_index;
    }
}
