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
			initSomething();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		for (int i = 0; i < buff.length; ++i) {
			//buff[i] &= 0xfeffffff;
		}
	}

	public void paintOnTMP(Wall wall) {
		for (int i = 0; i < wall.buff.length; ++i) {
			wall.buff[i] = 0;
		}

		int offsetIn = 0;
		int offsetOut = 0;
		double pX = 0;
		double pY = 0;
		double pXstep = (double) width / (double) wall.rect.width;
		double pYstep = (double) height / (double) wall.rect.height;

		try {
			for (int y = 0; y < height && y < wall.rect.height; ++y) {
				for (int x = 0; x < width && x < wall.rect.width; ++x) {
					if (wall.polygon.contains(wall.rect.x + x, wall.rect.y + y)) {
						int val = buff[(int) pX + (int) (Math.floor(pY) * width)];
						wall.buff[offsetOut + x] = val;
					}
					pX += pXstep;
				}
				pX = 0;
				pY += pYstep;
				offsetOut += wall.dirtyX;
				offsetIn += width;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}

	public void paintOnOLD(Wall wall) {
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

		//for (int[] indexes : triangleIndexes) {

		{
			int[] indexes = triangleIndexes[1];
			Lamp.sortIndexes2D(indexes, wall.corners2D);


//			int[] c0 = wall.corners2D[0], c1 = wall.corners2D[1];
//			if(c0[1] == c1[1] && c0[0] > c1[0])
//			{
//				int tmp = indexes[0];
//				indexes[0] = indexes[1];
//				indexes[1] = tmp;
//			}

			int xoffset = wall.corners2D[indexes[0]][0] - minX;
			int yoffset = wall.corners2D[indexes[0]][1] - minY;
			int offset = xoffset + yoffset * wall.dirtyX;

			int diffY_0_1 = Lamp.diffY(1, 0, indexes, wall.corners2D);
			int diffY_0_2 = Lamp.diffY(2, 0, indexes, wall.corners2D);
			int diffY_1_2 = Lamp.diffY(2, 1, indexes, wall.corners2D);

			double xStep0_1 = Lamp.stepX(1, 0, indexes, wall.corners2D);
			double xStep0_2 = Lamp.stepX(2, 0, indexes, wall.corners2D);
			double xStep1_2 = Lamp.stepX(2, 1, indexes, wall.corners2D);

			double p1_X = width;
			double p1_X_step = (double) -width / (double) diffY_0_1;

			double p1_Y = 0;
			double p1_Y_step = (double) height / (double) diffY_1_2;

			double p2_X = width;
			double p2_X_step = (double) -width / (double) diffY_0_2;

			double p2_Y = 0;
			double p2_Y_step = (double) height / (double) diffY_0_2;

			double x1 = 0, x2 = 0;

			boolean flipX = false, flipY = false;

			if (flipY) {
				p1_Y = height;
				p1_Y_step *= -1;
				p2_Y = height;
				p1_Y_step *= -1;
//				p2_X = 0;
//				p2_X_step *= -1;
			}

			for (int y = 0; y < diffY_0_1; ++y) {
				scanline(p1_X, 0, p2_X, p2_Y, wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5), flipX, flipY);

				x1 += xStep0_1;
				p1_X += p1_X_step;
				//p1_Y = 0 - zawsze w tej petli

				x2 += xStep0_2;
				p2_X += p2_X_step;
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
				scanline(0, p1_Y, p2_X, p2_Y, wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5), flipX, flipY);

				x1 += xStep1_2;
				//p1_X = 0 - zawsze w tej petli
				p1_Y += p1_Y_step;

				x2 += xStep0_2;
				p2_X += p2_X_step;
				p2_Y += p2_Y_step;

				offset += wall.dirtyX;
			}
		}
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

		//for (int[] indexes : triangleIndexes) {
		{
			int[] indexes = triangleIndexes[0];
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

			System.out.println("INdeyx: " + indexes[0] + " " + indexes[1]);
			switch (indexes[0]) {
				case 0: {
					int[] steps = {diffY_0_1, diffY_1_2, diffY_0_2};
					double[][] points = {{width, 0}, {width, 0}};
					subroutine(
							steps, points[0], points[1],
							diffY_0_1, A, C,
							diffY_1_2, B,
							wall.buff, offset, wall.dirtyX,
							xStep0_1, xStep1_2, xStep0_2, false, false);
					break;
				}

				case 1: {
					int[] steps = {-diffY_0_1, diffY_0_2, diffY_1_2};
					double[][] points = {{0, 0}, {0, 0}};
					subroutine(
							steps, points[0], points[1],
							diffY_0_1, A, B,
							diffY_1_2, C,
							wall.buff, offset, wall.dirtyX,
							xStep0_1, xStep1_2, xStep0_2, false, false);
					break;
				}
				case 2: {
					int[] steps = {diffY_1_2, -diffY_0_2, -diffY_0_1};
					double[][] points = {{0, height}, {0, height}};
					subroutine(
							steps, points[0], points[1],
							diffY_0_1, C, B,
							diffY_1_2, A,
							wall.buff, offset, wall.dirtyX,
							xStep0_1, xStep1_2, xStep0_2, false, false);
					break;
				}
			}
		}
	}
	static final int A = 0;
	static final int B = 1;
	static final int C = 2;
	private int[][] scanImageVersors;

	private void initSomething() {
		int[][] scanImageVersorsTMP = {
			/*A */{-width, 0},
			/*B*/ {0, height},
			/*C*/ {-width, height}
		};
		scanImageVersors = scanImageVersorsTMP;
	}

	private void subroutine(
			int[] steps, double[] p1, double[] p2,
			int firstLoopCycles, int first_P1_inc, int P2_inc,
			int secondLoopCycles, int second_P1_inc,
			int[] buffOut, int offset, int width,
			double first_x1_inc, double second_x1_inc, double x2_inc,
			boolean flipX, boolean flipY) {

		double[][] scanImageSteps = new double[3][2];

		for (int i = 0; i < 3; ++i) {
			for (int k = 0; k < 2; ++k) {
				scanImageSteps[i][k] = Lamp.safeDivide(scanImageVersors[i][k], steps[i]);
			}
		}

		double x1 = 0, x2 = 0;
		for (int y = 0; y < firstLoopCycles; ++y) {
			scanline(p1, p2, buffOut, (int) (offset + x1), (int) (x2 - x1 + 0.5), flipX, flipY);

			x1 += first_x1_inc;
			p1[0] += scanImageSteps[first_P1_inc][0];
			p1[1] += scanImageSteps[first_P1_inc][1];

			x2 += x2_inc;
			p2[0] += scanImageSteps[P2_inc][0];
			p2[1] += scanImageSteps[P2_inc][1];

			offset += width;
		}

		if (firstLoopCycles == 0) {
			x1 += width;
		}

		for (int y = 0; y < secondLoopCycles; ++y) {
			scanline(p1, p2, buffOut, (int) (offset + x1), (int) (x2 - x1 + 0.5), flipX, flipY);
			
			x1 += second_x1_inc;
			p1[0] += scanImageSteps[second_P1_inc][0];
			p1[1] += scanImageSteps[second_P1_inc][1];

			x2 += x2_inc;
			p2[0] += scanImageSteps[P2_inc][0];
			p2[1] += scanImageSteps[P2_inc][1];
			offset += width;
		}
	}

	private void scanline(double[] p1, double[] p2, int[] buffOut, int offset, int steps, boolean flipX, boolean flipY) {
		scanline(p1[0], p1[1], p2[0], p2[1], buffOut, offset, steps, flipX, flipY);
	}

	private void scanline(double x1, double y1, double x2, double y2,
			int[] buffOut, int offset, int steps, boolean flipX, boolean flipY) {

		int offsetStep = (int) Math.signum(steps);
		int absSteps = Math.abs(steps);
		double stepX = (x2 - x1) / absSteps;
		double stepY = (y2 - y1) / absSteps;

		int end = offset + steps;

		int localX, localY;

		if (flipY) {
			y1 = height - 1 - y1;
			stepY *= -1;
		}

		if (flipX) {
			x1 = width - x2;
			stepX *= -1;
		}

		try {
			for (; offset != end; offset += offsetStep) {
				localX = (int) x1;
				localY = (int) y1;
				buffOut[offset] = this.buff[localX + localY * width];
				x1 += stepX;
				y1 += stepY;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("ERRR:" + this.buff.length + " " + e.getMessage());
		}
	}
}
