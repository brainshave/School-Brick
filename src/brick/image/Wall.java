/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.image;

import brick.math.Lamp;
import brick.math.Matrix1x4;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Klasa przechowująca informację o obrazkach umieszczonych na tej ścianie
 * jak i o jej współrzędnych (na płaszczyźnie 2d czy 3d)?
 * 3d - potrzebna przy obliczaniu oświetlenia dla każdego punktu
 * albo
 * 2d - jasność obliczona dla każdego wierzchołka...
 * @author Szymon
 */
public class Wall {

	private static final int MAX_SIZE = 1200;
	private int[][] corners = new int[4][];
	private Matrix1x4[] corners3D = new Matrix1x4[4];
	private int[] brightnesses = new int[4]; // 0-255
	private int[][] images = null;
	private int actualImage = 0;
	private int num = -1;
	private int[] buff = new int[MAX_SIZE * MAX_SIZE];
	BufferedImage image = new BufferedImage(MAX_SIZE, MAX_SIZE, BufferedImage.TYPE_INT_ARGB);
	private int dirtyX = MAX_SIZE, dirtyY = MAX_SIZE;
	public Polygon polygon;
	public Rectangle rect;

	public Wall() {
	}

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

	public void setCorner(int num, int[] c, Matrix1x4 m) {
		corners[num] = c;
		corners3D[num] = m;
	}

	public void setCorners(int[] c0, int[] c1, int[] c2, int[] c3) {
		corners[0] = c0;
		corners[1] = c1;
		corners[2] = c2;
		corners[3] = c3;
	}

	public void paint(Graphics2D g, int width, int height, Lamp lamp) {
		// rysowanie samych kontur, póki co.
		//{Geometrical-debug} System.out.println("Painting " + this);
		polygon = new Polygon();
		for (int[] c : corners) {
			polygon.addPoint(c[0] + width / 2, c[1] + height / 2);
		}

		rect = polygon.getBounds();

		//g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
		//g.setColor(color);
		//g.fillPolygon(p);

		int red = color.getRed();
		int blue = color.getBlue();
		int green = color.getGreen();
		int colorInt = 0xff000000 + (red << 16) + (green << 8) + blue;

		int x = 0, y = 0;
		if (rect.width > dirtyX) {
			dirtyX = rect.width;
		}
		if (rect.height > dirtyY) {
			dirtyY = rect.height;
		}

		if (dirtyX > MAX_SIZE) {
			dirtyX = MAX_SIZE;
		}
		if (dirtyY > MAX_SIZE) {
			dirtyY = MAX_SIZE;
		}

		try {
			for (x = 0; x < dirtyX; ++x) {
				for (y = 0; y < dirtyY; ++y) {
					buff[y * dirtyX + x] =
							polygon.contains(x + rect.x, y + rect.y) ? colorInt : 0;
				}
			}

			if(lamp.enlight(this)) {
				image.getRaster().setDataElements(0, 0, dirtyX, dirtyY, buff);
				g.drawImage(image, rect.x, rect.y, null);
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			System.err.println("Poza obszarem");
		}
		dirtyX = rect.width;
		dirtyY = rect.height;
		//g.setColor(color);
		//g.fillRect(rect.x, rect.y, rect.width, rect.height);

		g.setColor(Color.WHITE);
		g.drawPolygon(polygon);
		int avgX = 0;
		int avgY = 0;
		for (int[] c : corners) {
			avgX += c[0];
			avgY += c[1];
		}
		avgX /= 4;
		avgY /= 4;
		g.drawString(String.valueOf(num), avgX + width / 2, avgY + height / 2);
	}

	public void paintRect(Graphics2D g, int x, int y, int width, int height) {
	}

	public void nextImage() {
	}

	@Override
	public String toString() {
		String str = "{ ";
		for (int i = 0; i < 4; ++i) {
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
