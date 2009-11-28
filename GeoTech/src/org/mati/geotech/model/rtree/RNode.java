package org.mati.geotech.model.rtree;

import java.util.Vector;

import org.mati.geotech.model.GeoObject;


public class RNode {
	private GeoObject _boundRect = new GeoObject();
	private Vector<RNode> _subNodes = new Vector<RNode>();
	
	public int flags[] = {0,0};
	
	public int getChildCount() {return _subNodes.size(); }
	
	public boolean isLeaf() { return _subNodes.size()==0?true:false;}
	
	public Vector<RNode> getChildren() {return _subNodes;}

	public GeoObject getRect() {return _boundRect; }
	
	public void addChild(RNode ch) {
		if(getChildCount()==0) _boundRect.setSameGeometry(ch.getRect());
		else
			_boundRect.setSameGeometry(_boundRect.getBoundRectWith(ch.getRect()));
		_subNodes.add(ch);
	}

	public void setGeoObject(GeoObject r) {
		_boundRect = r;
	}
}
