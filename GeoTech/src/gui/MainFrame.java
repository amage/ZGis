package gui;

import gui.method.DrawPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import model.RectViewModel;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = -8026416994513756565L;
	private DrawPanel _dpanel;
	private JPanel _ctrlPanel;
	
	//private JTree _tree;
	public MainFrame(DrawPanel dp) {
		_dpanel=dp;
		//_tree = new JTree();
		init();
	}

	private void init() {
		_ctrlPanel = makeCtrlPanel();
		this.setSize(new Dimension(360*3,180*3));
		this.add(_dpanel,BorderLayout.CENTER);
		this.add(_ctrlPanel,BorderLayout.WEST);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		_dpanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			//	_tree.setModel(new DefaultTreeModel(_dpanel.getModel().makeJTree()));
				repaint();
			}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
		});
	}
	
	private JPanel makeCtrlPanel() {
		JPanel p = new JPanel();
		
		
		return p;
	}

	public void setModel(RectViewModel aModel) {
		_dpanel.setModel(aModel);
	}
	
	
}
