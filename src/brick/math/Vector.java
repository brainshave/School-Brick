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

	public Vector(double a, double b, double c, double d) {
		super(a, b, c, d);
	}

	
	/**
	 * Normalize this vector, reversable via denormalize(), function returns
	 * <b>this</b> for chaining methods.
	 * @return this
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
	 * Reverse Normalization. No effect if normalize() was not previously
	 * called. Returns <b>this</b> for chaining methods.
	 * @return this
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

	/**
	 * Get new Vector in opposite direction.
	 * @return
	 */
	public Vector invert() {
		Vector tmp = new Vector();
		for (int i = 0; i < 3; ++i) {
			tmp.data[i] = -data[i];
		}
		tmp.data[3] = tmp.data[3];
		tmp.normFactor = normFactor;
		return tmp;
	}

	/**
	 * Calculates cos for two <b>normalized</b> vectors.
	 * @param other
	 * @return
	 */
	public double cosNorm(Vector other) {
		return data[0] * other.data[0] + data[1] * other.data[1] + data[2] * other.data[2];
	}
}
