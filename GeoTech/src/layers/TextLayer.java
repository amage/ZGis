package layers;


import java.awt.Font;

import javax.media.opengl.GL;

import org.mati.geotech.gui.ViewPort;
import org.mati.geotech.model.Rect;
import org.mati.geotech.model.ResManager;

import com.sun.opengl.util.j2d.TextRenderer;


public class TextLayer extends GTLayer {
	TextRenderer _textRender;
	TextRenderer _textRenderBack;
	
	public TextLayer(ResManager res, ViewPort vp) {
		super(res, vp);
		_textRender = new TextRenderer(new Font("SansSerif", Font.BOLD, 14));
		_textRenderBack = new TextRenderer(new Font("SansSerif", Font.BOLD, 14));
	}

	protected int mapToScrX(double mapX, ViewPort vp) {
		double x = mapX - (vp.getViewWorldX() - vp.getViewWorldWidth() / 2);
		double scaleX = getScreenWidth() / vp.getViewWorldWidth();
		return (int)(x*scaleX);
	}

	protected int mapToScrY(double mapY, ViewPort vp) {
		double y = mapY - (vp.getViewWorldY() - vp.getViewWorldHeight() / 2);
		double scaleY = getScreenHeight() / vp.getViewWorldHeight();
		return (int)(getScreenHeight()-y*scaleY);
	}

	
	public void drawText(int x, int y, String str, Rect r) {
		_textRender.beginRendering((int)(r.getWidth()), (int)(r.getHeight()));
		_textRender.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		_textRender.draw(str, x, y);
		_textRender.endRendering();
		
	}
	
	@Override
	public void paint(GL gl) {
	}
}
