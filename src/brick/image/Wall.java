/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.image;

import java.awt.Graphics2D;

/**
 * Klasa przechowująca informację o obrazkach umieszczonych na tej ścianie
 * jak i o jej współrzędnych (na płaszczyźnie 2d czy 3d)?
 * 3d - potrzebna przy obliczaniu oświetlenia dla każdego punktu
 * albo
 * 2d - jasność obliczona dla każdego wierzchołka...
 * @author Szymon
 */
public class Wall {

	private int[][] corners = new int[4][];
	private int[] brightnesses = new int[4]; // 0-255
	private int[][] images;
	private int actualImage = 0;

	public Wall(int[][] images) {
		if (images.length == 0) {
			throw new ArrayIndexOutOfBoundsException("There must be at least one image per wall.");
		}
		this.images = images;
	}

	public void setCorner(int num, int[] c) {
		corners[num] = c;
	}
	public void setCorners(int[] c0, int[] c1, int[] c2, int [] c3) {
		corners[0] = c0;
		corners[1] = c1;
		corners[2] = c2;
		corners[3] = c3;
	}

	public void paint(Graphics2D g) {
		// rysowanie samych kontur, póki co.
		for (int i = 0; i < 4; ++i) {
			int j = i + 1;
			if (j > 4) {
				j = 0;
			}
			g.drawLine(corners[i][0], corners[i][1], corners[j][0], corners[j][1]);
		}
	}

	public void paintRect(Graphics2D g, int x, int y, int width, int height) {
	}

	public void nextImage() {
	}
}
