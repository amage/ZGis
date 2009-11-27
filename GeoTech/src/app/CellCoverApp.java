package app;

import model.cellcover.CellCoverModel;
import gui.MainFrame;
import gui.method.CellCoverDrawPanel;

public class CellCoverApp {
	public static void main(String[] args) {
		MainFrame mf = new MainFrame(new CellCoverDrawPanel());
		CellCoverModel model = new CellCoverModel();
		mf.setModel(model);
		mf.setVisible(true);
	}
}
