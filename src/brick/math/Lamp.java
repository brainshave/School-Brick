/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.math;

import brick.gui.BrickFrame;
import brick.image.Wall;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Szymon
 */
public class Lamp extends AbstractTransformChangeNotifier {

	double ks = 0.1, kd = 1;
	double m = 1.2;
	int d0 = 0;

	enum Shader {

		FLAT, GOURAUD_TRI, GOURAUD_RECT, PHONG
	}
	private ComboBoxModel model = new DefaultComboBoxModel(Shader.values()) {

		@Override
		public void setSelectedItem(Object anObject) {
			super.setSelectedItem(anObject);
			shader = Shader.valueOf(anObject.toString());
			recalc();
			frame.recalc();
			frame.repaint();
		}
	};
	protected Shader shader = Shader.FLAT;
	private Matrix1x4 viewer;
	private final static double[][] CORNERS = {{0, - 2 * Brick.SIZE, - 2 * Brick.SIZE, 1}};

	public Lamp() {
		super(CORNERS);
		recalc();
	}

	public int calculateBrithness(Matrix1x4 point, Vector vector) {
		Vector e = new Vector(point, viewer).normalize();
		Vector l = new Vector(point, corners3D[0]);

		double len = l.length();
		double brightness = (kd * vector.cosNorm(l)
				+ ks * Math.pow(e.cosNorm(l), m)) / (len * len);

		brightness *= 256 * 256;

		if (brightness < 0) {
			brightness = 0;
		}
		return (int) brightness;
	}

	public int applyBrigthness(int color, int brightness) {
		int checkMask = 0x01000000;
		if ((color & checkMask) != 0 || (color & 0xff000000) == 0) {
			return color;
		}
		int mask = 0xff;
		int tmp;
		for (int k = 0; k < 19; k += 8) {
			tmp = ((color & mask) >> k) + 1;
			tmp *= brightness;
			tmp >>>= 8;
			if (tmp > 255) {
				tmp = 255;
			} else if (tmp < 0) {
				tmp = 0;
			}
			color = (color & ~mask) | (tmp << k);
			mask <<= 8;
		}
		return color | checkMask;
	}

	public static void sortIndexes2D(int[] indexes, int[][] corners) {
		for (int i = 0; i < indexes.length; ++i) {
			for (int k = i + 1; k < indexes.length; ++k) {
				int[] ci = corners[indexes[i]];
				int[] ck = corners[indexes[k]];
				if (ck[1] < ci[1] || (ck[1] == ci[1] && ck[0] < ci[0])) {
					int tmp = indexes[k];
					indexes[k] = indexes[i];
					indexes[i] = tmp;
				}
			}
		}
	}

	public static void testSortedIndexes(int[] indexes, int[][] corners) {
		int a = corners[indexes[0]][1];
		int b = corners[indexes[1]][1];
		int c = corners[indexes[2]][1];
		if (a > b || b > c || a > c) {
			System.err.println("Zle sorotwanie indeksow: " + a + " " + b + " " + c);
		}
	}

	private static double safeDivide(double a, double b) {
		try {
			return a / b;
		} catch (ArithmeticException e) {
			System.err.println("Div 0");
			return Double.POSITIVE_INFINITY;
		}
	}

	private void gradientLine(int[] buff, int offset, int steps, int v1, int v2) {
		int end = offset + steps;

		double gradientStep = safeDivide(v2 - v1, Math.abs(steps) + 1);
		double gradVal = v1;
		int tmp;
		if (steps > 0) {

			++end;

			for (; offset <= end; ++offset) {
				try {
					buff[offset] = applyBrigthness(buff[offset], (int) gradVal);

				} catch (ArrayIndexOutOfBoundsException e) {
				}

				gradVal += gradientStep;
			}
		} else if (steps < 0) {

			++offset;

			for (; offset >= end; --offset) {
				try {
					buff[offset] = applyBrigthness(buff[offset], (int) gradVal);

				} catch (ArrayIndexOutOfBoundsException e) {
				}

				gradVal += gradientStep;
			}
		} else {
			try {
				buff[offset] = applyBrigthness(buff[offset], v1);
			} catch (ArrayIndexOutOfBoundsException e) {
			}
		}
	}

	private void gradientLine(int[] buff, int offset, int steps, Matrix1x4 p1, Matrix1x4 p2, Vector wallNormVec) {

		int end = offset + steps;

		Vector vectorStep = stepVector(p1, p2, Math.abs(steps) + 1);

		Matrix1x4 tmpPoint = p1.clone();
		if (steps > 0) {

			++end;

			for (; offset <= end; ++offset) {
				try {
					buff[offset] = applyBrigthness(buff[offset], calculateBrithness(tmpPoint, wallNormVec));

				} catch (ArrayIndexOutOfBoundsException e) {
				}

				tmpPoint.accumulate(vectorStep);
			}
		} else if (steps < 0) {

			++offset;

			for (; offset >= end; --offset) {
				try {
					buff[offset] = applyBrigthness(buff[offset], calculateBrithness(tmpPoint, wallNormVec));

				} catch (ArrayIndexOutOfBoundsException e) {
				}

				tmpPoint.accumulate(vectorStep);
			}
		}
	}

	private static int diffY(int i1, int i2, int[] indexes, int[][] corners) {
		return corners[indexes[i1]][1] - corners[indexes[i2]][1];
	}

	private static double stepX(int i1, int i2, int[] indexes, int[][] corners) {
		return safeDivide(
				corners[indexes[i1]][0] - corners[indexes[i2]][0],
				diffY(i1, i2, indexes, corners));
	}

	private static double stepB(int i1, int i2, int[] indexes, int[] brightnesses, int[][] corners) {
		return safeDivide(
				brightnesses[indexes[i1]] - brightnesses[indexes[i2]],
				diffY(i1, i2, indexes, corners));
	}

	private static Vector stepVector(Matrix1x4 from, Matrix1x4 to, double steps) {
		Vector v = new Vector(from, to);
		steps = Math.abs(steps);
		for (int i = 0; i < 4; ++i) {
			v.data[i] /= steps;
		}
		return v;
	}

	private static Vector stepVector(int i1, int i2, int[] indexes,
			Matrix1x4[] corners3D, int[][] corners2D) {

		double steps = diffY(i1, i2, indexes, corners2D);
		return stepVector(corners3D[indexes[i2]], corners3D[indexes[i1]], steps);
	}

	/**
	 * @param buff
	 * @param width buff's row width
	 * @param indexes sorted indexes
	 * @param wall.corners2D unsorted corners
	 * @param brightnesses unsorted brightnesses
	 */
	private void gouraudTriangle(Wall wall, int offset, int width, int[] indexes, int[] brightnesses) {
		int diffY_0_1 = diffY(1, 0, indexes, wall.corners2D);
		int diffY_0_2 = diffY(2, 0, indexes, wall.corners2D);

		double bStep0_1 = stepB(1, 0, indexes, brightnesses, wall.corners2D);
		double bStep0_2 = stepB(2, 0, indexes, brightnesses, wall.corners2D);
		double bStep1_2 = stepB(2, 1, indexes, brightnesses, wall.corners2D);

		double xStep0_1 = stepX(1, 0, indexes, wall.corners2D);
		double xStep0_2 = stepX(2, 0, indexes, wall.corners2D);
		double xStep1_2 = stepX(2, 1, indexes, wall.corners2D);


		double x1 = 0, x2 = 0;

		double b1, b2;
		b1 = b2 = brightnesses[indexes[0]];

		for (int y = 0; y < diffY_0_1; ++y) {
			gradientLine(wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5), (int) b1, (int) b2);

			x1 += xStep0_1;
			b1 += bStep0_1;

			x2 += xStep0_2;
			b2 += bStep0_2;

			offset += width;
		}

		b1 = brightnesses[indexes[1]];

		if (diffY_0_1 == 0) {
			x1 += width;
		}

		for (int y = diffY_0_1; y < diffY_0_2; ++y) {
			gradientLine(wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5), (int) b1, (int) b2);
			x1 += xStep1_2;
			b1 += bStep1_2;

			x2 += xStep0_2;
			b2 += bStep0_2;

			offset += width;
		}

	}

	private void phongTriangle(Wall wall, int offset, int width, int[] indexes) {
		int diffY_0_1 = diffY(1, 0, indexes, wall.corners2D);
		int diffY_0_2 = diffY(2, 0, indexes, wall.corners2D);

		double xStep_0_1 = stepX(1, 0, indexes, wall.corners2D);
		double xStep_0_2 = stepX(2, 0, indexes, wall.corners2D);
		double xStep_1_2 = stepX(2, 1, indexes, wall.corners2D);

		Vector vStep_0_1 = stepVector(1, 0, indexes, wall.corners3D, wall.corners2D);
		Vector vStep_0_2 = stepVector(2, 0, indexes, wall.corners3D, wall.corners2D);
		Vector vStep_1_2 = stepVector(2, 1, indexes, wall.corners3D, wall.corners2D);

		double x1 = 0, x2 = 0;
		Matrix1x4 p1 = wall.corners3D[indexes[0]].clone();
		Matrix1x4 p2 = wall.corners3D[indexes[0]].clone();

		for (int y = 0; y < diffY_0_1; ++y) {
			gradientLine(wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5), p1, p2, wall.vector);

			x1 += xStep_0_1;
			p1.accumulate(vStep_0_1);

			x2 += xStep_0_2;
			p2.accumulate(vStep_0_2);
			offset += width;
		}

		p1 = wall.corners3D[indexes[1]].clone();

		if (diffY_0_1 == 0) {
			x1 += width;
		}

		for (int y = diffY_0_1; y < diffY_0_2; ++y) {
			gradientLine(wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5), p1, p2, wall.vector);

			x1 += xStep_1_2;
			p1.accumulate(vStep_1_2);

			x2 += xStep_0_2;
			p2.accumulate(vStep_0_2);
			offset += width;
		}
	}

	public static boolean sameRow(int x1, int x2, int width) {
		boolean val = (x1 / width) == (x2 / width);
		if (!val) {
			System.err.print("ROWS: X1: " + x1 / width + " ");
			System.err.println("X2: " + x2 / width + "         " + val);
		}
		return val;
	}

	private void enlightTriangles(Wall wall, int width) {
		int brights[] = new int[4];
		for (int i = 0; i < 4; ++i) {
			brights[i] = calculateBrithness(wall.corners3D[i], wall.vector);
		}

		// podzial na 2 trojkaty
		int[][] triangleIndexes = {{0, 1, 2}, {0, 2, 3}};

		Polygon p = new Polygon();
		for (int[] corner : wall.corners2D) {
			p.addPoint(corner[0], corner[1]);
		}

		int minX = p.getBounds().x;
		int minY = p.getBounds().y;

		for (int[] indexes : triangleIndexes) {
			sortIndexes2D(indexes, wall.corners2D);

			int xoffset = wall.corners2D[indexes[0]][0] - minX;
			int yoffset = wall.corners2D[indexes[0]][1] - minY;
			int offset = xoffset + yoffset * width;
			if (shader == Shader.PHONG) {
				phongTriangle(wall, offset, width, indexes);
			} else {
				gouraudTriangle(wall, offset, width, indexes, brights);
			}
		}
	}

	/**
	 * Niedokonczona implementacja usredniania Gourouda dla calego czworokata
	 * @param wall
	 * @param width
	 */
	private void gouraudRectangle(Wall wall, int width) {

		int brights[] = new int[4];
		for (int i = 0; i < 4; ++i) {
			brights[i] = calculateBrithness(wall.corners3D[i], wall.vector);
		}

		int[] indexes = {0, 1, 2, 3};
		sortIndexes2D(indexes, wall.corners2D);

		Polygon p = new Polygon();
		for (int[] corner : wall.corners2D) {
			p.addPoint(corner[0], corner[1]);
		}

		int xoffset = wall.corners2D[indexes[0]][0] - p.getBounds().x;
		int yoffset = wall.corners2D[indexes[0]][1] - p.getBounds().y;
		int offset = xoffset + (yoffset) * width;

		int diffY_0_1 = diffY(1, 0, indexes, wall.corners2D);
		int diffY_1_2 = diffY(2, 1, indexes, wall.corners2D);
		int diffY_2_3 = diffY(3, 2, indexes, wall.corners2D);

		double stepX_0_1 = stepX(1, 0, indexes, wall.corners2D);
		double stepX_1_3 = stepX(3, 1, indexes, wall.corners2D);
		double stepX_0_2 = stepX(2, 0, indexes, wall.corners2D);
		double stepX_2_3 = stepX(3, 2, indexes, wall.corners2D);

		double stepB_0_1 = stepB(1, 0, indexes, brights, wall.corners2D);
		double stepB_1_3 = stepB(3, 1, indexes, brights, wall.corners2D);
		double stepB_0_2 = stepB(2, 0, indexes, brights, wall.corners2D);
		double stepB_2_3 = stepB(3, 2, indexes, brights, wall.corners2D);

		double x1 = 0;
		double x2 = 0;
		double b1;
		double b2;

		b1 = b2 = brights[indexes[0]];

		int y;
		for (y = 0; y < diffY_0_1; ++y) {

			gradientLine(wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5), (int) b1, (int) b2);

			x1 += stepX_0_2;
			b1 += stepB_0_2;

			x2 += stepX_0_1;
			b2 += stepB_0_1;

			offset += width;
		}
		if (diffY_0_1 == 0) {
			x2 = width;
		}
		for (y = 0; y < diffY_1_2; ++y) {

			gradientLine(wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5), (int) b1, (int) b2);

			x1 += stepX_0_2;
			b1 += stepB_0_2;

			x2 += stepX_1_3;
			b2 += stepB_1_3;

			offset += width;
		}
		for (y = 0; y < diffY_2_3; ++y) {

			gradientLine(wall.buff, (int) (offset + x1), (int) (x2 - x1 + 0.5), (int) b1, (int) b2);

			x1 += stepX_2_3;
			b1 += stepB_2_3;

			x2 += stepX_1_3;
			b2 += stepB_1_3;

			offset += width;
		}
	}

	/**
	 *
	 * @param wall
	 * @return true if wall is enlighten at all
	 */
	public boolean enlight(Wall wall, Matrix1x4 viewer, int width, int height) {
		this.viewer = viewer;

		switch (shader) {
			case GOURAUD_TRI:
			case PHONG:
				enlightTriangles(wall, width);
				break;

			case GOURAUD_RECT:
				gouraudRectangle(wall, width);
				break;

			case FLAT:
			default:
				// obliczanie sredniego punktu:

				Matrix1x4 point = new Matrix1x4(0, 0, 0, 0);
				for (Matrix1x4 m : wall.corners3D) {
					for (int i = 0; i < 4; ++i) {
						point.data[i] += m.data[i];
					}
				}
				for (int i = 0; i < 4; ++i) {
					point.data[i] /= 4;
				}
				int bright = calculateBrithness(point, wall.vector);
				int size = width * height;
				for (int i = 0; i < size; ++i) {
					wall.buff[i] = applyBrigthness(wall.buff[i], bright);
				}
				break;
		}

		return true;
	}

	public void recalcThis() {
	}

	@Override
	public void paint(Graphics2D g, int width, int height) {
		g.setColor(Color.YELLOW);
		g.fillOval(corners2D[0][0] + width / 2 - 3, corners2D[0][1] + height / 2 - 3, 6, 6);
	}


	// reszta to juz tylko gettery i settery

	public ComboBoxModel getModel() {
		return model;
	}

	public int getD0() {
		return d0;
	}

	public void setD0(int d0) {
		this.d0 = d0;
	}

	public double getKd() {
		return kd;
	}

	public void setKd(double kd) {
		this.kd = kd;
	}

	public double getKs() {
		return ks;
	}

	public void setKs(double ks) {
		this.ks = ks;
	}

	public double getM() {
		return m;
	}

	public void setM(double m) {
		this.m = m;
	}
	protected BrickFrame frame;

	public BrickFrame getFrame() {
		return frame;
	}

	public void setFrame(BrickFrame frame) {
		this.frame = frame;
	}
}
