package org.mati.geotech.model;

import java.util.LinkedList;
import java.util.List;

import org.mati.geotech.layers.AbstractMapLayer;

public class World extends Rect {

    List<AbstractMapLayer> layers= new LinkedList<AbstractMapLayer>();

    public World() {
        super(-180, -90, 360, 180);
    }

    public List<AbstractMapLayer> getLayers() {
        return layers;
    }

}
