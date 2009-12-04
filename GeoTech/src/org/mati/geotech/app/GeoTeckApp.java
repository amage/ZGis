package org.mati.geotech.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.mati.geotech.gui.MainWindow;
import org.mati.geotech.utils.config.Config;

public class GeoTeckApp {

    static final public String confName = "geoteck.conf";

    /**
     * @param args
     */
    public static void main(String[] args) {

        // init log subsystem
        // BasicConfigurator.configure();
        // Logger.getRootLogger().setLevel(Level.ERROR);

        try {
            Config.getInstance().loadConfig(new FileInputStream(confName));
        } catch (IOException e) {
            try {
                new File(confName).createNewFile();
            } catch (IOException e1) {
                // Logger.getRootLogger().error("config error: "+e1.getMessage());
                e1.printStackTrace();
            }
            // Logger.getRootLogger().error("config error: "+e.getMessage());
        }

        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        new MainWindow(shell, SWT.NONE);
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        try {
            display.dispose();
        } finally {
            System.exit(0);
        }
    }

}
