package org.mati.geotech.model;


import java.util.Collection;
import java.util.LinkedList;

import org.mati.geotech.gui.ObjectCatalog;

public class ObjFilter {
	
	private ObjectCatalog _cat = new ObjectCatalog();

	public void setConfig(ObjectCatalog cat) {
		_cat  = cat;
	}
	
	public LinkedList<GeoObject> filt(Collection<? extends GeoObject> select) {
		LinkedList<GeoObject> res = new LinkedList<GeoObject>();
		for(GeoObject o:select) {
			if(isOk(o))	res.add(o);
		}
		return res;
	}
	
	public boolean isOk(GeoObject ob) {
		return !_cat.isTypeFiltred(ob.getType());
	}

}
