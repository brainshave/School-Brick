/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.math;

import brick.image.TransformsChangeNotifyer;
import brick.image.Wall;
import java.awt.Graphics2D;

/**
 *
 * @author Szymon
 */
public class Brick implements TransformsChangeNotifyer {

	public static float dgToRad = (float) Math.PI / 180;
	public int[] buff;
	Matrix4x4 transform = new Matrix4x4();
	Matrix4x4 scale = new Matrix4x4();
	Matrix4x4 rotX = new Matrix4x4();
	Matrix4x4 rotY = new Matrix4x4();
	Matrix4x4 rotZ = new Matrix4x4();
	Matrix4x4 endMatrix = new Matrix4x4();
	Wall[] walls;
	private final boolean[] visible = {true, true, true, true, true, true};

	public Brick() {
	}

	public Brick(Wall[] walls) {
		if (walls.length != 6) {
			throw new ArrayIndexOutOfBoundsException("Wrong number of walls: " + walls.length);
		}
		this.walls = walls;
	}

	public void paint(Graphics2D g) {
		for (int i = 0; i < 6; ++i) {
			if (visible[i]) {
				walls[i].paint(g);
			}
		}
	}

	private void recalc() {
		endMatrix = transform.product(rotX.product(rotY.product(rotZ.product(scale))));
		// TODO: określanie które ściany mają być widoczne?
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

	public void setTransform(int which, int value) {
		int index = index(which);
		if (index >= 0) {
			transform.data[index][3] = value;
		}
		recalc();
	}

	public void setAngle(int which, double value) {
		value *= dgToRad;
		float sin = (float) Math.sin(value);
		float cos = (float) Math.cos(value);
		switch (which) {
			case X: {
				float[][] tmp = {
					{1f, 0f, 0f, 0f},
					{0f, cos, sin, 0f},
					{0f, -sin, cos, 0f},
					{0f, 0f, 0f, 1f}
				};
				rotX.data = tmp;
				break;
			}
			case Y: {
				float[][] tmp = {
					{cos, 0f, -sin, 0f},
					{0f, 1f, 0f, 0f},
					{sin, 0f, cos, 0f},
					{0f, 0f, 0f, 1f}
				};
				rotY.data = tmp;
				break;
			}
			case Z: {
				float[][] tmp = {
					{cos, sin, 0f, 0f},
					{-sin, cos, 0f, 0f},
					{0f, 0f, 0f, 1f, 0f},
					{0f, 0f, 0f, 0f, 1f}
				};
				rotZ.data = tmp;
				break;
			}
			default:
				throw new ArrayIndexOutOfBoundsException("Bad axis given.");
		}
		recalc();
	}

	public void setScale(int which, float value) {
		int index = index(which);
		if (index >= 0) {
			scale.data[index][index] = value;
		}
		recalc();
	}


	public Wall[] getWalls() {
		return walls;
	}

	public void setWalls(Wall[] walls) {

		this.walls = walls;
	}
}
