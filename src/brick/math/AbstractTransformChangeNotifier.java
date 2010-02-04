/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.math;

import brick.image.TransformsChangeNotifyer;
import java.awt.Graphics2D;

/**
 *
 * @author Szymon
 */
public abstract class AbstractTransformChangeNotifier implements TransformsChangeNotifyer {

	public static double dgToRad = (double) Math.PI / 180;
	private Matrix4x4 transform = new Matrix4x4();
	private Matrix4x4 scale = new Matrix4x4();
	private Matrix4x4 rotX = new Matrix4x4();
	private Matrix4x4 rotY = new Matrix4x4();
	private Matrix4x4 rotZ = new Matrix4x4();
	protected Matrix4x4 endMatrix = new Matrix4x4();
	private final double[][] corners;

	protected int brickDistance = 300;
	protected int screenDistance = 300;

	private final Matrix1x4[] originalCorners3D;
	protected final Matrix1x4[] corners3D;
	protected final int[][] corners2D;
	private Matrix4x4 to2DMatrix = new Matrix4x4();

	public AbstractTransformChangeNotifier(double[][] corners) {
		this.corners = corners;
		originalCorners3D = new Matrix1x4[corners.length];
		corners3D = new Matrix1x4[corners.length];
		corners2D = new int[corners.length][2];
		for (int i = 0; i < corners.length; ++i) {
			originalCorners3D[i] = new Matrix1x4(corners[i]);
		}
		to2DMatrix.data[2][2] = 0;
	}

	abstract protected void recalcThis();
	abstract protected void paint(Graphics2D g, int width, int height);

	/**
	 * Oblicza rzeczywiste polozenie rogow po wszystkich transformacjach
	 */
	private void calcCorners() {
		Matrix1x4 d3, d2;
		int[] corner;

		for (int i = 0; i < 8; ++i) {
			d3 = originalCorners3D[i].product(endMatrix);
			corners3D[i] = d3;
			d2 = d3.product(to2DMatrix);
			//{Geometrical-debug} System.out.println("Corner: " + i + ", 3D: " + d3 + "\n           2D: " + d2);
			corner = corners2D[i];
			double focalPointFactor = (double) screenDistance / (screenDistance + brickDistance + d3.data[2]);
			if (focalPointFactor < 0) {
				focalPointFactor = -focalPointFactor;
			}
			corner[0] = (int) (focalPointFactor * d2.data[0]);
			corner[1] = (int) (focalPointFactor * d2.data[1]);
			//{Geometrical-debug} System.out.println("x: " + corner[0] + " y: " + corner[1]);
		}
	}

	public void setTransform(int which, double value) {
		transform.data[index(which)][3] = value;

	}

	public void setAngle(int which, double value) {
		value *= dgToRad;
		double sin = Math.sin(value);
		double cos = Math.cos(value);
		switch (which) {
			case X: {
				double[][] tmp = {
					{1, 0, 0, 0},
					{0, cos, sin, 0},
					{0, -sin, cos, 0},
					{0, 0, 0, 1}
				};
				rotX.data = tmp;
				break;
			}
			case Y: {
				double[][] tmp = {
					{cos, 0, -sin, 0},
					{0, 1, 0, 0},
					{sin, 0, cos, 0},
					{0, 0, 0, 1}
				};
				rotY.data = tmp;
				break;
			}
			case Z: {
				double[][] tmp = {
					{cos, sin, 0, 0},
					{-sin, cos, 0, 0},
					{0, 0, 1, 0},
					{0, 0, 0, 1}
				};
				rotZ.data = tmp;
				break;
			}
			default:
				throw new ArrayIndexOutOfBoundsException("Bad axis given.");
		}
	}

	public void setScale(int which, double value) {
		int index = index(which);

		scale.data[index][index] = value;
	}

	private int index(int which) {
		int index = -1;
		switch (which) {
			case X:
				index = 0;
				break;
			case Y:
				index = 1;
				break;
			case Z:
				index = 2;
				break;
			default:
				throw new ArrayIndexOutOfBoundsException("Bad axis given.");
		}
		return index;
	}

	final public void recalc() {
		endMatrix = transform.product(rotX.product(rotY.product(rotZ.product(scale))));
		calcCorners();
		recalcThis();
	}


	public int getScreenDistance() {
		return screenDistance;
	}

	public void setScreenDistance(int screenDistance) {
		this.screenDistance = screenDistance;
		to2DMatrix.data[3][2] = 1d / screenDistance;

	}

	public int getBrickDistance() {
		return brickDistance;
	}

	public void setBrickDistance(int brickDistance) {
		this.brickDistance = brickDistance;
	}
}
