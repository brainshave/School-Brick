/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package brick.math;

/**
 *
 * @author Szymon
 */
public class Matrix1x4 {
	public double[] data;

	public Matrix1x4() {
		this.data = new double[4];
	}

	public Matrix1x4(double[] data) {
		this.data = data;
	}
	public Matrix1x4 product(Matrix4x4 other) {
		double[] tmpdata = new double[4];
		for(int col = 0; col < 4; ++col) {
			for(int cross = 0; cross < 4; ++cross) {
				tmpdata[col] += data[cross] * other.data[col][cross];
			}
		}
		return new Matrix1x4(tmpdata);
	}

	@Override
	public String toString() {
		String str = "[ ";
		for(double i: data) {
			str += i + " ";
		}
		return str + "]";
	}
	
}
