package org.mati.geotech.layers;

import javax.media.opengl.GL;

import org.mati.geotech.gui.ViewPort;

public abstract class AbstractLayer {
    protected ViewPort viewPort;
    private int layerWidth = 1;
    private int layerHeight = 1;

    public AbstractLayer(ViewPort vp) {
        viewPort = vp;
    }

    public abstract void paint(GL gl);

    public void setSize(int w, int h) {
        layerWidth= w;
        layerHeight= h;
    }

    public int getScreenWidth() {
        return layerWidth;
    }

    public int getScreenHeight() {
        return layerHeight;
    }
}
