package org.mati.geotech.model.qmap;

public interface DownloaderListener {
	public void fileNotAvailable(String gpath);
	public void downloadComplite(String gpath,String filepath);
}
