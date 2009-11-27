package model;

import java.util.HashMap;
import java.util.Vector;

public class GeoObject extends Rect{
	private HashMap<String, String> _props = new HashMap<String, String>();
	private boolean _showText=false;
	
	private Vector<GeoPoint> _points = new Vector<GeoPoint>();
	
	public boolean showText() {return _showText;}
	public void setShowText(boolean f) {_showText=f;}
	
	public GeoObject(double x, double y, double w, double h) { super(x, y, w, h); }
	
	public GeoObject() { super(0, 0, 0, 0); setX(0);setY(0); }
	
	public HashMap<String, String> getProps() { return _props; }
	
	public void updateRect() {
		setWidth(0);setHeight(0);
		if(getPoints()!=null && getPoints().size() > 1) {
			GeoPoint maxLon = getPoints().firstElement();
			GeoPoint maxLat = getPoints().firstElement();
			GeoPoint minLon = getPoints().firstElement();
			GeoPoint minLat = getPoints().firstElement();
			for(GeoPoint p:getPoints()) {
				if(p.getLat() > maxLat.getLat()) maxLat = p;
				if(p.getLon() > maxLon.getLon()) maxLon = p;
				if(p.getLat() < minLat.getLat()) minLat = p;
				if(p.getLon() < minLon.getLon()) minLon = p;
			}
			setX(minLon.getLon());
			setY(minLat.getLat());
			setWidth(maxLon.getLon()-getX());
			setHeight(maxLat.getLat()-getY());
		}
	}
	
	public double getMapX() {
		try {
			return Double.parseDouble(_props.get("lon"));
		} catch(Exception e) { return super.getX(); }
	}
	public double getMapY() {
		try {
			return Double.parseDouble(_props.get("lat"));
		} catch(Exception e) { return super.getY(); }
	}
	
	
	public String getName() { return _props.get("name"); }
	public void setStartLvl(int lvl) { _props.put("lvl",String.valueOf(lvl)); }
	public int getStartLvl() { return Integer.parseInt(_props.get("lvl")); }

	public int getType() { return Integer.parseInt(_props.get("type")); }

	public int getId() {return Integer.parseInt(_props.get("id")); }
	
	public boolean isLine() { if(_points!=null && _points.size() > 1) return true; else return false; }
	public Vector<GeoPoint> getPoints() { return _points; }
	
	@Override
	public double getX() { return getMapX(); }
	@Override
	public double getY() { return getMapY(); }
	@Override
	public void setX(double x) { _props.put("lon", String.valueOf(x)); }
	@Override
	public void setY(double y) { _props.put("lat", String.valueOf(y)); }
}
