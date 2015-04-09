package com.lrc.util;

/**
 * Encapsulates a String[] object in such a fashion as to  allow us to use hashCode() and
 * equals() on the entire array.
 */
public class StringArray implements Cloneable, java.io.Serializable {
    public String[] array;

    public StringArray(String[] sa) {
        array = (String[]) sa.clone();
    }

    public String[] getArray() {
        return array;
    }

    public boolean equals(Object o) {
        if (o instanceof StringArray) {
            StringArray sa = (StringArray) o;

            for (int i = 0; i < sa.array.length; ++i) {
                if ((sa.array[i] == null) && (array[i] == null))
                    continue;

                if ((sa.array[i] == null) || (array[i] == null)) // neither should be null at this point!

                    return false;

                if (!sa.array[i].equals(array[i]))
                    return false;
            }
        } else

            return false;

        return true;
    }

    public int hashCode() {
        int hash = 2347;

        for (int i = 0; i < array.length; ++i)
            if (array[i] != null)
                hash += (array[i].hashCode()+(i*37));
            else
                hash += ((i*101)+(i*53));

        return hash;
    }
}
