package org.mati.geotech.model.rtree;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.mati.geotech.model.GeoObject;
import org.mati.geotech.model.Rect;
import org.mati.geotech.model.RectViewModel;


public class RTreeModel extends RectViewModel {
	
	private RTree _rtree = new RTree();
	
	private Vector<Rect> _rects = new Vector<Rect>();
		
	public RTree getRTree() {return _rtree;}
	
	public Vector<Rect> getRects() {return _rects;}
	
	public void makeTest() {
		_rects.clear();
		_rtree.clean();
		
		GeoObject rects[] = 
		{
			new GeoObject(  0,   0,100,100),
			new GeoObject(120, 120,100,100),
			
			new GeoObject(700, 700,100,100),
			new GeoObject(900, 900,100,100),
			
			new GeoObject(200, 180,100,100),
//			new Rect(  0, 900,100,100),			
//			new Rect(200, 100,100,100),
//			new Rect(300, 100,100,100),
//			new Rect(400, 100,100,100),
			
//			new Rect(450, 450,100,100),
			new GeoObject(300, 300,100,100),
			
//			new Rect(300, 500,100,100),
//			new Rect(200, 300,100,100),
			new GeoObject(200, 500,100,100)
				
		};
		
		for(GeoObject r:rects) {
			add(r);
		}
	}
	
	public void add(GeoObject r) {
		_rects.add(r);
		_rtree.addElement(r);					
	}
	
	public void makeRandom(int aNOR) {
		_rects.clear();
		_rtree.clean();
		double w=100;
		double h=100;
		boolean canOverlap=true;
		
		boolean skip = false;
		while(aNOR > 0) {
			GeoObject r = new GeoObject(
					getWorldRect().getX() + Math.random()*(getWidth()-w), 
					getWorldRect().getY() + Math.random()*(getHeight()-h),
					Math.random()*(w-10)+10, Math.random()*(h-10)+10);
			
			if(!canOverlap) {
				skip=false;
				for(Rect cur : _rects) {
					if(r.haveCross(cur)) { skip = true; break; }
				}
			}
			if(!skip) {
				_rects.add(r);
				_rtree.addElement(r);
				aNOR--;
			}
		}
	}
	
	public DefaultMutableTreeNode makeJTree() {
		DefaultMutableTreeNode root;
		
		root = new DefaultMutableTreeNode(_rtree.getRoot());
		addJTreeNodes(root,_rtree.getRoot());
		return root;
	}

	private void addJTreeNodes(DefaultMutableTreeNode jtnode, RNode node) {
		for(RNode n: node.getChildren()) {
			if(!n.isLeaf()) {
				DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(node.getRect());
				jtnode.add(newChild);
				addJTreeNodes(newChild, n);
			}
			else {
				jtnode.add(new DefaultMutableTreeNode(n.getRect()));
			}
		}
	}
	
}
