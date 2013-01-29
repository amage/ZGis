package org.mati.geotech.gui;

import java.awt.geom.Point2D;

import org.mati.geotech.model.Rect;

import com.jhlabs.map.MapMath;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;


public class ViewPort {
	private double _vwy=-30; //Широта
	private double _vwx=60; // Долгота
	private double _h=6;
	private double _fov= 90;
	private double _aspect = 1.0;
	
	final double _minZ = 0;
	final double _maxZ = 200;
	
	private double _mouseInWorldX=0;
	private double _mouseInWorldY=0;
	
	private Projection _proj;
	private Rect _screenRect = new Rect();
	
	public ViewPort() {
		_proj= ProjectionFactory.fromPROJ4Specification(new String[]{ 
				"+proj=merc","+a=6378137","+b=6378137","+lat_ts=0.0","+lon_0=0.0", 
				"+x_0=0.0", "+y_0=0", "+k=1.0","+no_defs"}
			);
	}
	
	public double getMouseMapLon() { 
		return worldToMapX(_mouseInWorldX); 
	}
	
	public double getMouseMapLat() { 
		return worldToMapY(_mouseInWorldY); 
	}

	public void setMousePos(double x, double y) {
		_mouseInWorldX = scrToWorldX(x);
		_mouseInWorldY = scrToWorldY(y);
	}

	
	public Projection getProjection() {return _proj;}
	
	/**
	 * Получить широту
	 * @return градусы широты
	 */
	public double getLatitude() { return _vwy; }
	
	/**
	 * Получить долготу
	 * @return градусы долготы
	 */
	public double getLongitude() { return _vwx; }
	public double getZ() { return _h;}
	
	public double getViewWorldWidth() { 
		return _aspect<=1?
					_aspect*(Math.tan(getFOV()*Math.PI/360) * getZ())*2
				:
					_aspect*(Math.tan(getFOV()*Math.PI/360) * getZ())*2; 
	}
	
	public double getViewWorldHeight() { 
		return _aspect>=1?
					(Math.tan(getFOV()*Math.PI/360) * getZ())*2
				:
					(Math.tan(getFOV()*Math.PI/360) * getZ())*2; 
	}
	public double getFOV() {return _fov;}
	public void setAspect(double asp) {_aspect = asp;}
	public double getAspect() {return _aspect;}
	public void translateInMap(double dx, double dy, double dz) {
		_vwx+=dx; _vwy+=dy; _h+=dz;
		if(_h<=getMinZ()) _h = getMinZ();
		if(_h>=getMaxZ()) _h = getMaxZ();
	}

	public Rect getScreenRect() {return _screenRect; }
	public double getMinZ() { return _minZ; }
	public double getMaxZ() { return _maxZ; }
	
	// camera
	public double getViewWorldX() { return _vwx; }
	public double getViewWorldY() { return _vwy; }
	public void setViewWorldX(double x) { _vwx=x; }
	public void setViewWorldY(double y) { _vwy=y; }
	
	// screen <=> map translation
	public int mapToScrX(double mapX) { return worldToScrX(mapToWorldX(mapX)); }
	public int mapToScrY(double mapY) { return worldToScrY(mapToWorldY(mapY)); }
	public double scrToMapX(double scrX) { return worldToMapX(scrToWorldX(scrX)); }
	public double scrToMapY(double scrY) { return worldToMapY(scrToWorldY(scrY)); }
	

	public double worldToMapX(double worldX) {
		Point2D.Double pnt = _proj.projectInverse(MapMath.degToRad(worldX),0, new Point2D.Double());
		return MapMath.radToDeg(pnt.x);
	}
	public double worldToMapY(double worldY) {
		Point2D.Double pnt = _proj.projectInverse(0,MapMath.degToRad(worldY*2), new Point2D.Double());
		return -MapMath.radToDeg(pnt.y);
	}
	public double mapToWorldX(double worldX) {
		Point2D.Double pnt = _proj.project(MapMath.degToRad(worldX),0, new Point2D.Double());
		return MapMath.radToDeg(pnt.x);
	}
	public double mapToWorldY(double worldY) {
		Point2D.Double pnt = _proj.project(0,MapMath.degToRad(worldY), new Point2D.Double());
		return -MapMath.radToDeg(pnt.y)/2;		
	}
	
// --------------- World <==> Screen ---------------
	public int worldToScrX(double worldX) {
		double x = worldX - (getViewWorldX() - getViewWorldWidth() / 2);
		double scaleX = getScreenWidth() / getViewWorldWidth();
		return (int)(x*scaleX);
	}
	public int worldToScrY(double worldY) {
		double y = worldY - (getViewWorldY() - getViewWorldHeight() / 2);
		double scaleY = getScreenHeight() / getViewWorldHeight();
		return (int)(y*scaleY);
	}
	
	public double scrToWorldX(double scrX) { 
		double xsh = (getViewWorldX() - getViewWorldWidth() / 2);
		double scaleX = getScreenWidth() / getViewWorldWidth();
		return (scrX)/scaleX + xsh;
	}
	public double scrToWorldY(double scrY) {
		double ysh = (getViewWorldY() - getViewWorldHeight() / 2);
		double scaleY = getScreenHeight() / getViewWorldHeight();
		return (scrY)/scaleY + ysh;
	}	
	public double getScreenWidth() { return getScreenRect().getWidth();	}
	public double getScreenHeight() { return getScreenRect().getHeight();	}

	public Rect scrToMapRect(Rect rect) {
		return worldToMapRect(scrToWorldRect(rect));
	}

	public Rect scrToWorldRect(Rect rect) {
		Rect res =
			new Rect(
				scrToWorldX(rect.getX()),
				scrToWorldY(rect.getY()),
				scrToWorldX(rect.getX()+rect.getWidth())
					-scrToWorldX(rect.getX()),
				scrToWorldY(rect.getY()+rect.getHeight())
					-scrToWorldY(rect.getY())
			);
		
		return res;
	}
	
	public Rect worldToMapRect(Rect rect) {
		Rect res =
			new Rect(
				worldToMapX(rect.getX()),
				worldToMapY(rect.getY()+rect.getHeight()),
				worldToMapX(rect.getX()+rect.getWidth())-worldToMapX(rect.getX()),
				worldToMapY(rect.getY())-worldToMapY(rect.getY()+rect.getHeight())
			);
		
		return res;
	}	
}
