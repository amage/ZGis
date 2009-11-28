package org.mati.geotech.model.qmap;

import org.mati.geotech.model.Rect;

abstract public class PathMaker {
	abstract public String makePathFor(Rect cell, Rect start);
}
