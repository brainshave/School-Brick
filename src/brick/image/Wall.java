/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

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
	private int[][] images = null;
	private int actualImage = 0;
	private int num = -1;

	public Wall(){}

	public Wall(Color color, int num) {
		this.color = color;
		this.num = num;
	}

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

	public void paint(Graphics2D g, int width, int height) {
		// rysowanie samych kontur, póki co.
		//{Geometrical-debug} System.out.println("Painting " + this);
		Polygon p = new Polygon();
		for(int[] c: corners) {
			p.addPoint(c[0] + width/2, c[1] + height/2);
		}
		//g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
		g.setColor(color);
		g.fillPolygon(p);

		g.setColor(Color.BLACK);
		g.drawPolygon(p);
		int avgX = 0;
		int avgY = 0;
		for(int[] c : corners) {
			avgX += c[0];
			avgY += c[1];
		}
		avgX /= 4;
		avgY /= 4;
		g.drawString(String.valueOf(num), avgX + width/2, avgY + height/2);
	}

	public void paintRect(Graphics2D g, int x, int y, int width, int height) {
	}

	public void nextImage() {
	}

	@Override
	public String toString() {
		String str = "{ ";
		for(int i = 0; i < 4; ++i) {
			str += corners[i][0] + " " + corners[i][1] + ", ";
		}
		str += "}";
		return str;
	}
	protected Color color = Color.BLUE;

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
