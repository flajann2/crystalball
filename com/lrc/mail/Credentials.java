package com.lrc.mail;

import java.io.Serializable;


/**
 * Immutable Logon credentials.
 */
public class Credentials implements Serializable {
    private String name;
    private String password;

    public Credentials() {
        name = "guest";
        password = "guest";
    }

    public Credentials(String _name, String _password) {
        name = _name;
        password = _password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean equals(Object obj) {
        // This method is derived from class java.lang.Object
        // to do: code goes here
        return false;
    }

    public String toString() {
        return name+", "+password;
    }
}
