package gui;

import java.util.Vector;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import layers.GTLayer;
import layers.LineObjectLayer;
import layers.MapLayer;
import layers.ObjectsLayer;
import layers.TextLayer;
import layers.GUILayer;
import layers.GeoGridLayer;
import model.ResManager;
import model.ResManagerListener;

public class GTCanvas extends GLCanvas implements GLEventListener, ResManagerListener {
	private Vector<GTLayer> _layers = new Vector<GTLayer>();
	private ViewPort _viewPort;
	private ResManager _res;
	private FPSCounter _fps;
	private GLU _glu = new GLU();
	
	public void setResManager(ResManager rm) {_res=rm; }
	
	private static final long serialVersionUID = 6013571363486206484L;

	public GTCanvas() {
		addGLEventListener(this);

		_viewPort = new ViewPort();
		_res = new ResManager();
		
		_res.addListner(this);
		
		_layers.add(new MapLayer(_res,_viewPort));
		_layers.add(new GeoGridLayer(_res,_viewPort));
		_layers.add(new LineObjectLayer(_res,_viewPort));
		_layers.add(new ObjectsLayer(_res,_viewPort));
		_layers.add(new TextLayer(_res,_viewPort));
		_layers.add(new GUILayer(_res,_viewPort));
		
	}
	
	public ViewPort getViewPort() {return _viewPort;}
	
	@Override
	public void display(GLAutoDrawable gla) {
		long t0 = System.currentTimeMillis();
		GL gl = gla.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_ACCUM_BUFFER_BIT|GL.GL_STENCIL_BUFFER_BIT);
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		_glu.gluPerspective(_viewPort.getFOV(),_viewPort.getAspect(),_viewPort.getMinZ(),_viewPort.getMaxZ());
		_glu.gluLookAt( _viewPort.getViewWorldX(), _viewPort.getViewWorldY(),  -_viewPort.getZ(), 
						_viewPort.getViewWorldX(), _viewPort.getViewWorldY(),  0, 
				 		0, -1,  0);
		if(_res!=null) {
			for(GTLayer l: _layers) l.paint(gla.getGL());
		}
		long t1 = System.currentTimeMillis();
		displayFPS(gla,t0,t1);
	}

	private void displayFPS(GLAutoDrawable gla, long t0, long t1) {
		t1++; // prevent div by 0
		_fps.setMPF((t1-t0));
		_fps.draw();
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean arg1, boolean arg2) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		//GLCapabilities caps = drawable.getChosenGLCapabilities();
		try {
			_res.init();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		GL gl = drawable.getGL();
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		gl.glClearColor(0.1f, 0.2f, 0.1f, 1);
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		
		_fps = new FPSCounter(drawable,14);	
//		FPSAnimator a = new FPSAnimator(drawable,30);
//		a.start();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
		double ww=w;
		double hh=h;
		//GL gl = drawable.getGL();
		_viewPort.setAspect(ww/hh*2);
		_viewPort.getScreenRect().setGeometry(x, y, w, h);

		for(GTLayer l: _layers) l.setSize(w, h);
		repaint();
	}

	public ResManager getResManager() { return _res; }
	
	@Override
	public void stateChanged() { repaint(); }
}
