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
import java.awt.Rectangle;
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

		FLAT, GOURAUD, PHONG
	}
	protected Shader shader = Shader.GOURAUD;
	//private Vector lightVector = null;
	private Matrix1x4 viewer;
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
	private Matrix4x4 source, endPoint;
	private final static double[][] CORNERS = {{0, - 2 * Brick.SIZE, - 2 * Brick.SIZE, 1}};

	public Lamp() {
		super(CORNERS);
		recalc();
	}

	public int calculateBrithness(Matrix1x4 point, Vector vector) {
		//return kd *
		//return 0;
		Vector e = new Vector(point, viewer).normalize();
		Vector l = new Vector(point, corners3D[0]);

		double len = l.length();
		double brightness = (kd * vector.cosNorm(l)
				+ ks * Math.pow(e.cosNorm(l), m)) / (len * len);

		//double brightness = (kd * vector.cosNorm(l)) / (len*len);
		//System.out.println(brightness);
		brightness *= 256 * 256;
		//if (brightness > 255) brightness = 255;
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
			tmp >>= 8;
			if (tmp > 255) {
				tmp = 255;
			}
			color = (color & ~mask) | (tmp << k);
			mask <<= 8;
		}
		return color | checkMask;

//					red = (((color & 0xff0000) * bright) >> 8) & 0xff0000;
//					green = (((color & 0xff00) * bright) >> 8) & 0xff00;
//					blue = (((color & 0xff) * bright) >> 8) & 0xff;
//					red = ((color & 0xff0000) + (bright << 16)) & 0xff0000;
//					green = ((color & 0xff00) + (bright << 8)) & 0xff00;
//					blue = ((color & 0xff) + bright) & 0xff;
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

		testSortedIndexes(indexes, corners);

//		int[][] sortedCorners = new int[3][];
//		for(int i = 0; i < 3; ++i) {
//			sortedCorners[i] = corners[indexes[i]];
//		}
//		return sortedCorners;
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

		// TODO: double -> int optimisation?
		steps += 2;
		double gradientStep = safeDivide(v2 - v1, steps);
		double gradVal = v1;
		int tmp;
		if (steps > 0) {
			--offset;
			++end;
			//System.out.print("+");
			for (; offset <= end; ++offset) {
				try {
					//buff[offset] = 0xff00ff00;
					buff[offset] = applyBrigthness(buff[offset], (int) gradVal);

				} catch (ArrayIndexOutOfBoundsException e) {
					//System.err.println(offset);
					//e.printStackTrace();
				}
				gradVal += gradientStep;
			}
		} else if (steps < 0) {
			++offset;
			--end;
			//System.out.print("-");
			for (; offset >= end; --offset) {
				try {
					//buff[offset] = 0xff0000ff;
					buff[offset] = applyBrigthness(buff[offset], (int) gradVal);
				} catch (ArrayIndexOutOfBoundsException e) {
					//System.err.println(offset);
					//e.printStackTrace();
				}
				gradVal -= gradientStep;
			}
		} else {
			try {
				buff[offset] = applyBrigthness(buff[offset], v1);
			} catch (ArrayIndexOutOfBoundsException e) {
				//e.printStackTrace();
				//System.err.println(offset);
			}
		}
	}

	/**
	 * @param buff
	 * @param width buff's row width
	 * @param indexes sorted indexes
	 * @param corners unsorted corners
	 * @param brightnesses unsorted brightnesses
	 */
	private void gradientTriangle(int[] buff, int offset, int width, int[] indexes,
			int[][] corners, int[] brightnesses) {

		int cx0 = corners[indexes[0]][0];
		int cy0 = corners[indexes[0]][1];
		int cb0 = brightnesses[indexes[0]];
		int cx1 = corners[indexes[1]][0];
		int cy1 = corners[indexes[1]][1];
		int cb1 = brightnesses[indexes[1]];
		int cx2 = corners[indexes[2]][0];
		int cy2 = corners[indexes[2]][1];
		int cb2 = brightnesses[indexes[2]];

		int diffY_0_1 = cy1 - cy0;
		int diffY_0_2 = cy2 - cy0;
		int diffY_1_2 = cy2 - cy1;

		double bStep0_1 = safeDivide(cb1 - cb0, diffY_0_1);
		double bStep0_2 = safeDivide(cb2 - cb0, diffY_0_2);
		double bStep1_2 = safeDivide(cb2 - cb1, diffY_1_2);

		double xStep0_1 = safeDivide(cx1 - cx0, diffY_0_1);
		double xStep0_2 = safeDivide(cx2 - cx0, diffY_0_2);
		double xStep1_2 = safeDivide(cx2 - cx1, diffY_1_2);

		double x1 = 0, x2 = 0;

		double b1 = cb0, b2 = cb0;

		for (int y = 0; y < diffY_0_1; ++y) {
			//sameRow((int)(offset + x1), (int)(offset + x1), width);
			gradientLine(buff, (int) (offset + x1), (int) (x2 - x1 - 0.5), (int) b1, (int) b2);
			x1 += xStep0_1;
			x2 += xStep0_2;
			b1 += bStep0_1;
			b2 += bStep0_2;
			offset += width;
		}

		// TODO: QUICK AND DIRTY!
		if (diffY_0_1 == 0) {
			x1 += width;
		}

		for (int y = diffY_0_1; y < diffY_0_2; ++y) {
			//sameRow((int)(offset + x1), (int)(offset + x1), width);
			gradientLine(buff, (int) (offset + x1), (int) (x2 - x1 - 0.5), (int) b1, (int) b2);
			x1 += xStep1_2;
			x2 += xStep0_2;
			b1 += bStep1_2;
			b2 += bStep0_2;
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

	/**
	 *
	 * @param wall
	 * @return true if wall is enlighten at all
	 */
	public boolean enlight(Wall wall, Matrix1x4 viewer, int width, int height) {
		this.viewer = viewer;

		switch (shader) {
			case GOURAUD:
				int brights[] = new int[4];
				for (int i = 0; i < 4; ++i) {
					brights[i] = calculateBrithness(wall.corners3D[i], wall.vector);
				}

				// podzial na 2 trojkaty
				int[] triangle1Indexes = {0, 1, 2};
				int[] triangle2Indexes = {0, 2, 3};

				// sortowanie indeksow po y-kach pozycji punktow w 2D:
				sortIndexes2D(triangle1Indexes, wall.corners2D);
				sortIndexes2D(triangle2Indexes, wall.corners2D);

				Polygon p = new Polygon();
				for (int[] corner : wall.corners2D) {
					p.addPoint(corner[0], corner[1]);
				}

				int minX = p.getBounds().x;
				int minY = p.getBounds().y;

				int xoffset = wall.corners2D[triangle1Indexes[0]][0] - minX;
				int yoffset = wall.corners2D[triangle1Indexes[0]][1] - minY;
				int offset1 = xoffset + (yoffset) * width;

				xoffset = wall.corners2D[triangle2Indexes[0]][0] - minX;
				yoffset = wall.corners2D[triangle2Indexes[0]][1] - minY;
				int offset2 = xoffset + (yoffset) * width;

				gradientTriangle(wall.buff, offset1, width, triangle1Indexes, wall.corners2D, brights);
				gradientTriangle(wall.buff, offset2, width, triangle2Indexes, wall.corners2D, brights);

				break;
			case PHONG:
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
		//lightVector = new Vector(corners3D[1], corners3D[0]);
	}

	@Override
	public void paint(Graphics2D g, int width, int height) {
		g.setColor(Color.YELLOW);
		g.fillOval(corners2D[0][0] + width / 2 - 3, corners2D[0][1] + height / 2 - 3, 6, 6);
//		g.setColor(Color.GRAY);
//		g.fillOval(corners2D[1][0] + width / 2 - 3, corners2D[1][1] + height / 2 - 3, 6, 6);
	}

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
