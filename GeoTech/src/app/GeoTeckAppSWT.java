package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import utils.config.Config;
import gui.swt.MainWindow;

public class GeoTeckAppSWT {
	
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
		
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		new MainWindow(shell,SWT.NONE);
		shell.open();
		
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		try {
			display.dispose();
		}
		finally {
			System.exit(0);
		}
	}

}
