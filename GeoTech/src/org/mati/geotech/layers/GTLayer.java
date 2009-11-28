package org.mati.geotech.layers;


import javax.media.opengl.GL;

import org.mati.geotech.gui.ViewPort;
import org.mati.geotech.model.ResManager;


public class GTLayer {
	ResManager _res;
	ViewPort _vp;
	
	public GTLayer(ResManager res, ViewPort vp) {
		_res = res;
		_vp = vp;
	}
	
	private int _sw=1; // Screen width
	private int _sh=1; // Screen height
	
	public void paint(GL gl) {
		/*
		GL gl = gla.getGL();
		
		double x1,x2,y1,y2;
		x1=-_vp.getMapWidth()/2;
		//x2=cam.getWidth()/2;
		x2=200;
		y1=-_cam.getHeight()/2;
		y2=_cam.getHeight()/2;
		
		gl.glPushMatrix();
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		GLU glu = new GLU();
		glu.gluPerspective(_cam.getFOV(),_cam.getAspect(),0.0001,1000);
		glu.gluLookAt(  0,  0,  -_cam.getZ(), 
				 		0,  0, 0, 
				 		0, -1, 0);
		
		gl.glColor3d(0,1,0);
		gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3d( x1, y1, 0);
			gl.glVertex3d( x1, y2, 0);
			gl.glVertex3d( x2, y2, 0);
			gl.glVertex3d( x2, y1, 0);
		gl.glEnd();	
		gl.glPopMatrix();
		*/	
	}
	
	public void setSize(int w, int h) {	_sw = w; _sh = h; }
	public int getScreenWidth() {return _sw; }
	public int getScreenHeight() {return _sh; }
}
