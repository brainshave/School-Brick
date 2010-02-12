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
	Wall[] walls;
	public static final double SIZE = 100;
	private static final double[][] CORNERS = {
		/* 0 */{-SIZE, SIZE, SIZE, 1},
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
		/* 0 */{0, 1, 5, 4},
		/* 1 */ {1, 2, 6, 5},
		/* 2 */ {2, 3, 7, 6},
		/* 3 */ {3, 0, 4, 7},
		/* 4 */ {0, 3, 2, 1},
		/* 5 */ {5, 6, 7, 4}
	};
	private final boolean[] visible = {true, true, true, true, true, true};

	public Brick() {
		super(CORNERS);
		//{Geometrical-debug} System.out.println("Tworze kostke");
		Wall[] tmpWalls = {new Wall(Color.BLUE, 0), new Wall(Color.CYAN, 1), new Wall(Color.GREEN, 2), new Wall(Color.MAGENTA, 3), new Wall(Color.ORANGE, 4), new Wall(Color.YELLOW, 5)};
		setWalls(tmpWalls);
		recalc();
	}

	public void paint(Graphics2D g, int width, int height) {
		for (Wall w : walls) {
			if(w.isVisible()) {
				w.reBuff(width, height, lamp);
				w.paint(g);
			}
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
		walls[0].vector = new Vector(corners3D[6], corners3D[5]).normalize();
		walls[1].vector = new Vector(corners3D[4], corners3D[5]).normalize();
		walls[5].vector = new Vector(corners3D[1], corners3D[5]).normalize();
		walls[2].vector = walls[0].vector.invert();
		walls[3].vector = walls[1].vector.invert();
		walls[4].vector = walls[5].vector.invert();

		/**
		 * obliczamy katy prostej od punktu widzenia do sciany 
		 * dla kazdej sciany w jednym rogu
		 * kat miedzy wersorem normalnym sciany a wersorem poprowadzonym
		 * od punktu widzenia musi sie zawierac miedzy 90 a 270 stopni,
		 * czyli cos < 0
		 */
		Matrix1x4 viewer = new Matrix1x4(0, 0, -screenDistance - brickDistance, 0);
		for (Wall w : walls) {
			w.recalc(viewer);
		}
	}

	/**
	 * Trzeba wywolac po uzyciu ktorejkolwiek z funkcji setAngle(),
	 * setTransform(), setScale() i set*Distance()
	 */
	public void recalcThis() {
		determineVisibleWalls();
	}

	public Wall[] getWalls() {
		return walls;
	}

	public void setWalls(Wall[] walls) {
		if (walls == null) {
			return;
		}

		if (walls.length != 6) {
			throw new ArrayIndexOutOfBoundsException("Wrong number of walls: " + walls.length);
		}

		// ustawianie rogow dla scian z tabeli CORNERS_TO_WALLS
		this.walls = walls;
		for (int w = 0; w < 6; ++w) {
			for (int c = 0; c < 4; ++c) {
				int index = CORNERS_TO_WALLS[w][c];
				walls[w].setCorner(c, corners2D[index], corners3D[index]);
			}
		}
	}
	protected Lamp lamp;

	public Lamp getLamp() {
		return lamp;
	}

	public void setLamp(Lamp lamp) {
		this.lamp = lamp;
	}
}
