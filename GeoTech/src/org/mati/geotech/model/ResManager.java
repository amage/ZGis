package org.mati.geotech.model;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;

import org.mati.geotech.gui.ObjectCatalog;
import org.mati.geotech.model.cellcover.CellCoverListener;
import org.mati.geotech.model.cellcover.MapGridCellView;
import org.mati.geotech.model.qmap.GoogleMapPathMaker;
import org.mati.geotech.model.qmap.PathMaker;
import org.mati.geotech.model.qmap.VirtualMapPathMaker;
import org.mati.geotech.model.rtree.RTree;
import org.mati.geotech.utils.URLUTF8Encoder;
import org.mati.geotech.utils.config.Config;
import org.mati.geotech.utils.config.ConfigUser;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;

public class ResManager implements TextureProcListener, CellCoverListener, ConfigUser {
	
	public enum MapSource {
		GOOGLE,
		VIRTUAL_EARTH_LINES,
		VIRTUAL_EARTH_PHOTO,
		VIRTUAL_EARTH_ALL
	};
	
	private MapSource _curMapSrc = MapSource.VIRTUAL_EARTH_ALL; 
		
	private TextureProc _texProc;
	private int _cacheLvls=3;
	private HashMap<String, Texture> _texsActive[];

	private String _serverURL = Config.getInstance().getProperty("geoteck.object_server","http://127.0.0.1/");
	
	private Texture _texMatrix[][][];
	private double _gsw = 0;
	private double _gsh = 0;
	
	private Rect _map = new Rect(-180,-90,360, 180);

	private Texture _texLoading;
	private Texture _texDownloading;
	private Texture _texNotAvailable;
	
	private Vector<Texture> _objTexs = new Vector<Texture>();
	private GeoObject newLineObject = new GeoObject();

	private RTree _objTrees[] = new RTree[20];
	
	private int _lvl = 1;
	private Rect _cacheRect = new Rect(0,0,0,0);

	// Interaction stuff
	private Vector<ResManagerListener> _rmi = new Vector<ResManagerListener>();

	private ObjFilter _objFilter = new ObjFilter();
	public void removeListner(ResManagerListener rmi) { _rmi.remove(rmi); }
	public void addListner(ResManagerListener rmi) {_rmi.add(rmi); }
	
	@SuppressWarnings("unchecked")
	public ResManager() {
		_texProc = new TextureProc();
		_texsActive = new HashMap[_cacheLvls];
		_texsActive[0] = new HashMap<String, Texture>();
		_texsActive[1] = new HashMap<String, Texture>();
		_texsActive[2] = new HashMap<String, Texture>();
		_map.setSameGeometry(new Rect(-180,-90,360,180));
		for(int i = 0; i < _objTrees.length; i++) {
			_objTrees[i] = new RTree();
		}
		
		_texMatrix = new Texture[3][][];
	}
	
	public MapSource getMapSourceType() {return _curMapSrc;}
	public void setMapSourceType(MapSource ms) {
		_curMapSrc=ms;
		_texProc.setMapSourceType(ms);
		for(int i = 0; i < 3; i++) _texsActive[i].clear();
		for(ResManagerListener rm : _rmi) rm.stateChanged();
	}

	
	synchronized private void preload(Rect r, int lvl) {
		int layer = lvl-_lvl+1;
		Vector<String> paths = getPathsFor(r, lvl);
		// System.out.println("preload"+"("+layer+")"+": " + paths.size());
		for(String str:paths) {
			try {
				_texsActive[layer].put(str, null);
				getMapTexture(str);
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
	}
	private PathMaker selectPathMaker() {
		PathMaker pm;
		switch (_curMapSrc) {
		case GOOGLE:
			pm = new GoogleMapPathMaker();
			break;
		case VIRTUAL_EARTH_LINES:
			pm = new VirtualMapPathMaker();
			break;
		case VIRTUAL_EARTH_PHOTO:
			pm = new VirtualMapPathMaker();
			break;
		case VIRTUAL_EARTH_ALL:
			pm = new VirtualMapPathMaker();
			break;
		default:
			pm = new VirtualMapPathMaker();
		}
		return pm;
	}


	/*
	synchronized private void freeTextures(Rect r, int lvl) {
		int layer = lvl-_lastLvl+1; 
		
		Vector<String> paths = getPathsFor(r, lvl);
		System.out.println("free("+layer+"): " + paths.size());
		for(String str:paths) {
			try {
				if(_texsActive[layer].get(str)!=null)
					_texsActive[layer].get(str).dispose();
				_texProc.freeTexture(str);
			} catch (Exception e) { 
				e.printStackTrace();
			}
		}
	}
	*/
		
	private void setViewWindow(Rect vp, int lvl) {				
		if(_lvl!=lvl) {		
			int shift = lvl-_lvl; 
			_lvl = lvl;

			// System.out.println("shift: " + shift);
			
			int cleanLayer = shift==1? 0 : 2;
			
			for(String str: _texsActive[cleanLayer].keySet()){
				_texProc.freeTexture(str);
				Texture t = _texsActive[cleanLayer].get(str);
				if(t!=null && t!=_texDownloading &&
				t!=_texLoading &&
				t!=_texNotAvailable) {
					t.bind();
					t.dispose();
				}
			}
			_texsActive[cleanLayer].clear();
			System.gc();
			
			if(shift == 1) {
				_texsActive[0] = _texsActive[1];
				_texsActive[1] = _texsActive[2];
				_texsActive[2] = new HashMap<String, Texture>();
				preload(_cacheRect, lvl+1);
			}
			else if (shift == -1) {
				_texsActive[2] = _texsActive[1]; 
				_texsActive[1] = _texsActive[0];
				_texsActive[0] = new HashMap<String, Texture>();
				preload(_cacheRect, _lvl-1);
			}
			else {
				System.out.println("RELOAD ALL CACHE!");
				for(int i = 0; i < 3; i++) {
					for(String str: _texsActive[i].keySet()){
						_texProc.freeTexture(str);
						Texture t = _texsActive[i].get(str);
						if(t!=null && t!=_texDownloading &&
						t!=_texLoading &&
						t!=_texNotAvailable) {
							t.bind();
							t.dispose();
						}
					}
					_texsActive[i].clear();
					System.gc();
					preload(_cacheRect, _lvl-1+i);
				}
			}
		}
	}

	/*
	synchronized private void updateCache(Rect rect, Rect window) {
//		Rect rLoad[] = { new Rect(0,0,0,0), new Rect(0,0,0,0) };
//		Rect rFree[] = {new Rect(0,0,0,0), new Rect(0,0,0,0) };

		// preload(rLoad[0], _lastLvl-1); preload(rLoad[0], _lastLvl); preload(rLoad[0], _lastLvl+1);	
	}
	*/

	synchronized private void loadTextures() throws Exception {
		_texProc.addListner(this);
		_texProc.start();
		
		for(int i = 0; i < 3; i++){ 
			Vector<String> paths = getPathsFor(_map, i+1);
			for(String str:paths) {
				_texsActive[i].put(str, getMapTexture(str));
			}
		}

		// FIXME: image path
		
		String syspath = Config.getInstance().getProperty("geotech.syspath", 
		        "/home/yuriy/workspace-geotech/geotech/");
		

		_texLoading = TextureIO.newTexture(new File(syspath+"images/loading.png"), false);
		_texNotAvailable = TextureIO.newTexture(new File(syspath+"images/notavailable.png"), false);
		_texDownloading= TextureIO.newTexture(new File(syspath+"images/downloading.png"), false);
		
		// obj textures load
		boolean bExit = false;
		int i=0;
		while(!bExit) {
			File f = new File(syspath+"objs/o"+i+".png");
			
			if(f.isFile()) {
				Texture t = TextureIO.newTexture(f,false);
				_objTexs.add(t);
				i++;
			} 
			else {
				bExit=true;
			}
		}
	}
	
		
	public Texture getMapTexture(String gpath) throws Exception {
		Texture tex = null;
		HashMap<String, Texture> lay = null;
		
		for(HashMap<String, Texture> hm :_texsActive) {
			if(tex==null) {
				tex = hm.get(gpath);
				lay = hm;
			}
		}
		
		if(tex==null || tex==_texLoading || tex==_texDownloading) {		
			try {
				TextureData texData = _texProc.getTextureData(gpath);
				if(texData!=null) {
					tex=TextureIO.newTexture(texData);
					lay.put(gpath, tex);
					// System.out.println("Make GL tex: "+gpath);
					return tex;
				}
				else {
					lay.put(gpath, _texLoading);
					return _texLoading;
				}
			}
			catch (TextureNotAvailableException e) {
				if(e.isDownloadig()) {
					lay.put(gpath, _texDownloading);
					return _texDownloading;
				}
				else {
					lay.put(gpath, _texNotAvailable);
					return _texNotAvailable;
				}
			}
		}
		else {
			return tex;
		}
	}

	public void init() throws Exception {
		loadTextures();
	}

	public Vector<String> getPathsFor(Rect cell, int lvl) {
		double _cellSize = _map.getWidth()/(Math.pow(2, lvl));
		int n = (int) Math.ceil(cell.getHeight()/_cellSize)*2+1;
		int m = (int) Math.ceil(cell.getWidth()/_cellSize)+1;
		
		double cellW = _cellSize;
		double cellH = _cellSize/2;
		
		double gx=Math.floor(cell.getX()/cellW)*cellW;
		double gy=Math.floor(cell.getY()/cellH)*cellH;
		
		Rect r = new Rect(0,0,0,0);
		
		LinkedList<String> result = new LinkedList<String>(); 
		
		for(int i=0; i < n; i++) {
			for(int j=0; j < m; j++) {
				r.setX(j*cellW+gx);
				r.setY(i*cellH+gy);
				r.setWidth(cellW);
				r.setHeight(cellH);
				if(r.haveOverlap(_map)) 
					result.add(makePathFor(r));
			}
		}
		Vector<String> res = new Vector<String>(result);
		return res;
	}
	
	public String makePathFor(Rect cell) {
		PathMaker pm = selectPathMaker();
		return pm.makePathFor(cell, _map);
	}

	@Override
	public void texturesReady(String gpaths[]) {
		for(String gpath : gpaths) {
			if(gpath.length()-_lvl > 2 || gpath.length()-_lvl < 0) {
				_texProc.freeTexture(gpath);
			}
			else {
//				System.out.println(gpath);
				_texsActive[gpath.length()-_lvl].put(gpath,null);
			}
		}
		for(ResManagerListener rm : _rmi) rm.stateChanged();
	}
	@Override
	public void downloadComplite(String name) {
		for(ResManagerListener rm : _rmi) rm.stateChanged();
	}

	public LinkedList<GeoObject> getObjects(Rect w) { return getObjects(w, _objFilter); }
	
	public LinkedList<GeoObject> getObjects(Rect w, ObjFilter of) {
		LinkedList<GeoObject> result = new LinkedList<GeoObject>();
		for(int i=0; i <= _lvl; i++) {
			if(of!=null)
				result.addAll(of.filt(_objTrees[i].select(w)));
			else
				result.addAll(_objTrees[i].select(w));
		}
		return result;
	}

	public Texture getObjTexture(int type) {
		if(type>=0 && type < _objTexs.size()) 
			return _objTexs.get(type);
		else
			return _objTexs.get(0);
	}

	// CellCover listener
	@Override
	public void gridSizeChanged(int n, int m) {
		// System.out.println("new grid: "+n+"x"+m);
		/*
		// TODO: use old tex
		for(int l = 0; l < 3; l++) {
			_texMatrix[l] = new Texture [n][m];
			for(int i = 0; i < _texMatrix[l].length; i++) {
				for(int j = 0; j < _texMatrix[l][i].length; j++)
					_texMatrix[l][i][j]=null;
			}
		}	
		*/	
	}

	@Override
	public void gridPositionChanged(double x, double y, double cw, double ch,
			int n, int m) {
		_cacheRect.setGeometry(x, y, cw*m, ch*m);
		_gsw=cw;
		_gsh=ch;
		setViewWindow(_cacheRect, _lvl);
		// System.out.println("new grid pos: "+y+"x"+x);		
	}

	@Override
	public void levelChanged(int newLvl, int prevLvl) {
		_lvl=newLvl;
		/*
		if(Math.abs(newLvl-prevLvl)==1) {
			Texture tm[][] = new Texture [(int)(_cacheRect.getWidth()/_gsw)]
										 [(int)(_cacheRect.getHeight()/_gsh)];
			for(int i = 0; i < tm.length; i++) for(int j = 0; j < tm[i].length; j++) tm[i][j]=null;

			if(newLvl>prevLvl) {
				// zoom in
				_texMatrix[0]=_texMatrix[1];
				_texMatrix[1]=_texMatrix[2];
				_texMatrix[2]=tm;
			}
			else {
				// zoom out
				_texMatrix[2]=_texMatrix[1];
				_texMatrix[1]=_texMatrix[0];
				_texMatrix[0]=tm;
			}
		}
		else {
			for(int l = 0; l < 3; l++) {
				_texMatrix[l] = new Texture [(int)(_cacheRect.getWidth()/_gsw)]
											 [(int)(_cacheRect.getHeight()/_gsh)];
				for(int i = 0; i < _texMatrix[l].length; i++) {
					for(int j = 0; j < _texMatrix[l][i].length; j++)
						_texMatrix[l][i][j]=null;
				}
			}
		}
		*/
	}

	public Texture getMapTexture(MapGridCellView cell) {
		if(cell.haveOverlap(_cacheRect)) {
			int i = (int)((cell.getY()-_cacheRect.getY())/(double)_gsh);
			int j = (int)((cell.getX()-_cacheRect.getX())/(double)_gsw);
			if(_texMatrix[1][i][j]==null) {
				try {
					_texMatrix[1][i][j]=getMapTexture(makePathFor(cell));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return _texMatrix[1][i][j];
		}
		else	
			return null;
	}
	
	public int getMapLvl() { return _lvl; }
	public void createObj(GeoObject go) throws IOException {
		String req = _serverURL+"get.php?cmd=insert";
		
		for(String key: go.getProps().keySet())
			req += "&"+URLUTF8Encoder.encode(key)+"="+URLUTF8Encoder.encode(go.getProps().get(key));
		//System.out.println(req);
		URL url = new URL(req);
		URLConnection con = url.openConnection();
		con.connect();
		Scanner s = new Scanner(con.getInputStream());
		if(s.hasNextLine()) {
			String ans=s.nextLine();
			if(!ans.equalsIgnoreCase("ok")) {
				throw new IOException("Wrong server answer("+ans+")");
			}
			go.getProps().put("id", s.nextLine().trim());
		}
		else
			throw new IOException("Wrong server answer");
		
		createObjPnts(go);
	}
	
	private void createObjPnts(GeoObject go) throws IOException {		
		for(GeoPoint p:go.getPoints()) {
			String req = _serverURL+"get.php?cmd=insert_pnt&obj_id="+go.getId()+"&lat="+p.getLat()+"&lon="+p.getLon();
			// System.out.println(req);
			URL url = new URL(req);
			URLConnection con = url.openConnection();
			con.connect();
			Scanner s = new Scanner(con.getInputStream());
			if(s.hasNextLine()) {
				String ans=s.nextLine();
				if(!ans.equalsIgnoreCase("ok")) {
					throw new IOException("Wrong server answer("+ans+")");
				}
			}
			else
				throw new IOException("Wrong server answer");

		}
	}
	
	@Override
	public void coufigChanged(Properties prop) {
		_serverURL = Config.getInstance().getProperty("geoteck.object_server","http://127.0.0.1/");		
	}
	public GeoObject getNewObjectLines() { return newLineObject; }
	public void newLineObject() { newLineObject=new GeoObject(); }
	public void setNewLineObject(GeoObject obj) { newLineObject=obj; }
	public void setFilter(ObjectCatalog cat) {
		_objFilter.setConfig(cat);
	}
}
