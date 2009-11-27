package model.qmap;

import model.Rect;

abstract public class PathMaker {
	abstract public String makePathFor(Rect cell, Rect start);
}
