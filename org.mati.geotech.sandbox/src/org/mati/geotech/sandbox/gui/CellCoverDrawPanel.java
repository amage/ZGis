package org.mati.geotech.sandbox.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.mati.geotech.model.Rect;
import org.mati.geotech.model.cellcover.CellCoverModel;
import org.mati.geotech.model.cellcover.MapGridCellView;


public class CellCoverDrawPanel extends DrawPanel {
	private static final long serialVersionUID = 8473481317194443201L;

	
	public CellCoverDrawPanel() {

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				Rect w = getModel().getDisplayRect();
				getCCModel().updateView(
//						(mapToScrX(w.getX()+w.getWidth())-mapToScrX(w.getX()))/w.getWidth(),
//						(mapToScrY(w.getY()+w.getHeight())-mapToScrY(w.getY()))/w.getHeight()
						getScreenWidth()/w.getWidth(),
						getScreenHeight()/w.getHeight()
				);
				repaint();
			}
		});	
		
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(!e.isControlDown()) {
					double r = e.getWheelRotation();
					r=0.10*r*(getModel().getDisplayRect().getWidth());
					getModel().getDisplayRect().setX(getModel().getDisplayRect().getX()-r/2);
					getModel().getDisplayRect().setY(getModel().getDisplayRect().getY()-r/2);
					getModel().getDisplayRect().setWidth(getModel().getDisplayRect().getWidth()+r);
					getModel().getDisplayRect().setHeight(getModel().getDisplayRect().getHeight()+r);
					
					Rect w = getModel().getDisplayRect();
					// System.out.print("scr: "+(mapToScrX(w.getX()+w.getWidth())-mapToScrX(w.getX())));
					// System.out.println(" vp: "+w.getWidth());
					getCCModel().updateView(
//							(mapToScrX(w.getX()+w.getWidth())-mapToScrX(w.getX()))/w.getWidth(),
//							(mapToScrY(w.getY()+w.getHeight())-mapToScrY(w.getY()))/w.getHeight()
							getScreenWidth()/w.getWidth(),
							getScreenHeight()/w.getHeight()

					);
					repaint();
				}
			}
		});
	}
	
	private CellCoverModel getCCModel() { return (CellCoverModel) getModel(); }
	
	@Override
	public void paintRects(Graphics g) {
		if(getCCModel()!=null) {
			g.setColor(Color.lightGray);
			for(Rect r:getModel().getRects()) {
				paintRect(g, r);
			}
			
			g.setColor(Color.blue);
			MapGridCellView [][] _map = getCCModel().getCC().getGridMartix();
			for(int i =0; i < getCCModel().getCC().getCellCountH(); i++) {
				for(int j =0; j < getCCModel().getCC().getCellCountW(); j++) {
					paintRect(g,_map[i][j]);
				}
			}
			
			// World
			g.setColor(Color.green);
			paintRect(g, getCCModel().getWorldRect());
			
			// Look window
			g.setColor(Color.red);
			paintRect(g, getModel().getDisplayRect());
			g.drawString("Screen", 
					(int)mapToScrX(getModel().getDisplayRect().getX()),
					(int)mapToScrY(getModel().getDisplayRect().getY())-10);
			
			g.drawString((int)getModel().getDisplayRect().getX()+" "+(int)getModel().getDisplayRect().getY(),
					(int)mapToScrX(getModel().getDisplayRect().getX()),
					(int)mapToScrY(getModel().getDisplayRect().getY()));
		}
	}
}
