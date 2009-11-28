package org.mati.geotech.model;

import java.util.Vector;

public abstract class RectViewModel {
	
	private Rect _displayRect = new Rect(0,0,30,30);
	private Rect _worldRect = new Rect(-180,-90,360,180);
	
	public abstract Vector<Rect> getRects();
	public double getWidth() {return _worldRect.getWidth();}
	public double getHeight() {return _worldRect.getHeight();}
	public Rect getDisplayRect() { return _displayRect; }
	public Rect getWorldRect() { return _worldRect; }

}
