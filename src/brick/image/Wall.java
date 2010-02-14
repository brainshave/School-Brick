package brick.image;

import brick.math.Lamp;
import brick.math.Matrix1x4;
import brick.math.Vector;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Klasa przechowująca informację o obrazkach umieszczonych na tej ścianie
 * jak i o jej współrzędnych (na płaszczyźnie 2d czy 3d)?
 * 3d - potrzebna przy obliczaniu oświetlenia dla każdego punktu
 * albo
 * 2d - jasność obliczona dla każdego wierzchołka...
 * @author Szymon
 */
public class Wall {

	private boolean visible = false;
	private static final int MAX_SIZE = 1200;
	public int[][] corners2D = new int[4][];
	public Matrix1x4[] corners3D = new Matrix1x4[4];
	public Vector[] viewerVectors = new Vector[4];
	private int num = -1;
	LinkedList<PrimitiveImage> images = new LinkedList<PrimitiveImage>();
	Iterator<PrimitiveImage> imagesIterator = null;
	PrimitiveImage activeImage = null;
	public int[] buff = new int[MAX_SIZE * MAX_SIZE];
	BufferedImage image = new BufferedImage(MAX_SIZE, MAX_SIZE, BufferedImage.TYPE_INT_ARGB);
	public int dirtyX = 0, dirtyY = 0;
	public Polygon polygon;
	public Rectangle rect;
	public Vector vector;
	private Matrix1x4 viewer;

	public Wall() {
	}

	{
		try {
			this.addImage(new PrimitiveImage(new File("D:\\Szkola\\Grafika\\Brick\\wall.jpg")));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	public Wall(Color color, int num) {
		this.color = color;
		this.num = num;
	}

	public void setCorner(int num, int[] c, Matrix1x4 m) {
		corners2D[num] = c;
		corners3D[num] = m;
	}

	public void recalc(Matrix1x4 viewer) {
		this.viewer = viewer;
		for (int i = 0; i < 4; ++i) {
			viewerVectors[i] = new Vector(viewer, corners3D[i]).normalize();
		}
		visible = viewerVectors[0].cosNorm(vector) < 0;
	}

	public void reBuff(int width, int height, Lamp lamp) {
		polygon = new Polygon();
		for (int[] c : corners2D) {
			polygon.addPoint(c[0] + width / 2, c[1] + height / 2);
		}

		rect = polygon.getBounds();

		int red = color.getRed();
		int blue = color.getBlue();
		int green = color.getGreen();

		// BARDZO WAZNE: 0xfe w alpha po to by zaznaczyc oswietlone piksele!!
		int colorInt = 0xfe000000 | (red << 16) | (green << 8) | blue;

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
			if (activeImage == null) {
				int offset = 0;
				for (y = 0; y < dirtyY; ++y) {
					for (x = 0; x < dirtyX; ++x) {
						buff[offset + x] =
								polygon.contains(x + rect.x, y + rect.y) ? colorInt : 0;
					}
					offset += dirtyX;
				}
			} else {
				//System.out.println("Painting on wall");
				activeImage.paintOn(this);
			}

			if (lamp != null) {
				visible = lamp.enlight(this, viewer, dirtyX, dirtyY);
			}

			if (visible) {
				image.getRaster().setDataElements(0, 0, dirtyX, dirtyY, buff);
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		dirtyX = rect.width;
		dirtyY = rect.height;
	}

	public void paint(Graphics2D g) {
		if (visible) {
			g.drawImage(image, rect.x, rect.y, null);
		}

		g.setColor(Color.WHITE);
		g.drawPolygon(polygon);
//		int avgX = 0;
//		int avgY = 0;
//		for (int[] c : corners2D) {
//			avgX += c[0];
//			avgY += c[1];
//		}
//		avgX /= 4;
//		avgY /= 4;
		//g.drawString(String.valueOf(num), avgX + rect.width / 2, avgY + rect.height / 2);
	}

	public void paintRect(Graphics2D g, int x, int y, int width, int height) {
	}

	public void nextImage() {
		if (imagesIterator == null || !imagesIterator.hasNext()) {
			imagesIterator = images.iterator();
		}
		activeImage = imagesIterator.hasNext() ? imagesIterator.next() : null;
	}

	public void addImage(PrimitiveImage image) {
		images.addFirst(image);
		imagesIterator = images.iterator();
		nextImage();
	}

	@Override
	public String toString() {
		String str = "{ ";
		for (int i = 0; i < 4; ++i) {
			str += corners2D[i][0] + " " + corners2D[i][1] + ", ";
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

	public boolean isVisible() {
		return visible;
	}
}
