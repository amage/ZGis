package org.mati.geotech.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.mati.geotech.gui.GTMainFrame;
import org.mati.geotech.utils.config.Config;


public class GeoTeckApp {
	
	static final public String confName="geoteck.conf";

	/**
	 * @param args
	 */
	public static void main(String[] args) {		

		// init log subsystem
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.ERROR);
		
		
		try {
			Config.getInstance().loadConfig(new FileInputStream(confName));
		} catch (IOException e) {
			try {
				new File(confName).createNewFile();
			} catch (IOException e1) {
				Logger.getRootLogger().error("config error: "+e1.getMessage());
			}
			Logger.getRootLogger().error("config error: "+e.getMessage());
		}
		GTMainFrame frame = new GTMainFrame();
		frame.setVisible(true);
	}

}
