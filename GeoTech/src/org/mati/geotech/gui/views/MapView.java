package org.mati.geotech.gui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.mati.geotech.gui.swt.MainWindow;

public class MapView extends ViewPart {
    public final static String ID= "org.mati.geotech.mapview";
    
    private MainWindow mainWindow;
    
    public MapView() {
    }

    @Override
    public void createPartControl(Composite parent) {
        mainWindow= new MainWindow(parent, SWT.NONE);
    }

    @Override
    public void setFocus() {
        mainWindow.setFocus();
    }

}
