package app;

import model.rtree.RTreeModel;

import gui.MainFrame;
import gui.method.RTreeDrawPanel;

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
