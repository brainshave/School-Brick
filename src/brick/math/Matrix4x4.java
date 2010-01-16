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

	public float[][] data;

	public Matrix4x4() {
		float[][] tmpdata = {
			{1f, 0f, 0f, 0f},
			{0f, 1f, 0f, 0f},
			{0f, 0f, 1f, 0f},
			{0f, 0f, 0f, 1f}
		};
		this.data = tmpdata;
	}

	public Matrix4x4(float[][] data) {
		this.data = data;
	}

	public Matrix4x4 product(Matrix4x4 other) {
		return new Matrix4x4();
	}

	public Matrix1x4 product(Matrix1x4 other) {
		return new Matrix1x4();
	}
}
