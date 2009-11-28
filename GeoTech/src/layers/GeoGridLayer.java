package layers;


import java.awt.geom.Point2D;

import javax.media.opengl.GL;

import org.mati.geotech.gui.ViewPort;
import org.mati.geotech.model.ResManager;

import com.jhlabs.map.MapMath;
import com.jhlabs.map.proj.Projection;


public class GeoGridLayer extends GTLayer {

	public GeoGridLayer(ResManager res, ViewPort vp) {
		super(res, vp);
	}
	
	@Override
	public void paint(GL gl) {
		Projection pr = _vp.getProjection();
		double step = MapMath.degToRad(10);
		gl.glEnable(GL.GL_LINE_STIPPLE);
		gl.glDisable(GL.GL_LINE_SMOOTH);
		gl.glLineStipple(1,(short) 0x000F);
		gl.glLineWidth(0.01f);
		gl.glBegin(GL.GL_LINES);
			gl.glDisable(GL.GL_LIGHTING);
			gl.glDisable(GL.GL_TEXTURE_2D);
			gl.glColor4d(0,0,0,0.5);
			for(double lambda = pr.getMinLongitude(); lambda <= pr.getMaxLongitude(); lambda+=step) {
				for(double phi = pr.getMinLatitude(); phi < pr.getMaxLatitude(); phi+=step) {	
					Point2D.Double ptn0 = pr.project(lambda, phi,new Point2D.Double());
					Point2D.Double ptn1 = pr.project(lambda+ MapMath.degToRad(10), phi + step,new Point2D.Double());
					gl.glVertex2d(MapMath.radToDeg(ptn0.x), MapMath.radToDeg(ptn0.y)/2);
					gl.glVertex2d(MapMath.radToDeg(ptn0.x), MapMath.radToDeg(ptn1.y)/2);
					
					gl.glVertex2d(MapMath.radToDeg(ptn0.x), MapMath.radToDeg(ptn0.y)/2);
					gl.glVertex2d(MapMath.radToDeg(ptn1.x), MapMath.radToDeg(ptn0.y)/2);
				}			
			}
		gl.glEnd();
		
		/*
		for(double lambda = -180; lambda <= 180; lambda+=10) {
			Point2D.Double p0 = pr.project(lambda, pr.getMaxLatitude(), new Point2D.Double());
			Point2D.Double p1 = pr.project(lambda, pr.getMinLatitude(), new Point2D.Double());
			gl.glBegin(GL.GL_LINES);
				gl.glVertex2d(p0.x,p0.y);
				gl.glVertex2d(p1.x,p1.y);
			gl.glEnd();			
		}
		*/
	}

}
