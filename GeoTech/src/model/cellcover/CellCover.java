package model.cellcover;

import java.util.Vector;
import model.Rect;

public class CellCover {
	// TODO: put that in config
	private static final int _maxLvl = 18;
	
	private Rect _worldRect = new Rect(-180,-90,360,180);
	
	private Rect _viewWindow = new Rect(0,0,0,0);
	private MapGridCellView[][] _mapGrid;
	
	private int _maxgw = 1;
	private int _maxgh = 1;
	
	private double _scaleW = 1;
//	private double _scaleH = 1;
	
	private int _texSizeW = 256;
//	private int _texSizeH = 256;
	
	private double _cellMapSize = 64;
	private int _lvl=0;

	// Interaction stuff
	private Vector<CellCoverListener> _cci = new Vector<CellCoverListener>();
	public void removeListner(CellCoverListener cci) { _cci.remove(cci); }
	public void addListner(CellCoverListener cci) {_cci.add(cci); }

	
	public CellCover() {
		_mapGrid = new MapGridCellView[_maxgh][_maxgw];
	}
	
	private void createNewGrid(int n, int m) {		
		_maxgh=n; _maxgw=m;
		_mapGrid = new MapGridCellView[n][m];
		for(int i=0; i < _maxgh; i++) {
			for(int j=0; j < _maxgw; j++) {
				_mapGrid[i][j]=new MapGridCellView();		
			}
		}
		for(CellCoverListener l: _cci) l.gridSizeChanged(n, m);
	}
	
	public void setViewWindow(double x, double y, double w, double h, double scaleW, double scaleH) {
		_viewWindow.setX(x);
		_viewWindow.setY(y);
		_viewWindow.setWidth(w);
		_viewWindow.setHeight(h);
		
		
		//System.out.println("scale: "+scaleW+"x"+scaleH);
		//System.out.println("size: "+w*scaleW+"x"+h*scaleH);
		
		_scaleW=scaleW;
		//_scaleH=scaleH;

		int lastLvl = _lvl;
		_lvl = (int)Math.ceil(Math.log(_worldRect.getWidth()/(_texSizeW/_scaleW))/Math.log(2));
		if(_lvl < 2) _lvl=2;
		if(_lvl > _maxLvl) _lvl = _maxLvl;
		
		// System.out.println("lvl: "+lvl);
		
		_cellMapSize = _worldRect.getWidth()/(Math.pow(2, _lvl));
		
		//System.out.println(w+"x"+h+" "+_cellMapSize+" "+_cellMapSize/2+" "+_texSizeW+" "+_scaleW);
		int n = (int) Math.ceil(h/_cellMapSize)*2+1;
		int m = (int) Math.ceil(w/_cellMapSize)+1;
		
		if(n!=_maxgh || m!=_maxgw) {
			createNewGrid(n, m);
			updateMapGrid(true);
		}
		else
			updateMapGrid(false);
		
		if(lastLvl!=_lvl) {
			for(CellCoverListener l: _cci) l.levelChanged(_lvl, lastLvl);
		}
	}

	private double _gx=0;
	private double _gy=0;
	private double _gsw=0;
	private double _gsh=0;
	
	private void updateMapGrid(boolean force) {		
		double cellW = _cellMapSize;
		double cellH = _cellMapSize/2;
		
		double gx=Math.floor(_viewWindow.getX()/cellW)*cellW;
		double gy=Math.floor(_viewWindow.getY()/cellH)*cellH;
		
		if(!( (_gsw==cellW)&&(_gsh==cellH)&&(_gx==gx)&&(_gy==gy)&&(!force))) 
		{
			_gx=gx; _gy=gy;
			_gsw=cellW; _gsh=cellH;
						
			for(int i=0; i < _maxgh && i < _maxgh; i++) {
				for(int j=0; j < _maxgw && j < _maxgw; j++) {
					_mapGrid[i][j].setPos(
							j*cellW+gx, 
							i*cellH+gy);
					_mapGrid[i][j].setWidth(cellW);
					_mapGrid[i][j].setHeight(cellH);
				}
			}
			for(CellCoverListener l: _cci) l.gridPositionChanged(gx, gy, cellW, cellH, _maxgw, _maxgh);
		}
	}
	
	public Vector<Rect> getCellsVector() { 
		Vector<Rect> cells = new Vector<Rect>();
		for(int i=0; i < getCellCountH(); i++) {
			for(int j=0; j < getCellCountW(); j++) {
				cells.add(_mapGrid[i][j]);		
			}
		}
		return cells; 
	}
	
	public MapGridCellView[][] getGridMartix() { return _mapGrid; }
	public int getCellCountW() {return _maxgw;}
	public int getCellCountH() {return _maxgh;}

	public int getLevel() { return _lvl; }
}
