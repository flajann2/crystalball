/*
 * Created on Aug 6, 2003
 *
 */
package com.vshake.crystal.demo;

import com.vshake.crystal.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.ImageIcon;
import java.util.*;
import java.text.DecimalFormat;

import javax.swing.*;


/**
 * DOCUMENT ME!
 *
 * @author fred
 */
public class PeopleNetwork {
    static public boolean generateNewNodes = true;

    /**
     * Factor controlling looping -- if random number is below this factor, stay in  loo,
     * else get out.
     */
    static double rfactor = 0.90;
    static double nfactor = 0.50;
    static String[] face_name = {
    	"Julie",
    	"John",
    	"Fracine",
    	"Claude",
    	"Spencer",
    	"Jack",
    	"Jill",
    	"Shelberon",
    	"Joy",
    	"James",
    	"Aaron",
    	"Hicks",
    	"Julia",
    	"Tom",
    	"Fred",
    	"Harry",
    	"Linda",
    	"Lorainne",
    	"Diane",
    	"Biff",
    	"Kennedy",
    	"Johnson",
    	"Mitchell",
    	"Bearnstien",
    	"Richberg",
    	"Wadsworth",
    	"Yossi",
    	"Linderberg",
    	"Francine",
    	"Bhon",
    	"Francois",
    	"Mrs. Baconfield",
    	"George Bush",
    	"Donald Rumsfield",
    	"John Ashcroft",
    	"Bill Clinton",
    	"Bill Gates",
    	"Call Girl",
    	"Paul Atreides",
    	"Dan Akroyd",
    	"Lenoard Nimoy",
    	"Bill Shatener",
    	"Gene Roddenbery",
    	"Goldie Hawn",
   		};
	static DecimalFormat face_file = new DecimalFormat("/home/fred/dev/java/crystalball/com/vshake/crystal/demo/pics/face_00.jpeg");

    static public void main(String[] av) {
        // TEST 1 -- objet creation PNode
        PNode root = new PNode();
        PNode node = root;

        for (int i = 0; i < 100; ++i) {
            Collection c = node.getNeighbors(null);
            System.out.println(node.getCaption()+" returned "+c.size()+" nodes: ");

            Iterator it = c.iterator();

            while (it.hasNext()) {
                node = (PNode) it.next();
                System.out.println("\t"+node.getCaption()+" has "
                                   +node.getNeighbors(null).size()+" neighbors ");
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

    static class PNode extends BNodeAdapter {
        Random rand = new Random();
        ImageIcon icon = null;

        /**
         * constructor for starter random node.
         */
        PNode() {
            this(0, null);
        }

        /**
         * Construct a node for a node.
         * @param ref
         */
        PNode(int face, PNode ref) {
            caption = face_name[face % face_name.length];
            // Our "description" will be the filename for the image to load!
            description = face_file.format(face);
            reference = ref;
            System.out.println("created PNode:"+caption+" - "+description+" to ref "+ref);
        }
        
		public Object getRepresentation() {
			if (icon == null)	{
				icon = new ImageIcon(description);
			}
			return icon.getImage();
		}

        /**
         * Create a set of random neighbors. Currently we ignore type.
         *
         * @see com.vshake.crystal.BNode#getNeighbors(com.vshake.crystal.LinkType)
         */
        public Collection getNeighbors(LinkType lt) {
            super.getNeighbors(lt);

            if (generateNewNodes && (neighbors.size() < 2)) {
                while (rand.nextDouble() < rfactor) {
                    // We want to randomly either create neighbors, or walk backwards through the references looking for neighbors.
                    if ((reference != null) && (rand.nextDouble() < nfactor)
                            && !reference.getNeighbors(lt).isEmpty()) {
                        // Randomly select from a pre-existing node.
                        Collection cnei = reference.getNeighbors(lt);
                        PNode[] anei = new PNode[cnei.size()];
                        anei = (PNode[]) cnei.toArray(anei);

                        int i = rand.nextInt(cnei.size());
                        int j = rand.nextInt(cnei.size());

                        Collection ncnei = anei[i].getNeighbors(lt);
                        addNeighbor(anei[i]);
                        anei[j].addNeighbor(anei[i]);
                    } else { // create a new node

                        int i = rand.nextInt(100);
                        addNeighbor(new PNode(i, this));
                    }
                }
            }

            return neighbors;
        }
    }
}
