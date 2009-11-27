package app;

import gui.FPSCounter;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.swing.JFrame;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;

public class TextureTextApp extends JFrame implements GLEventListener {
	private static final long serialVersionUID = -1440109786859976217L;
	
	Texture _t = null;
	private FPSCounter _fps;
	
	public static void main(String[] args) {
		TextureTextApp app = new TextureTextApp();
		app.go();
	}

	private void go() {
		init();
		setVisible(true);
	}

	private void init() {
		GLCapabilities capabilities = new GLCapabilities();
		capabilities.setHardwareAccelerated(true);
		GLCanvas canvas = new GLCanvas(capabilities);
		canvas.addGLEventListener(this);
		getContentPane().add(canvas);
		setSize(800, 600);
		Animator a = new Animator(canvas);
		a.start();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void display(GLAutoDrawable gla) {
		GL gl = gla.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		

		_t.enable();
		_t.bind();
		TextureCoords coords = _t.getImageTexCoords();

		for(int i = -1; i < 1; i++) {
		      gl.glBegin(GL.GL_QUADS);
		      gl.glTexCoord2f(coords.left(), coords.bottom());
		      gl.glVertex3f(i, 0, 0);
		      gl.glTexCoord2f(coords.right(), coords.bottom());
		      gl.glVertex3f(i+1, 0, 0);
		      gl.glTexCoord2f(coords.right(), coords.top());
		      gl.glVertex3f(i+1, 1, 0);
		      gl.glTexCoord2f(coords.left(), coords.top());
		      gl.glVertex3f(i, 1, 0);
		      gl.glEnd();			
		}
		_t.disable();
		_fps.draw();
	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		
		_fps = new FPSCounter(drawable,14);
		System.out.println(drawable.getChosenGLCapabilities());
		if(_t==null) {
			try {
				_t=TextureIO.newTexture(new File("C:/workspace/GeoTeck/bin/res/images/loading.png"),false);
			} catch (GLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}

}
