/*
 * Created on Aug 5, 2003
 *
 */
package com.vshake.crystal;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.util.*;

import javax.swing.*;


/**
 * CrystalBall implementation
 *
 * @author fred
 */
public class CrystalBall extends JPanel implements Runnable {
    //// Management
    Object viewLock = new Object(); // our own internal synchronization for views
    private Image offScreen = null;
    private BNode curnode = null;
    private boolean animating = false; // if TRUE, use cn_cur instead of curnode
    CNode cn_from;
    CNode cn_to;
    CNode cn_cur; // animation changes here!!!
    private int ply = 3; // how many levels nodes will be scanned for display
    private HashMap nodes = new HashMap(); // cache --- current nodes mapped into display.
    private HashMap links = new HashMap(); // cache -- current links mapped into display.
    private Point2D.Double pcenter = new Point2D.Double(); // Centeral focus of node -- node coordinates.
    private double zoom = 0.02; // zoom factor
    double meanDistance = 150; // mean distance between nodes.
    double ballRadius = 210.0; // Screen coordinate ball radius
    private Random rand = new Random(); // this is for the cheap "testellation" we will use to assign points.
    private Object animLock = new Object();
    static int frameRate = 15; // Frames per second
    static double framesToDo = 10; // number of frames in animation.
	
	// Mininum and maximum size for animated icons
	static int minXSize = 10;
	static int minYSize = 10;
	static int maxXSize = 100;
	static int maxYSize = 80;
	

    /**
     * Create the initial Crystal Ball.
     */
    public CrystalBall() {
        this.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    cb_EventMouseClicked(e);
                }
            });

        new Thread(this).start();
    }

    /**
     * Create a CrystalBall with a starter node.
     *
     * @param starter starter node.
     */
    public CrystalBall(BNode starter) {
        this();
        setCurrentBNode(starter);
    }

    void cb_EventMouseClicked(MouseEvent e) {
        //System.out.println(e);
        moveToNewNodeClick(e.getPoint());
    }

    /**
     * Find the node the user clicked, and shift center to it.
     *
     * @param p
     */
    void moveToNewNodeClick(Point p) {
        Iterator it = nodes.values().iterator();

        while (it.hasNext()) {
            CNode cn = (CNode) it.next();

            if (cn.apt.contains(p)) {
                // we found it.
                animateToCNode(cn);

                return;
            }
        }
    }

    void clearCache() {
        nodes.clear();
        links.clear();
    }

    /**
     * Set the given node as the center.
     *
     * @param bn
     */
    public void setCurrentBNode(BNode bn) {
        curnode = bn;
        clearCache();
    }

    private void readyAnim(CNode from, CNode to) {
        cn_from = from;
        cn_to = to;
        animating = true;
    }

    /**
     * Animate move to given node. If node is not in cache, jump to it.
     *
     * @param cn
     */
    public void animateToCNode(CNode cn) {
        readyAnim((CNode) nodes.get(curnode), cn);

        synchronized (animLock) {
            animLock.notify();
        }

        //System.out.println("aToCN: called");
        //curnode = cn.bn;
        //pcenter.x = cn.pt.x;
        //pcenter.y = cn.pt.y;
        //repaint();
        //System.out.println("aToCN: pcenter "+pcenter+" cn "+cn);
    }

    /**
     * Tesstelate from current node out to ply levels away.
     */
    void testellate() {
        if (nodes.isEmpty()) { // first time?

            CNode cn = new CNode(curnode);
            nodes.put(curnode, cn);
            assignCoords(cn, null, 0.0);
        }

        testellate(curnode, ply, null);
    }

	private LinkedList nodeSpace = new LinkedList(); // list of rectangles.
	 
	/**
	 *  Check to see if point is available or not.
	 * @param p
	 * @return true if point is availble
	 */
	private boolean isPointAvailable(Point2D p) {
		// We will use the meanDistance variable to assign a "radius"
		Iterator it = nodeSpace.iterator();
		while (it.hasNext()) {
			Rectangle2D rect = (Rectangle2D) it.next();
			if (rect.contains(p))
				return false;
		}
		return true;
	}
	
	private void addPointToNodeSpace(Point2D p) {
		Rectangle2D.Double rect = new Rectangle2D.Double();
		rect.x = p.getX() - meanDistance;
		rect.y = p.getY() - meanDistance;
		rect.width = rect.height = meanDistance * 2;
		nodeSpace.add(rect);
	}

    /**
     * Assign coodinates to cn using refcn as a reference.
     *
     * @param cn - point to receive assignment.
     * @param refcn
     * @param hint - varies from 0 to 1 exclusive -- hint for placement of point.
     */
    void assignCoords(CNode cn, CNode refcn, double hint) {
        
        // Establish a reference point!
        Point2D.Double rp = 
        	new Point2D.Double((refcn != null) ? refcn.pt.x : 0.0, 
        										(refcn != null) ? refcn.pt.y : 0.0);
        double PI2 = Math.PI * 2.0; 
        double r = PI2 * hint;
        double dr = PI2 * 0.20; // we bounce around the circle by 20% of an arc on each try.
        do {
			double g = meanDistance + ((rand.nextGaussian() * meanDistance * (r / PI2)) / 2.5);
			cn.pt.x = (Math.sin(r) * g) + rp.x;
			cn.pt.y = (Math.cos(r) * g) + rp.y;
        	r += dr;
        } while (!isPointAvailable(cn.pt));
		
		addPointToNodeSpace(cn.pt);

        if (cn.pt.x != cn.pt.x) {
            System.out.println("aC:NaN: cn=" + cn + " ref=" + refcn + " hint=" +
                hint);
        }
    }

    /**
     * Recursive testellation from given node.
     *
     * @param bn already in cache
     * @param ply
     * @param lt DOCUMENT ME!
     */
    void testellate(BNode bn, int ply, LinkType lt) {
        Collection nei = bn.getNeighbors(lt);
        Iterator it = nei.iterator();
        int ii = 0;
        int sz = nei.size();

        while (it.hasNext()) {
            BNode nbn = (BNode) it.next();

            // are we in yet?
            if (nodes.get(nbn) == null) {
                CNode cn = new CNode(nbn);
                nodes.put(nbn, cn);

                CLink link = new CLink((CNode) nodes.get(bn), cn);
                links.put(link, link);

                // Now we must assign a point to this.
                assignCoords(cn, (CNode) nodes.get(bn),
                    (double) ii / (double) sz);
                ++ii;

                // now do the same for this node
                if (ply > 0) {
                    testellate(nbn, ply - 1, lt);
                }
            }
        }
    }

    /**
     * Get the current BNode.
     *
     * @return
     */
    public BNode getCurrentBNode() {
        return curnode;
    }

    boolean needNewOffscreeenBuffer() {
        return offScreen == null;
    }

    public void paint(Graphics g) {
        //super.paint(g);
        //System.out.println("View paint for " + this);
        synchronized (viewLock) {
            if (needNewOffscreeenBuffer()) {
                offScreen = createImage(getSize().width, getSize().height);
            }

            // Set up and clean buffer
            Graphics2D goff = (Graphics2D) offScreen.getGraphics();

            goff.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);

            goff.setClip(0, 0, getSize().width, getSize().height);
            goff.clearRect(0, 0, getSize().width, getSize().height);

            renderNodes(goff);
            g.drawImage(offScreen, 0, 0, Color.white, this);
        }
    }

    /**
     * Convert to crystal metric.
     *
     * @param x DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static public double crystal(double x) {
        return 1.0 - (1 / Math.pow(2.0, x));
    }

    public void renderNodes(Graphics2D g) {
        double width = getSize().width;
        double height = getSize().height;
        double w2 = width / 2.0;
        double h2 = height / 2.0;

        // Caculate locations
        testellate();

        Iterator it = nodes.values().iterator();

        while (it.hasNext()) {
            CNode cn = (CNode) it.next();
            Object rep = cn.bn.getRepresentation();
            cn.temp_shape = (rep instanceof Shape) ? (Shape) rep : null;
            cn.temp_image = (rep instanceof Image) ? (Image) rep : null;

            if ((rep != null) && (cn.temp_shape == null) &&
                    (cn.temp_image == null)) {
                throw new ClassCastException();
            }

            double pwidth = ((cn.temp_shape != null)
                ? cn.temp_shape.getBounds2D().getWidth()
                : ((cn.temp_image != null) ? cn.temp_image.getWidth(null) : 0.0)) * zoom;
            double pheight = ((cn.temp_shape != null)
                ? cn.temp_shape.getBounds2D().getHeight()
                : ((cn.temp_image != null) ? cn.temp_image.getHeight(null) : 0.0)) * zoom;
            double pw2 = pwidth / 2.0;
            double ph2 = pheight / 2.0;
            double px = ((cn.pt.x - pcenter.x) * zoom) - pw2;
            double py = ((cn.pt.y - pcenter.y) * zoom) - ph2;
            double px2 = px + pw2 + pwidth;
            double py2 = py + ph2 + pheight;
            double rad = Math.sqrt((px * px) + (py * py));
            double rad2 = Math.sqrt((px2 * px2) + (py2 * py2));
            double trad = crystal(rad) * ballRadius;
            double trad2 = crystal(rad2) * ballRadius;

            cn.apt.x = (rad > 0.0000001) ? ((px / rad * trad) + w2) : w2;
            cn.apt.y = (rad > 0.0000001) ? ((py / rad * trad) + h2) : h2;

            cn.apt.width = ((rad2 > 0.0000001) ? ((px2 / rad2 * trad2) + w2) : w2) -
                cn.apt.x;
            cn.apt.height = ((rad2 > 0.0000001) ? ((py2 / rad2 * trad2) + h2) : h2) -
                cn.apt.y;
			if (cn.apt.height < 0.0) {
				//System.out.println("negH: cn = " + cn + " trad=" + pwidth + " ph=" + pheight);
				// TODO Improve sizing of images.
				cn.apt.height = -cn.apt.height;				
			}
			
			if (cn.apt.width < 10) cn.apt.width = 10;
			if (cn.apt.height < 10) cn.apt.height = 10;
			if (cn.apt.width > maxXSize) {
				cn.apt.x += (cn.apt.width - maxXSize) / 2;
				cn.apt.width = maxXSize;
			}
			if (cn.apt.height > maxYSize) {
				cn.apt.y += (cn.apt.height - maxYSize) / 2;
				cn.apt.height = maxYSize;
			}
        }

        // Render links
        it = links.values().iterator();

        while (it.hasNext()) {
            CLink cl = (CLink) it.next();
            g.drawLine((int) cl.cn1.cx(), (int) cl.cn1.cy(), (int) cl.cn2.cx(),
                (int) cl.cn2.cy());
        }

        // Render nodes
        it = nodes.values().iterator();

        while (it.hasNext()) {
            CNode cn = (CNode) it.next();

            if (cn.isVisual()) {
                if (cn.temp_image != null) {
                    g.drawImage(cn.temp_image, (int) cn.apt.x, (int) cn.apt.y,
                        (int) (cn.apt.x + cn.apt.width),
                        (int) (cn.apt.y + cn.apt.height), 0, 0,
                        cn.temp_image.getWidth(null),
                        cn.temp_image.getHeight(null), Color.white, null);
                }

                cn.clearVisual();
            } else {
                g.setColor(Color.yellow);
                g.fillRoundRect((int) cn.apt.x, (int) cn.apt.y,
                    (int) cn.apt.width, (int) cn.apt.height, 2, 2);

                g.setColor(Color.black);
                g.drawString(cn.bn.getCaption(), (float) cn.apt.x,
                    (float) (cn.apt.y + cn.apt.height));
            }
        }
        
        // Do caption for current node if not animating.
        if (!animating) {
        	CNode cn = (CNode) nodes.get(curnode);
        	String s = cn.bn.getCaption();
        	Rectangle2D srb = g.getFont().getStringBounds(s, 0, s.length(), g.getFontRenderContext());
        	LineMetrics lm = g.getFont().getLineMetrics(s, g.getFontRenderContext());
        	float base = lm.getAscent();
			float x = (float) (cn.apt.x + cn.apt.width / 2.0 - srb.getWidth() / 2.0);
			float y = (float) (cn.apt.y + cn.apt.height + srb.getHeight());
			g.setColor(Color.white);
			g.fill3DRect((int)x - 5, (int)(y-base-2), (int)srb.getWidth()+10, (int)srb.getHeight()+4, true);  
			g.setColor(Color.blue);
        	g.drawString(cn.bn.getCaption(), x, y);
        }
    }

    /**
     * Animation thread
     *
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while (true) {
            System.out.println("Anim: waiting...");

            synchronized (animLock) {
                try {
                    animLock.wait();
                } catch (InterruptedException ie) {
                }

                if (animating) {
                    System.out.println("Anim: running...");

                    long tinterval = 1000 / frameRate;
                    double dx = cn_to.pt.x - cn_from.pt.x;
                    double dy = cn_to.pt.y - cn_from.pt.y;

                    for (double f = 0.0; f <= framesToDo; ++f) {
                        synchronized (viewLock) {
                            double m = f / framesToDo;
                            pcenter.x = cn_from.pt.x + (m * dx);
                            pcenter.y = cn_from.pt.y + (m * dy);
                        }

                        repaint();

                        try {
                            Thread.sleep(tinterval);
                        } catch (InterruptedException ie) {
                        }
                    }

                    // Now set the new center.
                    curnode = cn_to.bn;
                    pcenter.x = cn_to.pt.x;
                    pcenter.y = cn_to.pt.y;
                    repaint();
                    animating = false;
                }
            }
        }
    }

    /**
     * Shadows BNode for graphics purposes.
     *
     * @author fred
     */
    class CNode {
        BNode bn;
        Point2D.Double pt = new Point2D.Double(); // virtual point
        Rectangle2D.Double apt = new Rectangle2D.Double(); // actual (temporary) location as calculated by graphics.
        Shape temp_shape; // Temporary shape for rendering (not carried btween paint events)
        Image temp_image; // Temporary image for rendering (not carried btween paint events)

        CNode(BNode bn) {
            this.bn = bn;
        }

        double cx() { // center x

            return apt.x + (apt.width / 2.0);
        }

        double cy() { // center y

            return apt.y + (apt.height / 2.0);
        }

        boolean isVisual() {
            return (temp_shape != null) || (temp_image != null);
        }

        void clearVisual() {
            temp_shape = null;
            temp_image = null;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            BNode other;

            if (obj instanceof CNode) {
                other = ((CNode) obj).bn;
            } else if (obj instanceof BNode) {
                other = (BNode) obj;
            } else {
                return false;
            }

            return bn.equals(other);
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return bn.hashCode();
        }

        public String toString() {
            return bn.toString() + "<" + pt + ", " + apt + ">";
        }
    }

    /**
     * Link 2 cnodes together.
     *
     * @author fred
     */
    class CLink {
        CNode cn1;
        CNode cn2;

        CLink(CNode cn1, CNode cn2) {
            this.cn1 = cn1;
            this.cn2 = cn2;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj) {
            if (obj instanceof CLink) {
                CLink o = (CLink) obj;

                // Nondirected graph
                return (cn1.equals(o.cn1) && cn2.equals(o.cn2)) ||
                (cn2.equals(o.cn1) && cn1.equals(o.cn2));
            } else {
                return false;
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return cn1.hashCode() ^ cn2.hashCode();
        }
    }
}
