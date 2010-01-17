/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package brick.gui;

import brick.math.Brick;
import javax.swing.JPanel;

/**
 *
 * @author Szymon
 */
public class RenderPanel extends JPanel{

	protected Brick brick;

	public Brick getBrick() {
		return brick;
	}

	public void setBrick(Brick brick) {
		this.brick = brick;
	}


}
