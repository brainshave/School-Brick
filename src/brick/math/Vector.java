/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package brick.math;

/**
 *
 * @author Szymon
 */
public class Vector extends Matrix1x4 {

	public double normFactor = 1;

	public Vector() {
		super();
	}

	public Vector(Matrix1x4 from, Matrix1x4 to) {
		super();
		for (int i = 0; i < data.length; ++i) {
			data[i] = to.data[i] - from.data[i];
		}
	}
	/**
	 *
	 * @return self!
	 */
	public Vector normalize() {
		// przelicznik normalizacji:
		normFactor = Math.sqrt(1.0 / (data[0] * data[0] + data[1] * data[1] + data[2] * data[2]));
		data[0] *= normFactor;
		data[1] *= normFactor;
		data[2] *= normFactor;
		// zapisujemy d, jesli chcielibysmy zdenormalizowac wektor:
		data[3] = normFactor;
		return this;
	}
	/**
	 *
	 * @return self!
	 */
	public Vector denormalize() {
		data[0] /= normFactor;
		data[1] /= normFactor;
		data[2] /= normFactor;
		data[3] = 1;
		normFactor = 1;
		return this;
	}

	public double length() {
		return Math.sqrt(data[0] * data[0] + data[1] * data[1] + data[2] * data[2]);
	}

	public Vector invert() {
		Vector tmp = new Vector();
		for (int i = 0; i < 3; ++i) {
			tmp.data[i] = -data[i];
		}
		tmp.data[3] = tmp.data[3];
		tmp.normFactor = normFactor;
		return tmp;
	}
}
