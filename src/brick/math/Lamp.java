/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.math;

import brick.gui.BrickFrame;
import brick.image.Wall;
import java.awt.Color;
import java.awt.Graphics2D;
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
	protected Shader shader = Shader.FLAT;
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
				+ ks * Math.pow(e.cosNorm(l), m)) / (len*len);
		
		//double brightness = (kd * vector.cosNorm(l)) / (len*len);
		System.out.println(brightness);
		brightness *= 256*256;
		//if (brightness > 255) brightness = 255;
		if (brightness < 0) {
			brightness = 0;
		}
		return (int) brightness;
	}
	
	public int applyBrigthness(int color, int brightness) {
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
		return color;

//					red = (((color & 0xff0000) * bright) >> 8) & 0xff0000;
//					green = (((color & 0xff00) * bright) >> 8) & 0xff00;
//					blue = (((color & 0xff) * bright) >> 8) & 0xff;
//					red = ((color & 0xff0000) + (bright << 16)) & 0xff0000;
//					green = ((color & 0xff00) + (bright << 8)) & 0xff00;
//					blue = ((color & 0xff) + bright) & 0xff;
	}

	/**
	 *
	 * @param wall
	 * @return true if wall is enlighten at all
	 */
	public boolean enlight(Wall wall, Matrix1x4 viewer, int size) {

		this.viewer = viewer;

		switch (shader) {
			case GOURAUD:
				
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
