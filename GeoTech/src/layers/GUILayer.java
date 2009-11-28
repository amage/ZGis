package layers;


import java.awt.Font;

import javax.media.opengl.GL;

import org.mati.geotech.gui.ViewPort;
import org.mati.geotech.model.ResManager;

import com.sun.opengl.util.j2d.TextRenderer;


public class GUILayer extends GTLayer {

	TextRenderer _textRender;
	public GUILayer(ResManager res, ViewPort vp) { 
		super(res, vp);
		_textRender = new TextRenderer(new Font("SansSerif", Font.BOLD, 14));
	}

	@Override
	public void paint(GL gl) {
		_textRender.beginRendering((int)_vp.getScreenWidth(), (int)_vp.getScreenHeight());
		_textRender.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		_textRender.draw("+", 
				_vp.mapToScrX(_vp.getViewWorldX())+(int)_vp.getScreenWidth()/2, 
				_vp.mapToScrY(_vp.getViewWorldY())+(int)_vp.getScreenHeight()/2);
		_textRender.draw("lat: " + _vp.getMouseMapLat(), (int)_vp.getScreenWidth()-100, 18);
		_textRender.draw("long: "+ _vp.getMouseMapLon(), (int)_vp.getScreenWidth()-100, 32);
		_textRender.endRendering();
		
		_vp.scrToMapX(0);
	}
}
