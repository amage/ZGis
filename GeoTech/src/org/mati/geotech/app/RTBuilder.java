package org.mati.geotech.app;

import org.mati.geotech.gui.MainFrame;
import org.mati.geotech.gui.method.RTreeDrawPanel;
import org.mati.geotech.model.rtree.RTreeModel;


public class RTBuilder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MainFrame mf = new MainFrame(new RTreeDrawPanel());
		RTreeModel model = new RTreeModel();
		model.makeRandom(10);
		mf.setModel(model);
		mf.setVisible(true);
	}
	
}
