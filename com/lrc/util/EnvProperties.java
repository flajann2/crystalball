package com.lrc.util;

import java.io.BufferedReader;

/*
 * EnvProperties - view of environment variables
 *
 * Copyright (c) 1999 Lee Brown.  All rights reserved.
 * Permission to use is granted without warranty on the condition that
 * the copyright notice and author information is retained in the code.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Enumeration;
import java.util.Properties;


/**
 * The <code>EnvProperties</code> class provides a view of the application's environment
 * variables.  The class extends the <code>Properties</code> class such that it
 * automatically loads the environment when an object of this class is first
 * constructed.  After constructing an object, the standard <code>Properties</code> API
 * can be used to manage the environment variables.
 * 
 * <p>
 * There is no mechanism within the standard Java API that can get you environment
 * variables.  The recommendation is that you use a wrapper around the application that
 * assigns system properties from environment variables. Then you should use
 * <code>System.getProperty(<i>name</i>)</code> to get the value of the named variable.
 * This preferred mechanism puts the platform-specific code outside of the
 * platform-independent Java application.
 * </p>
 * 
 * <p>
 * If you want to ignore the recommendation, then this class may work for you on some
 * platforms.  It attempts to run a command to get the variable definitions and then
 * parse the response.  Unfortunately, it can only make a stab at figuring out what
 * command to run.  It tries these:
 * </p>
 * 
 * <p>
 * Windows 95: <code>command.com /C set</code><br>
 * Windows NT: <code>cmd /C set</code><br>
 * Unix: <code>/usr/bin/env</code>
 * </p>
 * 
 * <p>
 * The command should print each assignment as <code>name=value</code> without quoting or
 * leading or trailing spaces, one per line.
 * </p>
 * 
 * <p>
 * Although it is possible to modify this class's cache of environment variable
 * definitions, modifications do not affect the underlying environment as maintained by
 * the operating system.
 * </p>
 * 
 * <p>
 * This class will not correctly report environment variable values that contain
 * newlines.  Note also that UNIX environment variables set by the shell must be
 * exported to be seen by other applications.
 * </p>
 *
 * @author Lee Brown
 * @version JDK 1.2 1999/12/16
 *
 * @see java.util.Properties
 * @see java.lang.System
 */
public class EnvProperties extends Properties {
    //final static String IDENT = "Copyright 1999 Lee Brown.  All rights reserved.  1999/12/16";
    static EnvProperties ep = new EnvProperties();

    /**
     * The constructor loads the image of the environment. The first instance of the
     * constructor loads a static table, subsequent instances continue to use that
     * table.
     *
     * @see java.util.Properties#Properties()
     */
    EnvProperties() {
        super();

        if (isEmpty()) {
            load();
        }
    }

    /**
     * The constructor loads the image of the environment, plus stores a default
     * properties object to be used if a key is not present in the primary table. The
     * first instance of the constructor loads a static table, subsequent instances
     * continue to use that table.
     *
     * @param defaultProps default Properties object
     *
     * @see java.util.Properties#Properties(java.util.Properties)
     */
    public EnvProperties(Properties defaultProps) {
        super(defaultProps);

        if (isEmpty()) {
            load();
        }
    }

    static public Properties getProperties() {
        return ep;
    }

    /**
     * Cache the environment variable definitions into the properties table. The
     * environment is obtained by running a platform-specific command that writes the
     * environment to standard output, which is read, parsed, and saved.  This method is
     * called when the first object is constructed, and may also be called explicitly
     * (although note that an application's environment does not change once the
     * application is started).
     *
     * @see java.util.Properties#load
     */
    void load() {
        // Determine the platform type so we can figure out what
        // command to run.
        String[] cmd;
        Properties sysProps = System.getProperties();
        String osName = sysProps.getProperty("os.name");

        //System.out.println("os is " + osName);	// DEBUG
        if (osName.equals("Windows NT") || osName.equals("Windows 2000")) {
            cmd = new String[3];
            cmd[0] = "cmd";
            cmd[1] = "/C";
            cmd[2] = "set";
        } else if (osName.startsWith("Wind")) {
            cmd = new String[3];
            cmd[0] = "command.com";
            cmd[1] = "/C";
            cmd[2] = "set";
        } else {
            cmd = new String[1];
            cmd[0] = "/usr/bin/env";
        }

        // Run the platform-specific command and collect the output.
        // We expect var=value on each line.  We can't handle newlines
        // within a value.
        try {
            InputStream is = Runtime.getRuntime().exec(cmd).getInputStream();
            load(is);
            is.close();
        } catch (IOException ioe) {}
    }

    /**
     * Load property values into the properties table. Property values are read from the
     * input stream, one per line. The form of each line should be
     * <code>name=value</code>, where <code>name</code> is the property name and
     * <code>value</code> is the property value.  Values include all characters after
     * the equals sign until the end of the line.
     * 
     * <p>
     * This method overrides the superclass method because the base
     * <code>Properties</code> method treats backslashes and the like as escape
     * characters, and that does not match what we will get from the commands that
     * produce the environment variable definitions.
     * </p>
     *
     * @param is InputStream for environment variable assignments
     *
     * @throws IOException InputStream is not readable
     */
    public void load(InputStream is) throws IOException {
        InputStreamReader isrdr = new InputStreamReader(is);
        BufferedReader in = new BufferedReader(isrdr);

        clear();

        String s;

        while ((s = in.readLine()) != null) {
            int n = s.indexOf('=');

            if (n > 0) {
                String key = new String(s.substring(0, n));
                String value = new String(s.substring(n+1));
                setProperty(key, value);
            }
        }

        in.close();
    }

    /**
     * Return an array of strings representing all environment variable assignments.
     * Each string has the form <code>name=value</code>.
     * 
     * <p>
     * If you modify the local copy of the environment and wish to run child processes
     * that inherit the modified environment, then you will need to run
     * <code>Runtime.exec(<i>command</i>, <i>envp</i>)</code> where
     * <code><i>envp</i></code> is an array of strings defining the new environment.
     * This method will produce the requisite array.
     * </p>
     *
     * @return array of strings representing environment assignments
     */
    String[] envArray() {
        int k = 0;
        String[] envp = new String[size()];

        for (Enumeration e = propertyNames(); e.hasMoreElements();) {
            String name = (String) e.nextElement();
            String value = getProperty(name);
            envp[k++] = name+"="+value;
        }

        return envp;
    }

    /**
     * Produce a single String representation of the environment. This string will
     * contain each <code>name=value</code> assignment separated by a new line
     * character.
     * 
     * <p>
     * This method overrides the superclass so that it can produce a String that looks
     * like what we need to get for the <code>load</code> method.
     * </p>
     *
     * @return String representation of the entire environment
     */
    public String toString() {
        StringBuffer s = new StringBuffer();
        String[] strArray = envArray();

        for (int k = 0; k < strArray.length; k++) {
            s.append(strArray[k]+"\n");
        }

        return s.toString();
    }

    /**
     * The main method demonstrates the environment variable API. The values of the named
     * environment variables are written to standard output.
     *
     * @param args names of environment variables
     */
    static void main(String[] args) {
        EnvProperties env = (EnvProperties) EnvProperties.getProperties();

        System.out.println("Individual values:");

        for (int i = 0; i < args.length; i++) {
            String value = env.getProperty(args[i]);

            if (value != null)
                System.out.println(args[i]+" = "+value+" ("+value.length()+")");
            else
                System.out.println(args[i]+" = [null]");
        }

        env.setProperty("XYZ", "xyz");

        System.out.println("\n\nEntire environment:");
        System.out.println(env.toString());

        String old = (String) env.remove("XYZ");

        if (old == null)
            old = "[null]";

        System.out.println("Removed XYZ, value was "+old);

        String[] a = env.envArray();
        System.out.println("Array of "+env.size()+" strings:");

        for (int j = 0; j < env.size(); j++)
            System.out.println("\t"+a[j]);

        System.out.println("done");

        System.exit(0);
    }
}
