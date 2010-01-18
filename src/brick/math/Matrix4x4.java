/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.math;

/**
 *
 * @author Szymon
 */
public class Matrix4x4 {

	public double[][] data;

	public Matrix4x4() {
		double[][] tmpdata = {
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{0, 0, 0, 1}
		};
		this.data = tmpdata;
	}

	public Matrix4x4(double[][] data) {
		this.data = data;
	}

	public Matrix4x4 product(Matrix4x4 other) {
		double[][] tmpdata = new double[4][4];
		int row, col, cross;
		for(row = 0; row < 4; ++row) {
			for(col = 0; col < 4; ++col) {
				for(cross = 0; cross < 4; ++cross) {
					tmpdata[row][col] += data[row][cross] * other.data[cross][col];
				}
			}
		}
		return new Matrix4x4(tmpdata);
	}
	@Override
	public String toString() {
		String str = "[ ";
		for(double[] row : data) {
			for(double num : row) {
				str += num + " ";
			}
			str += "; ";
		}
		str += "]";
		return str;
	}
}
