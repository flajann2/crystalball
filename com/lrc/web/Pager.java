package com.lrc.web;

import java.io.*;

import java.net.*;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Pager {
    public static void main(String[] av) {
        System.out.println("Url to be read: "+av[0]);

        try {
            URL url = new URL(av[0]);
            BufferedReader br =
                new BufferedReader(new InputStreamReader(url.openStream()));
            String s;

            while ((s = br.readLine()) != null)
                System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
