package org.mati.geotech.layers;


import java.awt.Font;

import javax.media.opengl.GL;

import org.mati.geotech.gui.ViewPort;
import org.mati.geotech.model.ResManager;

import com.sun.opengl.util.j2d.TextRenderer;


public class GUILayer extends AbstractMapLayer {

	TextRenderer textRender;
	public GUILayer(ResManager res, ViewPort vp) { 
		super(res, vp);
		textRender = new TextRenderer(new Font("SansSerif", Font.BOLD, 14));
	}

	@Override
	public void paint(GL gl) {
		textRender.beginRendering((int)viewPort.getScreenWidth(), (int)viewPort.getScreenHeight());
		textRender.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		textRender.draw("+", 
				viewPort.mapToScrX(viewPort.getViewWorldX())+(int)viewPort.getScreenWidth()/2, 
				viewPort.mapToScrY(viewPort.getViewWorldY())+(int)viewPort.getScreenHeight()/2);
		textRender.draw("lat: " + viewPort.getMouseMapLat(), (int)viewPort.getScreenWidth()-100, 18);
		textRender.draw("long: "+ viewPort.getMouseMapLon(), (int)viewPort.getScreenWidth()-100, 32);
		textRender.endRendering();
		
		viewPort.scrToMapX(0);
	}
}
