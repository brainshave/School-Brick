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
 *         0-{-1, 1, 1}-------3-{ 1, 1, 1}
 *        /|                 /|
 *       / |                / |
 *      /  |               /  |
 *     /   |              /   |
 *    1-{-1, 1,-1}-------2-{ 1, 1,-1}
 *    |    |             |    |
 *    |    |             |    |
 *    |    4-{-1,-1, 1}--|----7-{ 1, -1, 1}
 *    |   /              |   /
 *    |  /               |  /
 *    | /                | /
 *    |/                 |/
 *    5-{-1,-1,-1}-------6-{ 1,-1,-1}
 * 
 *     0---3
 *     | 4 |
 * 0---1---2---3---0
 * | 0 | 1 | 2 | 3 |
 * 4---5---6---7---4
 *	   | 5 |
 *     4---7
 *
 * @author Szymon
 */
public class Brick implements TransformsChangeNotifyer {

	public static double dgToRad = (double) Math.PI / 180;
	public int[] buff;
	private Matrix4x4 transform = new Matrix4x4();
	private Matrix4x4 scale = new Matrix4x4();
	private Matrix4x4 rotX = new Matrix4x4();
	private Matrix4x4 rotY = new Matrix4x4();
	private Matrix4x4 rotZ = new Matrix4x4();
	private Matrix4x4 endMatrix = new Matrix4x4();
	private Matrix4x4 to2DMatrix = new Matrix4x4();
	Wall[] walls;
	private static final double SIZE = 1;
	private static final double[][] CORNERS = {
		/* 0 */ {-SIZE, SIZE, SIZE, 1},
		/* 1 */ {-SIZE, SIZE, -SIZE, 1},
		/* 2 */ {SIZE, SIZE, -SIZE, 1},
		/* 3 */ {SIZE, SIZE, SIZE, 1},
		/* 4 */ {-SIZE, -SIZE, SIZE, 1},
		/* 5 */ {-SIZE,-SIZE,-SIZE, 1},
		/* 6 */ {SIZE, -SIZE, -SIZE, 1},
		/* 7 */ {SIZE, -SIZE, SIZE, 1}
	};
	private final Matrix1x4[] originalCorners3D = new Matrix1x4[8];
	private final Matrix1x4[] corners3D = new Matrix1x4[8];
	private final int[][] corners2D = new int[8][2];
	//private
	final boolean[] visible = {true, true, true, true, true, true};

	public Brick() {
		System.out.println("Tworze kostke" );
		for(int i = 0; i < 8; ++i) {
			originalCorners3D[i] = new Matrix1x4(CORNERS[i]);
		}
	}

	public void setWalls(Wall[] walls) {
		if(walls == null) return;

		if (walls.length != 6) {
			throw new ArrayIndexOutOfBoundsException("Wrong number of walls: " + walls.length);
		}
		
		this.walls = walls;
		walls[0].setCorners(corners2D[0], corners2D[1], corners2D[4], corners2D[5]);
		walls[1].setCorners(corners2D[1], corners2D[2], corners2D[5], corners2D[6]);
		walls[2].setCorners(corners2D[2], corners2D[3], corners2D[6], corners2D[7]);
		walls[3].setCorners(corners2D[3], corners2D[0], corners2D[7], corners2D[4]);
		walls[4].setCorners(corners2D[0], corners2D[3], corners2D[1], corners2D[2]);
		walls[5].setCorners(corners2D[5], corners2D[6], corners2D[4], corners2D[7]);
	}

	public void paint(Graphics2D g) {
		for (int i = 0; i < 6; ++i) {
			if (visible[i]) {
				walls[i].paint(g);
			}
		}
	}

	private void calcCorners() {
		Matrix1x4 d3, d2;
		int[] corner;
		double focalPointFactor = (double) screenDistance / (screenDistance + brickDistance);
		for(int i = 0; i < 8; ++i) {
			d3 = originalCorners3D[i].product(endMatrix);
			corners3D[i] = d3;
			d2 = d3.product(to2DMatrix);
			corner = corners2D[i];
			corner[0] = (int) (focalPointFactor * d2.data[0]);
			corner[1] = (int) (focalPointFactor * d2.data[1]);
		}
	}

	private void recalc() {
		endMatrix = transform.product(rotX.product(rotY.product(rotZ.product(scale))));
		calcCorners();
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

	public void setTransform(int which, double value) {
		int index = index(which);
		if (index >= 0) {
			transform.data[index][3] = value;
		}
		recalc();
	}

	public void setAngle(int which, double value) {
		value *= dgToRad;
		double sin = Math.sin(value);
		double cos = Math.cos(value);
		switch (which) {
			case X: {
				double[][] tmp = {
					{1f, 0f, 0f, 0f},
					{0f, cos, sin, 0f},
					{0f, -sin, cos, 0f},
					{0f, 0f, 0f, 1f}
				};
				rotX.data = tmp;
				break;
			}
			case Y: {
				double[][] tmp = {
					{cos, 0f, -sin, 0f},
					{0f, 1f, 0f, 0f},
					{sin, 0f, cos, 0f},
					{0f, 0f, 0f, 1f}
				};
				rotY.data = tmp;
				break;
			}
			case Z: {
				double[][] tmp = {
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
		//recalc();
	}

	public void setScale(int which, double value) {
		int index = index(which);
		if (index >= 0) {
			scale.data[index][index] = value;
		}
		//recalc();
	}

	public Wall[] getWalls() {
		return walls;
	}
	protected int screenDistance = 5;

	public int getScreenDistance() {
		return screenDistance;
	}

	public void setScreenDistance(int screenDistance) {
		System.out.println("Brick Distance: " + screenDistance);
		this.screenDistance = screenDistance;
		to2DMatrix.data[3][2] = 1d / screenDistance;
		
	}
	protected int brickDistance = 5;

	public int getBrickDistance() {
		return brickDistance;
	}

	public void setBrickDistance(int brickDistance) {
		System.out.println("Brick Distance: " + brickDistance);
		this.brickDistance = brickDistance;
	}

}
