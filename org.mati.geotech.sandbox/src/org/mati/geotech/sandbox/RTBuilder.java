package org.mati.geotech.sandbox;

import org.mati.geotech.model.rtree.RTreeModel;
import org.mati.geotech.sandbox.gui.MainFrame;
import org.mati.geotech.sandbox.gui.RTreeDrawPanel;

public class RTBuilder {

    public static void main(String[] args) {
        MainFrame mf = new MainFrame(new RTreeDrawPanel());
        RTreeModel model = new RTreeModel();
        model.makeRandom(10000);
        mf.setModel(model);
        mf.setVisible(true);
    }

}
