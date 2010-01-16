/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package brick.math;

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
	int[] corners = new int[8];
	int[] cornerBrightnesses = new int[8]; // 0-255

	void paintMe(Graphics2D g) {}
}
