package layers;

import gui.ViewPort;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

import model.Rect;
import model.ResManager;
import model.cellcover.CellCover;
import model.cellcover.CellCoverListener;
import model.cellcover.MapGridCellView;

public class MapLayer extends GTLayer implements CellCoverListener {
	CellCover _cc = new CellCover();
	Properties _objViews = new Properties();
	MapGridCellView[][] _mapGrid=null;
	
	public MapLayer(ResManager res, ViewPort vp) {
		super(res, vp);
		_cc.addListner(_res);
		_cc.addListner(this);
		String fName = "views.cfg";
		try {
			_objViews.load(new FileInputStream(new File(fName)));
		} catch (IOException e) {
			try {
				_objViews.store(new FileOutputStream(new File(fName)), "Object view configuration");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void paint(GL gl) {
		try {
			updateMapGrid();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(_mapGrid!=null) {	
			gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
			for(int i=0; i < _cc.getCellCountH(); i++) {
				for(int j=0; j < _cc.getCellCountW(); j++) {
					if(_mapGrid[i][j].haveOverlap(new Rect(-180,-90,360,180)))
						drawCell(gl, _mapGrid[i][j]);
				}
			}
		}
	}
	
	private void updateMapGrid() {
		_cc.setViewWindow(_vp.getViewWorldX()-_vp.getViewWorldWidth()/2,
				_vp.getViewWorldY()-_vp.getViewWorldHeight()/2, _vp.getViewWorldWidth(), _vp.getViewWorldHeight(),
				getScreenWidth()/_vp.getViewWorldWidth(),getScreenHeight()/_vp.getViewWorldHeight());
	}

	private void drawCell(GL gl, MapGridCellView cell) {
		try {
			Texture t = getTexture(cell);
			t.enable();
			t.bind();
			TextureCoords tc = t.getImageTexCoords();
			gl.glBegin(GL.GL_QUADS);
				gl.glTexCoord2d(tc.left(), tc.top());
				gl.glVertex3d(cell.getX(), cell.getY(),0); 
			
				gl.glTexCoord2d(tc.right(), tc.top());
				gl.glVertex3d(cell.getX()+cell.getWidth(), cell.getY(),0);
			
				gl.glTexCoord2d(tc.right(), tc.bottom());
				gl.glVertex3d(cell.getX()+cell.getWidth(), cell.getY()+cell.getHeight(),0); 
			
				gl.glTexCoord2d(tc.left(), tc.bottom());
				gl.glVertex3d(cell.getX(), cell.getY()+cell.getHeight(),0); 
			gl.glEnd();
			t.disable();
		}
		catch (Exception e) {
			e.printStackTrace();
			gl.glDisable(GL.GL_TEXTURE);
		}
		boolean drawRects=false;
		if(drawRects) {
			gl.glColor3d(0.5,0,0);
			gl.glBegin(GL.GL_LINE_LOOP);
				gl.glVertex2d(cell.getX(), cell.getY());
				gl.glVertex2d(cell.getX(), cell.getY()+cell.getHeight());
				gl.glVertex2d(cell.getX()+cell.getWidth(), cell.getY()+cell.getHeight());
				gl.glVertex2d(cell.getX()+cell.getWidth(), cell.getY());			
			gl.glEnd();
		}
	}

	private Texture getTexture(MapGridCellView cell) throws Exception {
		return _res.getMapTexture(_res.makePathFor(cell));
	}

	@Override
	public void gridPositionChanged(double x, double y, double cw, double ch, int n, int m) { }
	@Override
	public void gridSizeChanged(int n, int m) {
		_mapGrid = _cc.getGridMartix();		
	}

	@Override
	public void levelChanged(int newLvl, int prevLvl) { }
	
	


}
