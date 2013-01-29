package org.mati.geotech.app;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.mati.geotech.gui.views.MapView;

public class PerspectiveFactory implements IPerspectiveFactory {
    public static final String ID= "org.mati.geotech.mapViewPerspective";
    @Override
    public void createInitialLayout(IPageLayout layout) {
        layout.setEditorAreaVisible(false);
        layout.addView(MapView.ID, IPageLayout.LEFT, 1.0f, "mapView");
    }

}
