/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package brick.image;

/**
 *
 * @author Szymon
 */
public interface TransformsChangeNotifyer {
	public static final int X = 1;
	public static final int Y = 2;
	public static final int Z = 3;
	/**
	 *
	 * @param which one of: X, Y, Z
	 * @param value
	 */
	public void setTransform(int which, int value);

	/**
	 *
	 * @param which one of: X, Y, Z
	 * @param value
	 */
	public void setAngle(int which, double value);

	/**
	 *
	 * @param which one of: X, Y, Z
	 * @param value
	 */
	public void setScale(int which, float value);

}
