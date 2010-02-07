/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.math;

import brick.image.Wall;
import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Szymon
 */
public class Lamp extends AbstractTransformChangeNotifier {

	enum Shader {
		FLAT, GOURAUD, PHONG
	}

	protected Shader shader = Shader.FLAT;

	private ComboBoxModel model = new DefaultComboBoxModel(Shader.values()) {

		@Override
		public void setSelectedItem(Object anObject) {
			super.setSelectedItem(anObject);
			shader = Shader.valueOf(anObject.toString());
		}

	};

	private Matrix4x4 source, endPoint;
	private final static double[][] CORNERS = {{0, - 2 * Brick.SIZE, - 2 * Brick.SIZE, 1}, {0, -Brick.SIZE, -Brick.SIZE, 1}};

	public Lamp() {
		super(CORNERS);
		recalc();
	}

	/**
	 *
	 * @param wall
	 * @return true if wall is enlighten at all
	 */
	public boolean enlight(Wall wall) {
		

		return true;
	}

	public void recalcThis() {
	}

	@Override
	public void paint(Graphics2D g, int width, int height) {
		g.setColor(Color.YELLOW);
		g.fillOval(corners2D[0][0] + width / 2 - 3, corners2D[0][1] + height / 2 - 3, 6, 6);
		g.setColor(Color.GRAY);
		g.fillOval(corners2D[1][0] + width / 2 - 3, corners2D[1][1] + height / 2 - 3, 6, 6);
	}

	public ComboBoxModel getModel() {
		return model;
	}


}
