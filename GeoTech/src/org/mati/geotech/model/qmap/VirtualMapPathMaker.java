package org.mati.geotech.model.qmap;

import org.mati.geotech.model.Rect;

public class VirtualMapPathMaker extends PathMaker {

	@Override
	public String makePathFor(Rect cell, Rect start) {
		String path = "";
		Rect r = new Rect(0,0,0,0);
		
		r.setSameGeometry(start);
		
		while(cell.haveOverlap(r) && r.getWidth() > cell.getWidth()) {
			// 0 - q, 1 - r,  2 - t, 3 - s
			Rect [] sl = r.newFourBySplit();
			if(cell.haveOverlap(sl[0])) {
				r.setSameGeometry(sl[0]);
				path+="0";
			}
			else if(cell.haveOverlap(sl[1])) {
				r.setSameGeometry(sl[1]);
				path+="1";
			}
			else if(cell.haveOverlap(sl[2])) {
				r.setSameGeometry(sl[2]);
				path+="2";
			}
			else if(cell.haveOverlap(sl[3])) {
				r.setSameGeometry(sl[3]);
				path+="3";
			} else {
				System.out.println("error in: "+path);
				return null;
			}
		}
		return path;
	}

}
