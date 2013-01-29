package org.mati.geotech.gui;

import java.io.File;
import java.io.IOException;

import org.mati.geotech.GeoTechActivator;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

public class TexturesStorage {
    private Texture loadingTexture;
    private Texture downloadingTexture;
    private Texture notAvailableTexture;

    public TexturesStorage() {
        try {
            loadingTexture = TextureIO.newTexture(new File(GeoTechActivator
                    .getDefault().getPath("images/downloading.png")), false);
            notAvailableTexture = TextureIO.newTexture(new File(GeoTechActivator
                    .getDefault().getPath("images/error.png")), false);
            downloadingTexture = TextureIO.newTexture(new File(GeoTechActivator
                    .getDefault().getPath("images/downloading.png")), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Texture getDownloadingTexture() {
        return downloadingTexture;
    }
    
    public Texture getLoadingTexture() {
        return loadingTexture;
    }
    
    public Texture getNotAvailableTexture() {
        return notAvailableTexture;
    }
}
