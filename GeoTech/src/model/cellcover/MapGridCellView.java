package model.cellcover;

import model.Rect;


public class MapGridCellView extends Rect {
	
	MapGridCellView[] _subCells = new MapGridCellView[4];
	
	public MapGridCellView(double ax, double ay, double aw, double ah) {
		super(ax, ay, aw, ah);
	}
	
	public MapGridCellView() {
		super(0, 0, 0, 0);
	}

	private String _name = "test";
	
	public void setName(String name) {_name = name;}
	public String toString() {return _name;}
	
	public void setPos(double x, double y) { setX(x); setY(y); }
}