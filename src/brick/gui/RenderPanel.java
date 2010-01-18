/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.gui;

import brick.math.Brick;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author Szymon
 */
public class RenderPanel extends JPanel {

	protected Brick brick = null;

	public Brick getBrick() {
		return brick;
	}

	public void setBrick(Brick brick) {
		this.brick = brick;
	}

	@Override
	public void paintComponent(Graphics g) {
		if (g != null && this != null) {
			g.clearRect(0, 0, this.getWidth(), this.getHeight());
		}
		if (brick != null) {
			brick.paint((Graphics2D) g, this.getWidth(), this.getHeight());
		}
	}
}