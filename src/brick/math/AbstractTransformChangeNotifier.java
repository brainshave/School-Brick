/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package brick.math;

import brick.image.TransformsChangeNotifyer;

/**
 *
 * @author Szymon
 */
public abstract class AbstractTransformChangeNotifier implements TransformsChangeNotifyer{
	public static double dgToRad = (double) Math.PI / 180;
	private Matrix4x4 transform = new Matrix4x4();
	private Matrix4x4 scale = new Matrix4x4();
	private Matrix4x4 rotX = new Matrix4x4();
	private Matrix4x4 rotY = new Matrix4x4();
	private Matrix4x4 rotZ = new Matrix4x4();
	
	protected Matrix4x4 endMatrix = new Matrix4x4();
	
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
		recalcThis();
	}
	
	abstract protected void recalcThis();

}
