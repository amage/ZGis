package org.mati.geotech.model.rtree;

import java.util.EmptyStackException;
import java.util.Stack;
import java.util.Vector;

import org.mati.geotech.model.GeoObject;
import org.mati.geotech.model.Rect;


public class RTree {
	private RNode _root = new RNode();
	private int _minChildCount = 8;
	private int getMaxChildCount() {return _minChildCount*2-1;}
	
	public RNode getRoot() {return _root;}
	
	public synchronized void clean() {
		_root=new RNode();
		System.gc();
	}
	
	public void createTree(Vector<GeoObject> rects) {
		_root = new RNode();
	}
	
	public void addElement(GeoObject r) {
		if(_root.isLeaf()) _root.getRect().setSameGeometry(r);
		RNode newNode = new RNode();
		newNode.setGeoObject(r);

		Stack<RNode> path = new Stack<RNode>();
		path.add(_root);
		RNode node = chooseLeaf(r,_root,path);
		node.addChild(newNode);
		
		if(node.getChildCount()>getMaxChildCount())
			splitNode(node, path);
		
		RNode tmp;
		try {
			while(true) {
				tmp = path.pop();
				tmp.getRect().setSameGeometry(tmp.getRect().getBoundRectWith(r));			
			}
		}
		catch(EmptyStackException e) {
			
		}
	}
	
	private void splitNode(RNode node, Stack<RNode> path) {
		// System.out.println("splitNode()");
		int i1=0, i2=1,i1max=0,i2max=1;
		double aspendMax = 0;
		double aspend=0;
		
		Rect r1,r2;
		for(i1=1; i1 < node.getChildCount(); i1++) {
			r1 = node.getChildren().get(i1).getRect();
			for(i2=0; i2 < node.getChildCount(); i2++) {
				r2 = node.getChildren().get(i2).getRect();
				aspend = r1.getBoundRectWith(r2).getArea() - r1.getArea() - r2.getArea(); 
				if(aspend > aspendMax) {
					aspendMax = aspend;
					i1max=i1; i2max=i2;
				}
				
			}
		}
		// TODO: remove
		node.getChildren().get(i1max).flags[1]=1;
		node.getChildren().get(i2max).flags[1]=2;
//----------------------------------------------------------
		double d[] = new double[node.getChildCount()];
		
		RNode g1 = new RNode();
		RNode g2 = new RNode();
		g1.addChild(node.getChildren().get(i1max));
		g2.addChild(node.getChildren().get(i2max));
		d[i1max]=-1;
		d[i2max]=-1;

		double d1,d2;
		
		int i=0;
		for(RNode n : node.getChildren()) {
			if(n!=node.getChildren().get(i1max) && n!=node.getChildren().get(i2max)) {
				d1=n.getRect().getBoundRectWith(g1.getRect()).getArea();
				d2=n.getRect().getBoundRectWith(g2.getRect()).getArea();
				d[i]=Math.abs(d1-d2);
			}
			else
				d[i]=-1;
			i++;
		}

		int j=0;
		int jMax=0;
		double dMax=0;
		for(i=0; i<node.getChildCount(); i++) {
			dMax=0;
			jMax=0;
			for(j=0;j<node.getChildCount();j++) {
				if(dMax < d[j]) {
					dMax=d[j];
					jMax=j;
				}
			}
			
			d[jMax]=-1;
			d1=node.getChildren().get(jMax).getRect().getBoundRectWith(g1.getRect()).getArea();
			d2=node.getChildren().get(jMax).getRect().getBoundRectWith(g2.getRect()).getArea();
			
			if(d1>d2) 
				g2.addChild(node.getChildren().get(jMax));
			else
				g1.addChild(node.getChildren().get(jMax));
			if(g1.getChildCount() >= _minChildCount || g2.getChildCount() >= _minChildCount )
				break;
		}

		if(g1.getChildCount() >= _minChildCount || g2.getChildCount() >= _minChildCount ) {
			RNode g;
			int f;
			if(g1.getChildCount()>=g2.getChildCount()){ f=2; g=g2;} else {f=1;g=g1;}
			for(i=0; i<node.getChildCount(); i++) {
				if(d[i]!=-1) {
					g.addChild(node.getChildren().get(i));
					g.flags[0]=f;
				}
			}
		}
		
		g1.flags[0]=1;
		g2.flags[0]=2;
		
		if(node!=_root) {
			RNode perent = path.pop();
			perent.getChildren().remove(node);
			perent.addChild(g1);
			perent.addChild(g2);
			if(perent.getChildCount()>getMaxChildCount()) {
				splitNode(perent, path);
				return;
			}
		} else {
			_root = new RNode();
			_root.addChild(g1);
			_root.addChild(g2);
		}
		node=null;
		_root.flags[0]=-1;
	}
	
	private RNode chooseLeaf(Rect newElement, RNode node, Stack<RNode> path) {
		if((node==_root && node.isLeaf())||node.getChildren().get(0).isLeaf()) return node;
		else path.add(node);
		
		Vector<RNode> snodes = node.getChildren();
		
		int iMin = 0;
		Rect minRect = snodes.get(0).getRect().getBoundRectWith(newElement);
		for(int i = 1; i < snodes.size(); i++) {
			Rect nodeRect=snodes.get(i).getRect();
			if(nodeRect.getBoundRectWith(newElement).getArea() < minRect.getArea()) {
				minRect=nodeRect.getBoundRectWith(newElement);
				iMin=i;
			}
		}
		//path.add(snodes.get(iMin));
		return chooseLeaf(newElement, snodes.get(iMin),path);
	}
	
	public Vector<GeoObject> select(Rect aWindow) {
		Vector<GeoObject> result = new Vector<GeoObject>();
		select(aWindow,_root,result);
		return result;
	}
	
	synchronized private void select(Rect aW, RNode aNode, Vector<GeoObject> aR) {
		for(RNode n:aNode.getChildren()) {
			if(n.getRect().haveCross(aW)) {
				if(n.isLeaf()) 
					aR.add(n.getRect());
				else 
					select(aW, n, aR);
			}
		}
	}
}
