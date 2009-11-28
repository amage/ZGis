package org.mati.geotech.layers;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.media.opengl.GL;

import org.apache.log4j.Logger;
import org.mati.geotech.gui.ViewPort;
import org.mati.geotech.model.GeoObject;
import org.mati.geotech.model.Rect;
import org.mati.geotech.model.ResManager;
import org.xml.sax.SAXException;

import com.keithpower.gekmlib.Folder;
import com.keithpower.gekmlib.KMLParser;
import com.keithpower.gekmlib.Kml;
import com.keithpower.gekmlib.Placemark;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;


public class ObjectsLayer extends TextLayer {
	
	//private LinkedList<GeoObject> testObjList = new LinkedList<GeoObject>();
	
	public ObjectsLayer(ResManager res, ViewPort vp) {
		super(res, vp);
		try {
			Kml kml = new KMLParser().parse(new File("./my.kml"));
			for(Folder f : kml.getDocument().getFolders()) {
				for(Placemark p : f.getPlacemarks()) {
					GeoObject go = new GeoObject();
					go.setStartLvl(0);
					Scanner s = new Scanner(p.getPoint().getCoordinates());
					s.useDelimiter(",");
					go.setX(Double.parseDouble(s.next()));
					go.setY(Double.parseDouble(s.next()));
					go.getProps().put("name", p.getName());
					go.getProps().put("type", "0");
					// System.out.println("kml: "+p.getName()+" pos: "+p.getPoint().getCoordinates());
					_res.recvObject(go);
					// testObjList.add(go);
				}
			}

		} catch (IOException e) {
			Logger.getRootLogger().error(e.getMessage());
		} catch (SAXException e) {
			Logger.getRootLogger().error(e.getMessage());
		}
	}
	
	private double merkProjX(double x) { return _vp.mapToWorldX(x); }
	private double merkProjY(double y) { return _vp.mapToWorldY(y); }

	@Override
	public void paint(GL gl) {
		// TextureCoords tc = t.getImageTexCoords();
		Rect vr =
			new Rect(_vp.worldToMapX(_vp.getViewWorldX()-_vp.getViewWorldWidth()/2),
					_vp.worldToMapY(_vp.getViewWorldY()+_vp.getViewWorldHeight()/2),
					_vp.worldToMapX(_vp.getViewWorldX()+_vp.getViewWorldWidth()/2)-
						_vp.worldToMapX(_vp.getViewWorldX()-_vp.getViewWorldWidth()/2),
					_vp.worldToMapY(_vp.getViewWorldY()-_vp.getViewWorldHeight()/2)-
						_vp.worldToMapY(_vp.getViewWorldY()+_vp.getViewWorldHeight()/2));

		LinkedList<GeoObject> objs = _res.getObjects(vr);
//		System.err.println(objs.size());
/*
		gl.glBegin(GL.GL_QUADS);
			gl.glVertex2d(_vp.mapToWorldX(vr.getX()), _vp.mapToWorldY(vr.getY()));
			gl.glVertex2d(_vp.mapToWorldX(vr.getX()+vr.getWidth()), _vp.mapToWorldY(vr.getY()));
			gl.glVertex2d(_vp.mapToWorldX(vr.getX()+vr.getWidth()), _vp.mapToWorldY(vr.getY()+vr.getHeight()));
			gl.glVertex2d(_vp.mapToWorldX(vr.getX()), _vp.mapToWorldY(vr.getY()+vr.getHeight()));
		gl.glEnd();	
*/
		gl.glTexEnvi(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		for(GeoObject o : objs) {
			if(o.isLine()) continue;
			try {				
				double scaleX = getScreenWidth() / _vp.getViewWorldWidth();
				double scaleY = getScreenHeight() / _vp.getViewWorldHeight();
				double xsh = 16/scaleX;
				double ysh = 16/scaleY;

				Texture t = _res.getObjTexture(o.getType());
				t.enable();
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NICEST);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NICEST);
				gl.glEnable(GL.GL_BLEND);
				t.bind();
				TextureCoords tc = t.getImageTexCoords();
				// System.out.println("obj: "+o.getName()+" y: "+(merkProjY(o.getMapY()) - ysh));
				gl.glBegin(GL.GL_QUADS);
					gl.glTexCoord2d(tc.left(), tc.top());
					gl.glVertex2d(merkProjX(o.getMapX()) - xsh, merkProjY(o.getMapY()) - ysh);
					gl.glTexCoord2d(tc.right(), tc.top());					
					gl.glVertex2d(merkProjX(o.getMapX()) + xsh, merkProjY(o.getMapY()) - ysh);
					gl.glTexCoord2d(tc.right(), tc.bottom());					
					gl.glVertex2d(merkProjX(o.getMapX()) + xsh, merkProjY(o.getMapY()) + ysh);
					gl.glTexCoord2d(tc.left(), tc.bottom());					
					gl.glVertex2d(merkProjX(o.getMapX()) - xsh, merkProjY(o.getMapY()) + ysh);
				gl.glEnd();
				t.disable();

				//if(o.showText()) {
					drawText(
						mapToScrX(merkProjX(o.getMapX()), _vp)+16, 
						mapToScrY(merkProjY(o.getMapY()), _vp)+5, 
						o.getName(), 
						new Rect(0,0,_vp.getScreenWidth(), _vp.getScreenHeight()));
					//o.setShowText(false);
				//}				
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("on draw layer: "+o.getName() + ": " +e.getMessage());
			}
		}
		

//		_res.getObjects();
//		gl.glBegin(GL.GL_QUADS);
//			gl.glTexCoord2d(tc.left(), tc.top());
//			gl.glVertex2d(cell.getX(), cell.getY()); 
//		
//			gl.glTexCoord2d(tc.right(), tc.top());
//			gl.glVertex2d(cell.getX()+cell.getWidth(), cell.getY());
//		
//			gl.glTexCoord2d(tc.right(), tc.bottom());
//			gl.glVertex2d(cell.getX()+cell.getWidth(), cell.getY()+cell.getHeight()); 
//		
//			gl.glTexCoord2d(tc.left(), tc.bottom());
//			gl.glVertex2d(cell.getX(), cell.getY()+cell.getHeight()); 
//		
//		gl.glEnd();

		// t.disable();
	}
}
