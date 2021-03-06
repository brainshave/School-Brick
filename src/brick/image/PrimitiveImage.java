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
			buff[i] &= 0xfeffffff;
		}
	}
	private Wall wall;

	public void paintOn(Wall wall) {
		this.wall = wall;
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
		boolean flipY = false;

		for (int i = 0; i < 2; ++i) {
			int[] indexes = triangleIndexes[i];
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

			if (i == 1) {
				for (int k = 0; k < 3; ++k) {
					int[] mapping = {2, 1500, 0, 1};
					indexes[k] = mapping[indexes[k]];
				}
				flipY = true;
			}

			//System.out.println("INdeyx: " + indexes[0] + " " + indexes[1]);
			switch (indexes[0]) {
				case 0: {
					double[][] points = {{width, 0}, {width, 0}};
					if (indexes[1] == 1) {
						int[] steps = {diffY_0_1, diffY_1_2, diffY_0_2};
						subroutine(
								steps, points[0], points[1],
								diffY_0_1, A, C,
								diffY_1_2, B,
								wall.buff, offset, wall.dirtyX,
								xStep0_1, xStep1_2, xStep0_2, false, flipY);
					} else {
						int[] steps = {diffY_0_2, -diffY_1_2, diffY_0_1};
						subroutine(
								steps, points[0], points[1],
								diffY_0_1, C, A,
								diffY_1_2, B,
								wall.buff, offset, wall.dirtyX,
								xStep0_1, xStep1_2, xStep0_2, false, flipY);
					}
					break;
				}

				case 1: {
					double[][] points = {{0, 0}, {0, 0}};
					if (indexes[1] == 0) {
						int[] steps = {-diffY_0_1, diffY_0_2, diffY_1_2};

						subroutine(
								steps, points[0], points[1],
								diffY_0_1, A, B,
								diffY_1_2, C,
								wall.buff, offset, wall.dirtyX,
								xStep0_1, xStep1_2, xStep0_2, false, flipY);
					} else {
						int[] steps = {-diffY_0_2, diffY_0_1, -diffY_1_2};

						subroutine(
								steps, points[0], points[1],
								diffY_0_1, B, A,
								diffY_1_2, C,
								wall.buff, offset, wall.dirtyX,
								xStep0_1, xStep1_2, xStep0_2, false, flipY);
					}
					break;
				}
				case 2: {
					double[][] points = {{0, height}, {0, height}};
					if (indexes[1] == 0) {
						int[] steps = {diffY_1_2, -diffY_0_2, -diffY_0_1};
						subroutine(
								steps, points[0], points[1],
								diffY_0_1, C, B,
								diffY_1_2, A,
								wall.buff, offset, wall.dirtyX,
								xStep0_1, xStep1_2, xStep0_2, false, flipY);
					} else {
						int[] steps = {-diffY_1_2, -diffY_0_1, -diffY_0_2};
						subroutine(
								steps, points[0], points[1],
								diffY_0_1, B, C,
								diffY_1_2, A,
								wall.buff, offset, wall.dirtyX,
								xStep0_1, xStep1_2, xStep0_2, false, flipY);
					}
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
			scanline(p1, p2, buffOut, (int) (offset + x1), (int) (x2 - x1), flipX, flipY);

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
			scanline(p1, p2, buffOut, (int) (offset + x1), (int) (x2 - x1), flipX, flipY);

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

		int absSteps = Math.abs(steps) + 1;
		double stepX = (x2 - x1) / absSteps;
		double stepY = (y2 - y1) / absSteps;

		int end = offset + steps;

		int localX, localY;

		if (flipY) {
			y1 = height - 1 - y1;
			stepY *= -1;
			x1 = width - x1;
			stepX *= -1;
		}

		if (steps > 0) {
			//++end;
		} else {
			//++offset;
		}
		try {
			for (; offset != end; offset += offsetStep) {

				localX = (int) x1;
				localY = (int) y1;

				if (false) {
					int color = 0xfe000000;
					double rightAmount = 1 - x1 + localX;
					double upAmount = 1 - y1 + localY;

					int index = localX + localY * width;
					int color1 = this.buff[index];
					int color2 = this.buff[index + 1];
					index += width;
					int color3 = this.buff[index];
					int color4 = this.buff[index + 1];
					int part = 0, part1, part2, part3, part4;
					for (int i = 0; i < 25; i += 8) {
						part1 = (color1 & (0xff << i)) >> i;
						part2 = (color2 & (0xff << i)) >> i;
						part3 = (color3 & (0xff << i)) >> i;
						part4 = (color4 & (0xff << i)) >> i;
						part = (int) ((part1 * (1 - rightAmount) + part2 * rightAmount) * (1 - upAmount)
								+ (part3 * (1 - rightAmount) + part4 * rightAmount) * upAmount);
						if (part > 255) {
							part = 255;
						} else if (part < 0) {
							part = 0;
						}
						color |= part << i;
					}
					buffOut[offset] = color;
				} else {
					buffOut[offset] = this.buff[localX + localY * width];
				}

				x1 += stepX;
				y1 += stepY;

			}
		} catch (ArrayIndexOutOfBoundsException e) {
			//System.err.println("ERRR:" + this.buff.length + " " + e.getMessage());
		}
	}
}
