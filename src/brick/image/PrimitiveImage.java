/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.image;

import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Szymon
 */
public class PrimitiveImage {

	public int[] buff;
	public int width, height;

	public PrimitiveImage(File f) throws IOException {
		//File f = new File(filepath);
		BufferedImage imageInput = ImageIO.read(f);
		width = imageInput.getWidth();
		height = imageInput.getHeight();
		buff = new int[width * height];

		PixelGrabber grabber = new PixelGrabber(imageInput, 0, 0, width, height, buff, 0, width);

		try {
			grabber.grabPixels();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public void paintMe(Wall w) {
		
	}
}
