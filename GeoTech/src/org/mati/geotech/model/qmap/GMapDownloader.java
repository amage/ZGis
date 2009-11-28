package org.mati.geotech.model.qmap;

import org.mati.geotech.utils.config.Config;



public class GMapDownloader extends MapDownloader {
	private String[] _url = {
				"http://kh.google.com/kh?v=99&n=404&t=",
				"http://kh1.google.com/kh?v=99&n=404&t=",
				"http://kh2.google.com/kh?v=99&n=404&t=",
				"http://kh3.google.com/kh?v=99&n=404&t="
				};
	
	public GMapDownloader() {
	}

	@Override
	protected String getStorePath() {
		return Config.getInstance().getProperty("geoteck.google_maps.store_path","./maps/gmap/");
	}

	@Override
	protected String makePath(String mapPath) {
		return _url[(int)(Math.random()*_url.length)]+mapPath;
	}

	@Override
	protected long getSleepTime() { return 15*60000; }

	@Override
	protected String getFileExt() {
		return ".jpg";
	}

	@Override
	protected long getDelayLoadTime() {
		return (long)(10000*Math.random());
	}
}
