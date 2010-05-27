package org.mati.geotech.model;

import java.util.LinkedList;
import java.util.List;

import org.mati.geotech.layers.AbstractMapLayer;

public class World extends Rect {

    List<AbstractMapLayer> layers= new LinkedList<AbstractMapLayer>();

    public World() {
        super(-180, -90, 360, 180);
    }

    public World(double x0, double y0, double x, double y) {
        super(x0, y0, x0-x, y0-y);
    }

    public List<AbstractMapLayer> getLayers() {
        return layers;
    }

}
