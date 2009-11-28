package org.mati.geotech.layers;

import java.util.LinkedList;

import javax.media.opengl.GL;

import org.mati.geotech.gui.ViewPort;
import org.mati.geotech.model.GeoObject;
import org.mati.geotech.model.GeoPoint;
import org.mati.geotech.model.Rect;
import org.mati.geotech.model.ResManager;


public class LineObjectLayer extends GTLayer {
	public LineObjectLayer(ResManager res, ViewPort vp) { super(res, vp); }
	
	@Override
	public void paint(GL gl) {
		gl.glDisable(GL.GL_LINE_STIPPLE);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LINE_SMOOTH);
	    gl.glPointSize(3);
	    gl.glLineWidth(3);
		//gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		Rect vr =
			new Rect(_vp.worldToMapX(_vp.getViewWorldX()-_vp.getViewWorldWidth()/2),
					_vp.worldToMapY(_vp.getViewWorldY()+_vp.getViewWorldHeight()/2),
					_vp.worldToMapX(_vp.getViewWorldX()+_vp.getViewWorldWidth()/2)-
						_vp.worldToMapX(_vp.getViewWorldX()-_vp.getViewWorldWidth()/2),
					_vp.worldToMapY(_vp.getViewWorldY()-_vp.getViewWorldHeight()/2)-
						_vp.worldToMapY(_vp.getViewWorldY()+_vp.getViewWorldHeight()/2));

		LinkedList<GeoObject> objs = _res.getObjects(vr);
		GeoObject newObj = _res.getNewObjectLines();
		
		for(GeoObject o : objs) {
			if(o.isLine()) {
				if(o!=null && o.getPoints().size()>1) {
					gl.glBegin(GL.GL_LINE_STRIP);
						gl.glColor3d(0, 1, 1);
						for(GeoPoint p: o.getPoints())
							 gl.glVertex2d(_vp.mapToWorldX(p.getLon()), _vp.mapToWorldY(p.getLat()));
					gl.glEnd();
				}
			}
		}
		
		if(newObj!=null && newObj.getPoints().size()>1) {
			gl.glBegin(GL.GL_LINE_STRIP);
				gl.glColor3d(0, 1, 1);
				for(GeoPoint p: newObj.getPoints())
					 gl.glVertex2d(_vp.mapToWorldX(p.getLon()), _vp.mapToWorldY(p.getLat()));
			gl.glEnd();
		} else if (newObj!=null && newObj.getPoints().size()==1) {
			gl.glBegin(GL.GL_POINT);
				gl.glColor3d(0, 1, 1);
				for(GeoPoint p: newObj.getPoints())
					gl.glVertex2d(_vp.mapToWorldX(p.getLon()), _vp.mapToWorldY(p.getLat()));
			gl.glEnd();			
		}
	}
	
}
