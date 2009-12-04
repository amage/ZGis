package org.mati.geotech.app;

import org.mati.geotech.gui.method.CellCoverDrawPanel;
import org.mati.geotech.model.cellcover.CellCoverModel;

public class CellCoverApp {
    public static void main(String[] args) {
        MainFrame mf = new MainFrame(new CellCoverDrawPanel());
        CellCoverModel model = new CellCoverModel();
        mf.setModel(model);
        mf.setVisible(true);
    }
}
