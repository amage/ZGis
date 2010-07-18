package org.mati.geotech.model;

import java.util.LinkedList;
import java.util.List;

import org.mati.geotech.layers.AbstractLayer;

public class World extends Rect {

    List<AbstractLayer> layers= new LinkedList<AbstractLayer>();

    public World() {
        super(-180, -90, 360, 180);
    }

    public World(double x0, double y0, double x, double y) {
        super(x0, y0, x0-x, y0-y);
    }

    public List<AbstractLayer> getLayers() {
        return layers;
    }

}
