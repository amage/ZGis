package model;

import java.util.Vector;

public interface ObjectLoaderListener {
	public void recvObjectIdList(Vector<Integer> oid);
	public void recvObject(GeoObject gobj);
}
