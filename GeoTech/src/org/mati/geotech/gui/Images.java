package org.mati.geotech.gui;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Images {
    private static final String IMAGES_PATH = "/res/images/";

    public static Image getImageFree(String fullName) {
        java.net.URL imgURL = Images.class.getResource(fullName);
        if (imgURL != null) {
            return new ImageIcon(fullName).getImage();
        } else {
            System.err.println("Couldn't find file: " + fullName);
            return null;
        }
    }

    public static ImageIcon getImageIcon(String name) {
        java.net.URL imgURL = Images.class.getResource(IMAGES_PATH + name
                + ".png");
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + IMAGES_PATH + name
                    + ".png");
            return null;
        }
    }

    public static Image getImage(String name) {
        java.net.URL imgURL = Images.class.getResource(IMAGES_PATH + name
                + ".png");
        if (imgURL != null) {
            return new ImageIcon(imgURL).getImage();
        } else {
            System.err.println("Couldn't find file: " + IMAGES_PATH + name
                    + ".png");
            return null;
        }
    }
}
