package org.mati.geotech.model;

public class TextureNotAvailableException extends Exception {
	private static final long serialVersionUID = -1120836211444732180L;	
	private boolean _downloading = false;
	
	
	public TextureNotAvailableException() {
		_downloading = false;
	}
	
	public TextureNotAvailableException(boolean downloadind) {
		_downloading = downloadind;
	}
	
	public boolean isDownloadig() {
		return _downloading;
	}

}
