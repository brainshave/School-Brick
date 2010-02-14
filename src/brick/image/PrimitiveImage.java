/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.image;

import brick.math.Lamp;
import java.awt.Polygon;
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
//		for(int i = 0; i < buff.length; ++i) {
//			buff[i] &= 0xfeffffff;
//		}
	}

	public void paintOn(Wall wall) {
		for (int i = 0; i < wall.buff.length; ++i) {
			wall.buff[i] = 0;
		}

		Polygon p = new Polygon();
		for (int[] corner : wall.corners2D) {
			p.addPoint(corner[0], corner[1]);
		}

		int minX = p.getBounds().x;
		int minY = p.getBounds().y;

		int[][] triangleIndexes = {{0, 1, 2}, {0, 2, 3}};

		for (int[] indexes : triangleIndexes) {
			Lamp.sortIndexes2D(indexes, wall.corners2D);

			int xoffset = wall.corners2D[indexes[0]][0] - minX;
			int yoffset = wall.corners2D[indexes[0]][1] - minY;
			int offset = xoffset + yoffset * wall.dirtyX;

			int diffY_0_1 = Lamp.diffY(1, 0, indexes, wall.corners2D);
			int diffY_0_2 = Lamp.diffY(2, 0, indexes, wall.corners2D);
			int diffY_1_2 = Lamp.diffY(2, 1, indexes, wall.corners2D);

			double xStep0_1 = Lamp.stepX(1, 0, indexes, wall.corners2D);
			double xStep0_2 = Lamp.stepX(2, 0, indexes, wall.corners2D);
			double xStep1_2 = Lamp.stepX(2, 1, indexes, wall.corners2D);

			double p1_X_step = (double) width / diffY_0_1;
			double p1_Y_step = (double) height / diffY_1_2;

			double p2_X_step = (double) width / diffY_0_2;
			double p2_Y_step = (double) height / diffY_0_2;

			//System.out.println("" + diffY_0_1 + ' ' + diffY_0_2 + ' ' + diffY_1_2);

			double x1 = 0, x2 = 0;
			double p1_X = width, p1_Y = 0, p2_X = width, p2_Y = 0;

			for (int y = 0; y < diffY_0_1; ++y) {
				scanline(p1_X, 0, p2_X, p2_Y, wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5));

				x1 += xStep0_1;
				p1_X -= p1_X_step;
				//p1_Y = 0 - zawsze w tej petli

				x2 += xStep0_2;
				p2_X -= p2_X_step;
				p2_Y += p2_Y_step;

				offset += wall.dirtyX;
			}

			//b1 = brightnesses[indexes[1]];

			if (diffY_0_1 == 0) {
				x1 += wall.dirtyX;
			}

			p1_X = 0;

			for (int y = diffY_0_1; y < diffY_0_2; ++y) {
				//gradientLine(wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5), (int) b1, (int) b2);
				scanline(0, p1_Y, p2_X, p2_Y, wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5));

				x1 += xStep1_2;
				//p1_X = 0 - zawsze w tej petli
				p1_Y += p1_Y_step;

				x2 += xStep0_2;
				p2_X -= p2_X_step;
				p2_Y += p2_Y_step;

				offset += wall.dirtyX;
			}
		}
	}

	private void scanline(double x1, double y1, double x2, double y2,
			int[] buffOut, int offset, int steps) {

		int absSteps = Math.abs(steps) + 1;
		//System.out.println("" + steps + " " + absSteps);
		double stepX = ((double) (x2 - x1)) / absSteps;
		double stepY = ((double) (y2 - y1)) / absSteps;

		int end = offset + steps;

		if (steps > 0) {
			++end;
			for (; offset <= end; ++offset) {
				buffOut[offset] = this.buff[(int) (x1 + y1 * width)];
				x1 += stepX;
				y1 += stepY;
			}
		} else {
			++offset;
			for (; offset >= end; --offset) {
				buffOut[offset] = this.buff[(int) (x1 + y1 * width)];
				x1 += stepX;
				y1 += stepY;
			}
		}
	}
}
