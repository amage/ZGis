package org.mati.geotech.gui.method;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import org.mati.geotech.model.*;
import org.mati.geotech.model.rtree.RNode;
import org.mati.geotech.model.rtree.RTree;
import org.mati.geotech.model.rtree.RTreeModel;


public class RTreeDrawPanel extends DrawPanel {
	private boolean _bDrawAll = true;
	private boolean _bDrawTree = true;
	
	public void setDrawAll(boolean f) {_bDrawAll = f;}
	public boolean getDrawAll() {return _bDrawAll;}
	
	private static final long serialVersionUID = -950718375757439961L;

	private int maxRectCount=39;
	
	public RTreeDrawPanel() {
		super();
		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				getRTModel().makeRandom(maxRectCount);
				//_model.add(_model.getDisplayRect());
				repaint();
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
		});
		
	}

	private RTreeModel getRTModel() {
		return (RTreeModel)getModel();
	}	
	public void paintRects(Graphics g) {
		updateScale();
		
		if(_bDrawAll && getRTModel()!=null){
			g.setColor(Color.lightGray);
			for(Rect r:getRTModel().getRects()) {
				paintRect(g, r);	
			}
		}

		
		// Draw r-tree
		if(_bDrawTree) {
			RTree rt = getRTModel().getRTree();
			RNode node = rt.getRoot();
			drawRTree(node,g);
		}


		Vector<GeoObject> rs = getRTModel().getRTree().select(getRTModel().getDisplayRect());
		for(Rect r: rs) {
			g.setColor(Color.black);
			paintRect(g, r);
		}
	
// Look window
		g.setColor(Color.red);
		paintRect(g, getModel().getDisplayRect());
		g.drawString("Screen", 
				(int)mapToScrX(getModel().getDisplayRect().getX())+10,
				(int)mapToScrY(getModel().getDisplayRect().getY())+10);
	}
	
	private void drawRTree(RNode node, Graphics g) {
		if(node.isLeaf()) 
			if(node.flags[1]==1) {
				g.setColor(Color.red);
			}
			else if(node.flags[1]==2) {
				g.setColor(Color.blue);
			}			
			else {
				g.setColor(Color.green);
			}
		else {
			if(node.flags[0]==1)
				g.setColor(Color.red);
			else if(node.flags[0]==2)
				g.setColor(Color.blue);
			else if(node.flags[0]==-1) {
				//System.out.println("flag[0]: "+node.flags[0]);
			}
			else {
				g.setColor(Color.gray);
				//System.out.println("flag[0]: "+node.flags[0]);
			}
		}
		
		if((_bDrawAll && node.isLeaf()) || !node.isLeaf()) {
			Rect r = node.getRect();
			paintRect(g, r);
		}
		for(RNode cnode : node.getChildren()) drawRTree(cnode, g);
	}
}
