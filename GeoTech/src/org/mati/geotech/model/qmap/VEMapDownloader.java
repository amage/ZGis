package org.mati.geotech.model.qmap;

import org.mati.geotech.model.ResManager.MapSource;
import org.mati.geotech.utils.config.Config;


public class VEMapDownloader extends MapDownloader {
	MapSource _type = MapSource.VIRTUAL_EARTH_ALL;
	private String[] _urlPrefix = {
			"http://r0.ortho.tiles.virtualearth.net/tiles/",
			"http://r1.ortho.tiles.virtualearth.net/tiles/",
			"http://r2.ortho.tiles.virtualearth.net/tiles/",
			"http://r3.ortho.tiles.virtualearth.net/tiles/"
			};
	private String _urlPostfix = ".png?g=99";
	
	private String getMapPath() {
		switch (_type) {
		case VIRTUAL_EARTH_ALL:
		case VIRTUAL_EARTH_LINES:
		case VIRTUAL_EARTH_PHOTO:
			return Config.getInstance().getProperty("geoteck.virtual_earth.store_path","./maps/ve");
		}
		return Config.getInstance().getProperty("geoteck.virtual_earth.store_path","./maps/ve");
	}
	
	@Override
	protected long getSleepTime() { return 1000; }
	@Override
	protected String getStorePath() { return getMapPath()+getExtByType()+"/"; }
	@Override
	protected String makePath(String mapPath) {		
		String res = _urlPrefix[(int)(Math.random()*_urlPrefix.length)]+getExtByType()+mapPath+_urlPostfix;
		return res;
	}
	@Override
	protected String getFileExt() {
		return ".png";
	}
	@Override
	protected long getDelayLoadTime() {
		return 0;
	}
	public void setType(MapSource ve_type) {
		_type = ve_type;		
	}
	
	private String getExtByType() {
		switch (_type) {
		case VIRTUAL_EARTH_ALL:
			return "h";
		case VIRTUAL_EARTH_PHOTO:
			return "a";
		case VIRTUAL_EARTH_LINES:
			return "r";
		default:
			return "h";
		}
	}
}
