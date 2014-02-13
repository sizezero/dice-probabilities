package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>
 * This distribution performs operations on each of the distinct cumulative
 * distribution values. This is a common way to combine distributions.
 * 
 * <p>
 * These operations will not change the lower and upper bounds of the
 * distribution: possible things are still possible and impossible things are
 * still impossible.
 */
public class ScaleCumulativeDistribution extends AbstractDistribution {

	private final int lower;
	private final int upper;
	private BigFraction[] vals;
	private BigFraction[] cums;

	/**
	 * <p>
	 * Callers will need to implement a method that is capable of scaling each
	 * individual cumulative probability
	 */
	public interface Scale {
		/**
		 * <p>
		 * Given the old cumulative value and it's x value, return the new
		 * cumulative value
		 */
		BigFraction scale(BigFraction old, int x);
	}

	private ScaleCumulativeDistribution(Distribution d, Scale s) {
		// we will be calling getCumulativeProbability(x) a lot
		d = d.cacheCumulative();

		this.lower = d.lowerBound();
		this.upper = d.upperBound();
		final int n = upper - lower;
		this.vals = new BigFraction[n];
		this.cums = new BigFraction[n];
		for (int i = 0, x = lower; x < upper; ++i, ++x) {
			this.cums[i] = s.scale(d.getCumulativeProbability(x), x);
		}

		// initialize the probabilities base on cumulative
		BigFraction last = BigFraction.ZERO;
		for (int i = vals.length - 1; i >= 0; --i) {
			vals[i] = cums[i].subtract(last);
			last = cums[i];

			// not less than zero
			assert (vals[i].compareTo(BigFraction.ZERO) != -1);
			// not greater than one
			assert (vals[i].compareTo(BigFraction.ONE) != 1);
		}

	}

	public static Distribution scale(Distribution d, Scale s) {
		// I like factory methods over constructors
		return new ScaleCumulativeDistribution(d, s);
	}

	@Override
	public int lowerBound() {
		return lower;
	}

	@Override
	public int upperBound() {
		return upper;
	}

	@Override
	protected BigFraction getProbabilityBounded(int x) {
		return vals[x - lower];
	}

	@Override
	public BigFraction getCumulativeProbability(int x) {
		if (x < lower) {
			return BigFraction.ONE;
		} else if (x >= upper) {
			return BigFraction.ZERO;
		} else {
			return cums[x - lower];
		}
	}

	/**
	 * <p>
	 * This class has an efficient getCumulativeProbability(x)
	 */
	@Override
	public Distribution cacheCumulative() {
		return this;
	}
}
