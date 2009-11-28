package org.mati.geotech.utils.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Vector;

public class Config {
	private static Config _conf;
	private Vector<ConfigUser> _cui = new Vector<ConfigUser>();
	private Properties _props = new Properties();
	
	
	public void addConfigUser(ConfigUser cu) {
		_cui.add(cu);
		cu.coufigChanged(_props);
	}
	public void removeConfigUser(ConfigUser cu) {_cui.remove(cu);}
	
	public void updateAll() {
		for(ConfigUser cu: _cui) {
			cu.coufigChanged(_props);
		}
	}
	
	private Config() {}
	
	public void loadConfig(InputStream is) throws IOException {
		_props.load(is);
		updateAll();
	}
	
	static public Config getInstance() {
		if(_conf==null) _conf = new Config();
		return _conf;
	}
	public Properties getProps() {
		return _props;
	}
	public void save(OutputStream outs) throws IOException {
		_props.store(outs, "GeoTeck config file");
	}
	public String getProperty(String key, String defVal) {
		if(!_props.containsKey(key)) {
			_props.put(key, defVal);
			System.out.println("new key: "+key);
			return defVal;
		}
		else return _props.getProperty(key);
	}
}
