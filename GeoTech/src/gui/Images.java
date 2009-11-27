package gui;

import javax.swing.ImageIcon;
import java.awt.Image;

public class Images {
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
		java.net.URL imgURL = Images.class.getResource("/res/images/" + name + ".png");
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("Couldn't find file: /res/images/" + name + ".png");
			return null;
		}
	}

	public static Image getImage(String name) {
		java.net.URL imgURL = Images.class.getResource("/res/images/" + name + ".png");
		if (imgURL != null) {
			return new ImageIcon(imgURL).getImage();
		} else {
			System.err.println("Couldn't find file: /res/images/" + name + ".png");
			return null;
		}
	}
}
