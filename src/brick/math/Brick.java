/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.math;

import brick.image.TransformsChangeNotifyer;
import brick.image.Wall;
import java.awt.Color;
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
public class Brick extends AbstractTransformChangeNotifier implements TransformsChangeNotifyer {
	public int[] buff;
	private Matrix4x4 to2DMatrix = new Matrix4x4();
	Wall[] walls;
	private static final double SIZE = 100;
	private static final double[][] CORNERS = {
		/* 0 */ {-SIZE, SIZE, SIZE, 1},
		/* 1 */ {-SIZE, SIZE, -SIZE, 1},
		/* 2 */ {SIZE, SIZE, -SIZE, 1},
		/* 3 */ {SIZE, SIZE, SIZE, 1},
		/* 4 */ {-SIZE, -SIZE, SIZE, 1},
		/* 5 */ {-SIZE, -SIZE, -SIZE, 1},
		/* 6 */ {SIZE, -SIZE, -SIZE, 1},
		/* 7 */ {SIZE, -SIZE, SIZE, 1}
	};
	/**
	 * Mapowanie rogow dla konkretnych scian
	 * Dla kazdej sciany po 4 rogi
	 */
	private static final int[][] CORNERS_TO_WALLS = {
		/* 0 */ {0, 1, 5, 4},
		/* 1 */ {1, 2, 6, 5},
		/* 2 */ {2, 3, 7, 6},
		/* 3 */ {3, 0, 4, 7},
		/* 4 */ {0, 3, 2, 1},
		/* 5 */ {5, 6, 7, 4}
	};
	private final Matrix1x4[] originalCorners3D = new Matrix1x4[8];
	private final Matrix1x4[] corners3D = new Matrix1x4[8];
	private final int[][] corners2D = new int[8][2];
	private final Vector[] wallVectors = new Vector[6];
	private final boolean[] visible = {true, true, true, true, true, true};

	public Brick() {
		//{Geometrical-debug} System.out.println("Tworze kostke");
		for (int i = 0; i < 8; ++i) {
			originalCorners3D[i] = new Matrix1x4(CORNERS[i]);
		}
		to2DMatrix.data[2][2] = 0;
		Wall[] tmpWalls = {new Wall(Color.BLUE, 0), new Wall(Color.CYAN, 1), new Wall(Color.GREEN, 2), new Wall(Color.MAGENTA, 3), new Wall(Color.ORANGE, 4), new Wall(Color.YELLOW, 5)};
		setWalls(tmpWalls);
		recalc();
	}

	public void setWalls(Wall[] walls) {
		if (walls == null) {
			return;
		}

		if (walls.length != 6) {
			throw new ArrayIndexOutOfBoundsException("Wrong number of walls: " + walls.length);
		}

		// ustawianie rogow dla sician z tabeli CORNERS_TO_WALLS
		this.walls = walls;
		for (int w = 0; w < 6; ++w) {
			for (int c = 0; c < 4; ++c) {
				walls[w].setCorner(c, corners2D[CORNERS_TO_WALLS[w][c]]);
			}
		}
	}

	public void paint(Graphics2D g, int width, int height) {
		for (int i = 0; i < 6; ++i) {
			if (visible[i]) {
				walls[i].paint(g, width, height);
			}
		}
	}

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

	private void determineVisibleWalls() {
		/*
		 * obliczamy wersory normalne dla trzech scian:
		 *   0,  1  i  5
		 * odpowiednio wersory [rogi]:
		 * 6-5, 4-5, 1-5
		 * dla reszty po prostu odwracamy wersory:
		 * 0:2, 1:3, 5:4
		 */
		wallVectors[0] = new Vector(corners3D[6], corners3D[5]).normalize();
		wallVectors[1] = new Vector(corners3D[4], corners3D[5]).normalize();
		wallVectors[5] = new Vector(corners3D[1], corners3D[5]).normalize();
		wallVectors[2] = wallVectors[0].invert();
		wallVectors[3] = wallVectors[1].invert();
		wallVectors[4] = wallVectors[5].invert();

		/**
		 * obliczamy katy prostej od punktu widzenia do sciany 
		 * dla kazdej sciany w jednym rogu
		 * kat miedzy wersorem normalnym sciany a wersorem poprowadzonym
		 * od punktu widzenia musi sie zawierac miedzy 90 a 270 stopni,
		 * czyli cos < 0
		 */
		Matrix1x4 viewer = new Matrix1x4(0, 0, -screenDistance - brickDistance, 0);
		for (int w = 0; w < 6; ++w) {
			Vector v = new Vector(viewer, corners3D[CORNERS_TO_WALLS[w][0]]).normalize();
			double cos = v.cosNorm(wallVectors[w]);
			visible[w] = cos < 0;
		}
	}

	/**
	 * Trzeba wywolac po uzyciu ktorejkolwiek z funkcji setAngle(),
	 * setTransform(), setScale() i set*Distance()
	 */
	public void recalcThis() {
		//{Geometrical-debug} System.out.println("    scale: " + scale);
		//{Geometrical-debug} System.out.println("     rotZ: " + rotZ);
		//{Geometrical-debug} System.out.println("     rotY: " + rotY);
		//{Geometrical-debug} System.out.println("     rotX: " + rotX);
		//{Geometrical-debug} System.out.println("transform: " + transform);
		//{Geometrical-debug} System.out.println("endMatrix: " + endMatrix);
		calcCorners();
		determineVisibleWalls();
	}

	

	

	public Wall[] getWalls() {
		return walls;
	}
	protected int screenDistance = 300;

	public int getScreenDistance() {
		return screenDistance;
	}

	public void setScreenDistance(int screenDistance) {
		this.screenDistance = screenDistance;
		to2DMatrix.data[3][2] = 1d / screenDistance;

	}
	protected int brickDistance = 300;

	public int getBrickDistance() {
		return brickDistance;
	}

	public void setBrickDistance(int brickDistance) {
		this.brickDistance = brickDistance;
	}
}
