package org.mati.geotech.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingDeque;

import org.mati.geotech.model.ResManager.MapSource;
import org.mati.geotech.model.qmap.DownloaderListener;
import org.mati.geotech.model.qmap.GMapDownloader;
import org.mati.geotech.model.qmap.MapDownloader;
import org.mati.geotech.model.qmap.VEMapDownloader;
import org.mati.geotech.utils.config.Config;

import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

public class TextureProc extends Thread implements DownloaderListener {
	private boolean _canDownload = Boolean.parseBoolean(
						Config.getInstance().getProperty("geoteck.can_download_maps","true"));
	private MapSource _mapType = MapSource.VIRTUAL_EARTH_ALL;
	
	public void setMapSourceType(MapSource ms) { 
		_mapType=ms;
		switch (_mapType) {
		case GOOGLE:
			_dwCtrl = _gmDownloader;
			break;
		case VIRTUAL_EARTH_ALL:
			_dwCtrl = _veaDownloader;
			break;
		case VIRTUAL_EARTH_LINES:
			_dwCtrl = _velDownloader;
			break;
		case VIRTUAL_EARTH_PHOTO:
			_dwCtrl = _vepDownloader;
			break;
		}
		_texsToFree.addAll(_texsActive.keySet()); _texsActive.clear();
		_texsToFree.addAll(_texsCache.keySet());_texsCache.clear();
		_texsToFree.addAll(_texsNA.keySet());_texsNA.clear();
	}
	
	private String getMapPath() {
		switch (_mapType) {
		case GOOGLE:
			return Config.getInstance().getProperty("geoteck.google_maps.store_path","./maps/gmap/");
		case VIRTUAL_EARTH_ALL:
			return Config.getInstance().getProperty("geoteck.virtual_earth.store_path","./maps/ve")+"h/";
		case VIRTUAL_EARTH_LINES:
			return Config.getInstance().getProperty("geoteck.virtual_earth.store_path","./maps/ve")+"r/";
		case VIRTUAL_EARTH_PHOTO:
			return Config.getInstance().getProperty("geoteck.virtual_earth.store_path","./maps/ve")+"a/";
		}
		return Config.getInstance().getProperty("geoteck.virtual_earth.store_path","./maps/veh/");
	}
	private HashMap<String, TextureData> _texsActive = new HashMap<String, TextureData>();
	private HashMap<String, String> _texsCache = new HashMap<String, String>();
	private HashMap<String,String> _texsNA = new HashMap<String,String>();

	private LinkedBlockingDeque<String> _loadFromCache = new LinkedBlockingDeque<String>();
	private Stack<String> _texsToFree = new Stack<String>();
	
	private final VEMapDownloader _velDownloader = new VEMapDownloader();
	private final VEMapDownloader _vepDownloader = new VEMapDownloader();
	private final VEMapDownloader _veaDownloader = new VEMapDownloader();
	private final GMapDownloader  _gmDownloader  = new GMapDownloader();
	
	public MapDownloader _dwCtrl = _veaDownloader;
	int tex_count=0;
	
	// Interaction stuff
	private Vector<TextureProcListener> _tci = new Vector<TextureProcListener>();
	public void removeListner(TextureProcListener tci) { _tci.remove(tci); }
	public void addListner(TextureProcListener tci) {_tci.add(tci); }
	
	public TextureProc() {
		_velDownloader.setType(ResManager.MapSource.VIRTUAL_EARTH_LINES);
		_velDownloader.start();
		_vepDownloader.setType(ResManager.MapSource.VIRTUAL_EARTH_PHOTO);
		_vepDownloader.start();
		_veaDownloader.setType(ResManager.MapSource.VIRTUAL_EARTH_ALL);
		_veaDownloader.start();
		_gmDownloader.start();
		_dwCtrl.addErrorListner((DownloaderListener)this);
		// setPriority(Thread.MIN_PRIORITY);
	}
	
	public void freeAll() {
		_texsToFree.addAll(_texsActive.keySet());
	}
	
	public void freeTexture(String gpath) {
		if(_texsToFree.search(gpath)==-1) {
			//System.out.println("free: "+gpath);
			_texsToFree.add(gpath);
		}
		synchronized(this) { 
			notifyAll();
		}
	}
	
	public TextureData getTextureData(String gpath) throws TextureNotAvailableException {
		//if(_canDownload)
		if(_texsNA.get(gpath)!=null) throw new TextureNotAvailableException();
		
		TextureData texData = _texsActive.get(gpath);
		if(texData==null) {
			if(_texsCache.get(gpath)!=null) {
				if(!_loadFromCache.contains(gpath)) {
					_loadFromCache.addLast(gpath);
					synchronized(this) { notifyAll(); }
				}
			} else {
				if(new File(makeFileNameFromPath(gpath)).isFile()) {
					_texsCache.put(gpath, makeFileNameFromPath(gpath));
					_loadFromCache.addLast(gpath);
					synchronized(this) { notifyAll(); }
					return null;
				}
				else if (_canDownload) {
					_dwCtrl.addPath(gpath);
					throw new TextureNotAvailableException(true);
				}
				else {
					throw new TextureNotAvailableException(false);					
				}
			}
		}
		return texData;
	}

	public String makeFileNameFromPath(String gpath) {
		String path = getMapPath();
		
		for(int i = 0; i < gpath.length()-1; i++) {
			path+=gpath.charAt(i)+"/";
		}
		if(_mapType==MapSource.GOOGLE) {
			path+=gpath.charAt(gpath.length()-1)+".jpg";
		}
		else {
			path+=gpath.charAt(gpath.length()-1)+".png";
		}
		return path;
	}
	
	@Override
	public void run() {
		boolean bExit = false;
		Stack<String> ltexs = new Stack<String>();
		while(!bExit) {	
			// load new textures
			if(!_loadFromCache.isEmpty()) {
				String gpath = _loadFromCache.pollFirst();
				try {
					// System.out.println(makeFileNameFromVEPath(gpath));
					TextureData td = TextureIO.newTextureData(new File(makeFileNameFromPath(gpath)), false,"");
					tex_count++;
					_texsActive.put(gpath, td);
					ltexs.push(gpath);
				} catch (IOException ioe) {
					if(_canDownload) {
						_dwCtrl.addPath(gpath);
					} else {
						_texsNA.put(gpath, "N/A");
					}
				} catch (OutOfMemoryError e) {
					// TODO: good memory free
					System.out.println("gc");
					freeAll();
					System.gc();
				}
			}
			
			// clean textures
			if(!_texsToFree.isEmpty()) {
				String gpath = _texsToFree.pop();
				TextureData td = _texsActive.get(gpath);
				if(td!=null) {
					_texsActive.remove(gpath);
					_texsCache.remove(gpath);
					_texsNA.remove(gpath);
					// System.out.println("free: "+gpath);
					tex_count--;
					td.flush();
					td=null;
				}
			}
			String res[] = new String[ltexs.size()];
			for(int i=0; i < res.length; i++) {
				res[i] = ltexs.pop();
			}
			for(TextureProcListener tl:_tci) tl.texturesReady(res);
			yield();
			bExit=waitForTask();
		}
	}

	private synchronized boolean waitForTask() {
		while(_loadFromCache.isEmpty() && _texsToFree.isEmpty()) {
			try { 
				// System.out.println("Textures count: "+tex_count);
				wait();
			} catch (InterruptedException e) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void fileNotAvailable(String gpath) {
		System.err.println("error: "+gpath);
		_texsNA.put(gpath, "");
	}

	@Override
	public void downloadComplite(String gpath, String filepath) {
		// System.out.println(gpath+": "+filepath);
		_texsCache.put(gpath, filepath);
		for(TextureProcListener tl:_tci) tl.downloadComplite(gpath);
	}
}
