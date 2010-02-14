/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.gui;

import brick.image.PrimitiveImage;
import brick.image.Wall;
import brick.math.Brick;
import brick.math.Lamp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Szymon
 */
public class RenderPanel extends JPanel {

	protected Brick brick = null;

	public RenderPanel() {
		this.setDropTarget(new DropTarget() {

			{
				this.setActive(true);
				this.setDefaultActions(DnDConstants.ACTION_COPY);
			}

			@Override
			public synchronized void drop(DropTargetDropEvent dtde) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				Transferable tr = dtde.getTransferable();

				try {
					Wall w = null;
					if (brick != null) {
						Point p = dtde.getLocation();
						w = brick.getWallAt(p.x, p.y);

						if (w != null) {
							List<File> files = (List<File>) tr.getTransferData(DataFlavor.javaFileListFlavor);
							for (File f : files) {
								System.out.println(f.getAbsolutePath());
								w.addImage(new PrimitiveImage(f));
							}
						}
					}

				} catch (NullPointerException ex) {
					JOptionPane.showMessageDialog(null, "Zly typ przerzucanego obiektu", "ERROR", JOptionPane.ERROR_MESSAGE);
				} catch (UnsupportedFlavorException ex) {
					JOptionPane.showMessageDialog(null, "Zly typ przerzucanego obiektu", "ERROR", JOptionPane.ERROR_MESSAGE);
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(null, ex, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				System.gc();
				repaint();
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		if (g != null && this != null) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			//g.clearRect(0, 0, this.getWidth(), this.getHeight());
		}
		if (brick != null) {
			brick.paint((Graphics2D) g, this.getWidth(), this.getHeight());
		}
		if (lamp != null) {
			lamp.paint((Graphics2D) g, this.getWidth(), this.getHeight());
		}
	}

	public Brick getBrick() {
		return brick;
	}

	public void setBrick(Brick brick) {
		this.brick = brick;
	}
	protected Lamp lamp = null;

	public Lamp getLamp() {
		return lamp;
	}

	public void setLamp(Lamp lamp) {
		this.lamp = lamp;
	}

}
