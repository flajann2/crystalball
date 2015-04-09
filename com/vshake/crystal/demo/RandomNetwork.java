/*
 * Created on Aug 6, 2003
 *
 */
package com.vshake.crystal.demo;

import com.vshake.crystal.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.util.*;

import javax.swing.*;


/**
 * DOCUMENT ME!
 *
 * @author fred
 */
public class RandomNetwork {
    static public boolean generateNewNodes = true;
    /**
     * Factor controlling looping -- if random number is below this factor, stay in  loo,
     * else get out.
     */
    static double rfactor = 0.80;
    static double nfactor = 0.50;

    static public void main(String[] av) {
        // TEST 1 -- objet creation RNode
        RNode root = new RNode();
        RNode node = root;
        for (int i = 0; i < 100; ++i) {
            Collection c = node.getNeighbors(null);
            System.out.println(node.getCaption() + " returned " + c.size() + " nodes: ");
            Iterator it = c.iterator();
            while (it.hasNext()) {
                node = (RNode) it.next();
                System.out.println("\t" + node.getCaption() + " has " + node.getNeighbors(null).size() + " neighbors ");
            }
        }
        System.out.println("*** Network Created. ***");
        generateNewNodes = false;
        
        // Test 2 -- Crystal Ball
        try {
            JFrame f =
                new JFrame() {
                    {
                        enableEvents(WindowEvent.WINDOW_EVENT_MASK);
                    }

                    protected void processWindowEvent(WindowEvent e) {
                        if (e.getID() == WindowEvent.WINDOW_CLOSING)
                            System.exit(0);
                    }
                };

            f.getContentPane().setLayout(new BorderLayout());
            f.setSize(800, 500);

            CrystalBall cb = new CrystalBall(root);
            f.getContentPane().add(cb, "Center");
            f.setVisible(true);
        } catch (Exception e) {
            System.out.println("Error: "+e);
        }
    }

    static class RNode extends BNodeAdapter {
        Random rand = new Random();

        /**
         * constructor for starter random node.
         */
        RNode() {
            this("RN-Root", "Root node", null);
        }

        /**
         * Construct a node for a node.
         *
         * @param cap
         * @param desc
         * @param ref
         */
        RNode(String cap, String desc, RNode ref) {
            caption = cap;
            description = desc;
            reference = ref;
            System.out.println("created RNode:" + cap + " - " + desc + " to ref " + ref);
        }

        /**
         * Create a set of random neighbors. Currently we ignore type.
         *
         * @see com.vshake.crystal.BNode#getNeighbors(com.vshake.crystal.LinkType)
         */
        public Collection getNeighbors(LinkType lt) {
        	super.getNeighbors(lt);
            
            if (generateNewNodes && neighbors.size() < 2) {
                while (rand.nextDouble() < rfactor) {
                    // We want to randomly either create neighbors, or walk backwards through the references looking for neighbors.
                    if (reference != null && rand.nextDouble() < nfactor && !reference.getNeighbors(lt).isEmpty()) {
                        // Randomly select from a pre-existing node.
                        Collection cnei = reference.getNeighbors(lt);
                        RNode[] anei = new RNode[cnei.size()];
                        anei = (RNode[]) cnei.toArray(anei);
                        
                        int i = rand.nextInt(cnei.size());
                        int j = rand.nextInt(cnei.size());
                        
                        Collection ncnei = anei[i].getNeighbors(lt);
                        addNeighbor(anei[i]);
                        anei[j].addNeighbor(anei[i]);
                    }
                    else { // create a new node
                        int i = rand.nextInt(1024000);
                        addNeighbor(new RNode("RN-" + i, "rnode generation", this));
                    }
                }
            }
            return neighbors;
        }
      }
}
