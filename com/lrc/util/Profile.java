package com.lrc.util;

import java.io.*;

import java.lang.reflect.*;

import java.util.*;


/**
 * This emulates the behavior of Profile in Windows. A Profile object is attached to the
 * actual file it is invoked for.
 * 
 * <p>
 * Files that are read and written are assumed to be in ascii format. No unicode support
 * is present at this time.
 * </p>
 */
public class Profile extends Hashtable {
    /** Pathname of the file associated with this profile. */
    public String name; // should be final!!!
    boolean valid = false;
    boolean dirty = false;

    /**
     * All Profile objects must be associated with a Physical .ini file. Profile will
     * create one if not present.
     *
     * @param file DOCUMENT ME!
     * @param mustexist DOCUMENT ME!
     *
     * @throws ProfileException DOCUMENT ME!
     */
    public Profile(String file, boolean mustexist) throws ProfileException {
        name = file; // one-time assign to name

        try {
            BufferedReader br =
                new BufferedReader(new InputStreamReader(new FileInputStream(name)));

            String line = null;
            Section section = null;
            String last_tok = null;
            String left_of_eq = null;

            boolean commenting = false;
            boolean com_one_line = false;

            while ((line = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line, "[]=\"/*", true); // we want the tokens, too!

                while (st.hasMoreTokens()) {
                    String tok = (String) st.nextToken();

                    // Check for opening comment
                    if ("/".equals(tok) && st.hasMoreTokens()) // comment initiator
                     {
                        String ntok = (String) st.nextToken();

                        if ("*".equals(ntok)) // really a comment?

                            commenting = true;
                        else if ("/".equals(ntok)) // one-line comment
                         {
                            commenting = true;
                            com_one_line = true;
                        } else // resolve and reconstruct token
                         {
                            tok = tok+ntok;

                            if (last_tok != null) {
                                tok = last_tok+tok;
                                last_tok = null;
                            }
                        }
                    }

                    if (!commenting) {
                        if ("[".equals(tok)) // opening of a [section]
                         {
                            // The next token should be the string, followed by a closing ]
                            // for now, we will be a bit lax in out syntax checking.
                            StringBuffer sb = new StringBuffer();
                            String s = null;

                            while (!"]".equals((s = (String) st.nextToken())))
                                sb.append(s);

                            s = sb.toString();

                            if ((section = (Section) get(s)) == null) // not created section yet

                                put(s, section = new Section(s));
                        }
                        else if ("\"".equals(tok)) // quoted string
                         {
                            StringBuffer sb = new StringBuffer();
                            String s;

                            while (!"\"".equals((s = (String) st.nextToken())))
                                sb.append(s);

                            last_tok = tok = sb.toString();
                        }
                        else if ("=".equals(tok)) {
                            left_of_eq = last_tok;
                            last_tok = null;
                        }
                        else // assume a string
                         {
                            last_tok = tok;
                        }
                    }

                    // Check for end comment
                    if (commenting && !com_one_line && "*".equals(tok)
                            && st.hasMoreTokens()) // not a one-liner, comment end?
                     {
                        String ntok = (String) st.nextToken();

                        if ("/".equals(ntok)) // ending slash?

                            commenting = false;
                    } else if (commenting && com_one_line && !st.hasMoreTokens())
                        commenting = com_one_line = false;
                }

                // Check to see if we have a left_of_eq. If we do, and a
                //  last_tok too, add them!
                if (left_of_eq != null)
                    section.put(left_of_eq, (last_tok != null) ? last_tok : "");
            }

            valid = true;
            br.close();
        } catch (Exception e) {
            //log.warnln("Profile failure:" + e);
            valid = false;

            if (mustexist)
                throw new ProfileException(""+e);
        } finally {}
    }

    /**
     * Call to flush state out to file (will be called upon GC anyway!)
     */
    public synchronized void flush() {
        try {
            if (valid && dirty) {
                OutputStreamWriter osw =
                    new OutputStreamWriter(new FileOutputStream(name));

                for (Enumeration senum = elements(); senum.hasMoreElements();) {
                    Section s = (Section) senum.nextElement();
                    osw.write("["+s.name+"]\n", 0, s.name.length()+3);

                    for (Enumeration nvpenum = s.names(); nvpenum.hasMoreElements();) {
                        String n = (String) nvpenum.nextElement();
                        Object v = s.getValue(n, null);

                        if (v != null) {
                            String nvp = n+"="+v+"\n"; //Name-Value Pair
                            osw.write(nvp, 0, nvp.length());
                        }
                    }

                    osw.write('\n');
                }

                osw.close();
                dirty = false;
            }
        } catch (Exception e) {
            //log.warnln("Exception in flush():" + e);
        } finally {
            valid = false;
        }
    }

    /**
     * Make sure file is written back out before going away
     */
    protected void finalize() {
        flush();
    }

    /**
     * Enumerator for all of the sections. Objects are always strings.
     *
     * @return DOCUMENT ME!
     */
    public Enumeration sections() {
        return keys();
    }

    /**
     * Enumerates the names (keys) for a given section.
     *
     * @param section DOCUMENT ME!
     *
     * @return Enumeration, whose objects are always Strings.
     *
     * @exception ProfileException
     */
    public Enumeration names(String section) throws ProfileException {
        try {
            return ((Section) get(section)).names();
        } catch (Exception e) {
            throw new ProfileException("names:"+e);
        }
    }

    /**
     * Dumps entire contents into a string
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        for (Enumeration esec = elements(); esec.hasMoreElements();) {
            Section sec = (Section) esec.nextElement();
            sb.append("["+sec+"]\n");

            for (Enumeration envp = sec.keys(); envp.hasMoreElements();) {
                String name = (String) envp.nextElement();
                String value = (String) sec.get(name);
                sb.append("\t"+name+"="+value+"\n");
            }
        }

        return sb.toString();
    }

    /**
     * Deletes the profile
     *
     * @param section DOCUMENT ME!
     *
     * @return boolean
     */
    public boolean delProfile(String section) {
        Section sec;

        if ((sec = (Section) remove(section)) != null) {
            valid = true;
            dirty = true;

            return true;
        }

        return false;
    }

    /**
     * Sets the profile
     *
     * @param section DOCUMENT ME!
     * @param key DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return string representation of the profile set.
     */
    public String setProfile(String section, String key, Object value) {
        Section sec;

        if ((sec = (Section) get(section)) == null) // create a new section?
         {
            sec = new Section(section);
            put(section, sec);
        }

        String sval;
        sec.put(key, sval = value.toString());

        valid = true;
        dirty = true;

        return sval;
    }

    /**
     * Gets the string representation of value
     *
     * @param section DOCUMENT ME!
     * @param key DOCUMENT ME!
     * @param default_value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getProfile(String section, String key, Object default_value) {
        Section sec = null;
        String value;

        if (((sec = (Section) get(section)) != null)
                && ((value = (String) sec.get(key)) != null))
            return value;
        else

            return default_value.toString();
    }

    /**
     * Get the integer representation of a value.
     *
     * @param section DOCUMENT ME!
     * @param key DOCUMENT ME!
     * @param default_value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getIntegerProfile(String section, String key, Object default_value) {
        String profile = getProfile(section, key, default_value);

        return Integer.valueOf(profile).intValue();
    }

    /**
     * Parse a string value into comma-delimited Strings and return them as an
     * enumeration.
     *
     * @param section DOCUMENT ME!
     * @param key DOCUMENT ME!
     * @param default_value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Enumeration getCommaProfile(String section, String key, Object default_value) {
        final StringTokenizer st =
            new StringTokenizer(getProfile(section, key, default_value), ", ", false);

        return new Enumeration() {
                public boolean hasMoreElements() {
                    return st.hasMoreTokens();
                }

                public Object nextElement() {
                    return st.nextToken();
                }
            };
    }

    /**
     * @see #getCommaProfile
     */
    public Vector getCommaProfileAsVector(String section, String key, Object default_value) {
        Vector v = new Vector();

        for (Enumeration e = getCommaProfile(section, key, default_value);
                 e.hasMoreElements();)
            v.addElement(e.nextElement());

        return v;
    }

    /**
     * Parse a string value into a list of pairs.
     * 
     * <p>
     * A Pair list string is of the format:
     * </p>
     * 
     * <p>
     * <code>k1->v1,k2->v2,k3->v3, ... kn->vn </code>
     * </p>
     * 
     * <p>
     * The k->v pairs are enumerated into a series of Profile.Pair objects: <code>public
     * class Profile.Pair { public Object key; public Object value; }</code> Warning:
     * There is no syntax checking to speak of with this simple parsing. You will simply
     * get incorrect results (or an exception) if the string is misformatted. This
     * shortcoming will be address later.
     * </p>
     * 
     * <p></p>
     *
     * @param section DOCUMENT ME!
     * @param key DOCUMENT ME!
     * @param default_value DOCUMENT ME!
     *
     * @return Enumeration of Profile.Pair objects, each with key and value.
     *
     * @see Profile.Pair
     */
    public Enumeration getPairProfile(String section, String key, Object default_value) {
        final Enumeration e = getCommaProfile(section, key, default_value);

        return new Enumeration() {
                public boolean hasMoreElements() {
                    return e.hasMoreElements();
                }

                public Object nextElement() {
                    String s = (String) e.nextElement();
                    StringTokenizer st = new StringTokenizer(s, "->", true);
                    Pair p = new Pair();

                    while (st.hasMoreTokens()) {
                        String t = st.nextToken();

                        if (p.key == null) // first token is a key

                            p.key = t;
                        else if (t.equals("-") || t.equals(">")) {}
                        else if (p.value == null)
                            p.value = t;
                    }

                    return p;
                }
            };
    }

    /**
     * @see #getPairProfile
     */
    public Hashtable getPairProfileAsHashtable(String section, String key,
                                               Object default_value) {
        Hashtable h = new Hashtable();

        for (Enumeration e = getPairProfile(section, key, default_value);
                 e.hasMoreElements();) {
            Pair p = (Pair) e.nextElement();
            h.put(p.key, p.value);
        }

        return h;
    }

    /**
     * Attempts to load an object with this section. Entry names must match field names
     * exactly.
     * 
     * <p>
     * If there are any keys that are not found as corresponding fields in object, then
     * those keys are ignored.
     * </p>
     * 
     * <p>
     * If any fields are not specified in section, then those fields are not modified.
     * </p>
     * 
     * <p></p>
     *
     * @param section DOCUMENT ME!
     * @param o DOCUMENT ME!
     *
     * @return The Object passed in (o) is returned.
     *
     * @throws ProfileException DOCUMENT ME!
     */
    public Object getSectionAsObject(String section, Object o)
                              throws ProfileException {
        Class c = o.getClass();

        for (Enumeration e = names(section); e.hasMoreElements();)
            try {
                String key = (String) e.nextElement();
                Field f = c.getDeclaredField(key);
                String value = getProfile(section, key, null);

                //log.noteln("Type of "+f+" is "+f.getType().getName());
                // Handle all the primitive types
                if (f.getType().getName().equals("int")) {
                    int d = Integer.valueOf(value).intValue(); // use highest precison coversion
                    f.setInt(o, d);
                } else if (f.getType().getName().equals("short")) {
                    short d = Short.valueOf(value).shortValue(); // use highest precison coversion
                    f.setShort(o, d);
                } else if (f.getType().getName().equals("double")) {
                    double d = Double.valueOf(value).doubleValue(); // use highest precison coversion
                    f.setDouble(o, d);
                } else if (f.getType().getName().equals("float")) {
                    float d = Float.valueOf(value).floatValue(); // use highest precison coversion
                    f.setFloat(o, d);
                } else // Last ditch effort to convert

                    f.set(o, value);
            } catch (NoSuchFieldException ne) {
                // do nothing. we ignore those that cannot be resolved.
                //log.warnln("Profile: field exception:" + ne);
            } catch (Exception se) {
                //log.dialog(se);
            }

        return o;
    }

    /**
     * Object's fields are introspected, converted to string, and used as values in the
     * section.
     * 
     * <p>
     * All fields are converted to strings.
     * </p>
     * 
     * <p>
     * String representation of objects must not contain any newlines or nulls!!!!
     * </p>
     * 
     * <p></p>
     *
     * @param section DOCUMENT ME!
     * @param o DOCUMENT ME!
     *
     * @throws IllegalAccessException DOCUMENT ME!
     */
    public void setSectionFromObject(String section, Object o)
                              throws IllegalAccessException {
        Field[] f = o.getClass().getFields();

        for (int i = 0; i < f.length; ++i)
            setProfile(section, f[i].getName(), f[i].get(o));
    }

    /**
     * For testing only! This is a simple-minded test. It simply reads in the Profile and
     * prints the contents. The name of the profile is supplied on the command line.
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        //log.verbose_mode = true;
        //log.dialog_mode = true;
        //log.debug_mode = true;
        try {
            Profile p = new Profile(args[0], true);
            System.out.println("Profile dump of "+args[0]+"is:\n\n"+p);
        } catch (Exception e) {
            //log.dialog(e);
        }
    }

    /**
     * Represents all the entries in a particular section.
     */
    protected class Section extends Hashtable {
        /** Name of this section. */
        public String name;

        Section(String name) {
            this.name = name;
        }

        /**
         * Enumerate all of the names in this section.
         *
         * @return DOCUMENT ME!
         */
        public Enumeration names() {
            return keys();
        }

        /**
         * DOCUMENT ME!
         *
         * @param name DOCUMENT ME!
         * @param default_value DOCUMENT ME!
         *
         * @return Object can either be a String or Integer.
         */
        public Object getValue(String name, Object default_value) {
            Object g = get(name);

            if (g == null)
                g = default_value;

            return g;
        }

        public String toString() {
            return name;
        }
    }


    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision: 1.1 $
     */
    public class Pair {
        public Object key = null;
        public Object value = null;
    }
}
