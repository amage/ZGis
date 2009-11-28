package org.mati.geotech.model.cellcover;

import java.util.Vector;

import org.mati.geotech.model.Rect;
import org.mati.geotech.model.RectViewModel;


public class CellCoverModel extends RectViewModel implements CellCoverListener {
	private CellCover _cc = new CellCover();
	
	public CellCoverModel() {
		_cc.addListner(this);
	}
	
	public Vector<Rect> getRects() {		
		Vector<Rect> rects = new Vector<Rect>();
		for(Rect r:_cc.getCellsVector()) rects.add(r);
		
		return rects;
	}

	public CellCover getCC() {return _cc;}
	
	public void updateView(double scaleW, double scaleH) {
		_cc.setViewWindow(getDisplayRect().getX(), 
				getDisplayRect().getY(), 
				getDisplayRect().getWidth(), 
				getDisplayRect().getHeight(),
				scaleW,scaleH);
		
	}

	@Override
	public void gridSizeChanged(int n, int m) {
		System.out.println("new grid: "+n+"x"+m);		
	}

	@Override
	public void gridPositionChanged(double x, double y, double cw, double ch,
			int n, int m) {
		System.out.println("new grid pos: "+y+"x"+x);		
	}

	@Override
	public void levelChanged(int newLvl, int prevLvl) {
		System.out.println("new grid level: "+newLvl+" (was: "+prevLvl+")");
		
	}
	
}
