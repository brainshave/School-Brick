/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.math;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Szymon
 */
public class Lamp extends AbstractTransformChangeNotifier {

	private Matrix4x4 source, endPoint;
	private final static double[][] CORNERS = {{0, 2 * Brick.SIZE, - 2 * Brick.SIZE, 1}, {0, 0, 0, 0}};

	public Lamp() {
		super(CORNERS);
	}

	public void recalcThis() {
	}

	@Override
	protected void paint(Graphics2D g, int width, int height) {
		g.setColor(Color.YELLOW);
		g.fillOval(corners2D[0][0] + width / 2 - 3, corners2D[0][1] + height / 2 - 3, 6, 6);
		g.setColor(Color.GRAY);
		g.fillOval(corners2D[1][0] + width / 2 - 3, corners2D[1][1] + height / 2 - 3, 6, 6);
	}
}
