/*
 * Created on Sep 16, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.vshake.crystal.applet;

import com.allaire.wddx.WddxDeserializer;
import com.allaire.wddx.WddxSerializer;

import com.vshake.crystal.*;

import org.xml.sax.InputSource;

//import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

import java.util.*;

import javax.swing.*;


/**
 * DOCUMENT ME!
 *
 * @author fred
 */
public class RemoteBall extends JApplet {
    CrystalBall cb = null;
    RBNode root = null;
    HashMap world = new HashMap();
    WddxDeserializer wds;
    WddxSerializer ws;

	// TODO change this to be URL of production server -- make sure the URL is correct!
    String base = "http://localhost/shake/crystal_server.php";

    /**
     * DOCUMENT ME!
     *
     * @throws HeadlessException
     */
    public RemoteBall() throws HeadlessException {
        super();

		try {
			ws =  new WddxSerializer();
			wds = new WddxDeserializer("com.jclark.xml.sax.Driver");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(cb = new CrystalBall());
    }

    /* (non-Javadoc)
     * @see java.applet.Applet#destroy()
     */
    public void destroy() {
        // TODO Auto-generated method stub
        super.destroy();
    }

    /* (non-Javadoc)
     * @see java.applet.Applet#init()
     */
    public void init() {
        String s = null;

		Object o = queryServer("test", "just an object");
		System.out.println("TEST: WDDX return = " + o.toString());

        for (int i = 0; (s = getParameter(Integer.toString(i))) != null; ++i) {
            // s contains a string of the format user=friend1,friend2,... 
            // We want to parse this string into an array
            String[] sa = s.split("[=,]");

            if (root == null)
                root = new RBNode(sa[0]);

            HashSet friends = new HashSet();

            for (int j = 1; j < sa.length; ++j) {
                friends.add(sa[j]);
                System.out.println("Friend "+sa[j]+" added.\n");
            }

            world.put(sa[0], friends);
        }

        // Set up back references
        //Iterator kit = world.keySet().iterator();
        /*
           Object[] kar = world.keySet().toArray();
           for (int ik = 0; ik < kar.length; ++ik) {
               String k = (String) kar[ik];
               HashSet friends = (HashSet) world.get(k);
               Iterator fit = friends.iterator();
               while (fit.hasNext()) {
                   String f = (String) fit.next();
                   HashSet w = (HashSet) world.get(f);
                   if (w == null) w = new HashSet();
                   w.add(k);
                   world.put(f, w);
               }
           }
         */
        cb.setCurrentBNode(root);
        System.out.println("Init complete");
    }

    /* (non-Javadoc)
     * @see java.applet.Applet#start()
     */
    public void start() {
        // TODO Auto-generated method stub
        System.out.println("We started up");
        super.start();
    }

    /* (non-Javadoc)
     * @see java.applet.Applet#stop()
     */
    public void stop() {
        // TODO Auto-generated method stub
        super.stop();
        System.out.println("We stopped");
    }
    
    /**
     * Send a WDDX packet to server and get a response.
     * @param funct -- name of function server-side to handle this query
     * @param qry -- the query packet
     * @return result packet
     */
    public Object queryServer(String funct,  Object qry) {
        try {
            // Serialize packet
            StringWriter sw = new StringWriter();
            ws.serialize(qry, sw);
            
            // Create a connection to the server.
            System.out.println("Initing the cry applet.");
            URL url = new URL(base + "?cry_" + funct + "=" + URLEncoder.encode(sw.toString(), "UTF-8"));
            URLConnection co = (HttpURLConnection) url.openConnection();
            co.setDoInput(true); // set for reading
            
            // Read response and return it.
            InputStream is = co.getInputStream();
            InputSource sax = new InputSource(is);
            Object ret = wds.deserialize(sax);
            is.close();
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    class RBNode extends BNodeAdapter {
        Collection nei = null;

        //String ns;
        RBNode(String s) {
            caption = s;
        }

        /* (non-Javadoc)
         * @see com.vshake.crystal.BNodeAdapter#addNeighbor(com.vshake.crystal.BNode)
         */
        public void addNeighbor(BNode bn) {}

        /* (non-Javadoc)
         * @see com.vshake.crystal.BNode#getDescription()
         */
        public String getDescription() {
            return caption;
        }

        /* (non-Javadoc)
         * @see com.vshake.crystal.BNode#getNeighbors(com.vshake.crystal.LinkType)
         */
        public Collection getNeighbors(LinkType lt) {
            if (nei == null) {
                HashSet friends = (HashSet) world.get(caption);
                nei = new HashSet();

                if (friends != null) {
                    Iterator fit = friends.iterator();

                    while (fit.hasNext()) {
                        nei.add(new RBNode((String) fit.next()));
                    }
                }
            }

            return nei;
        }
    }
}
