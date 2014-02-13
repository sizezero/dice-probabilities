package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>
 * A helper class to implement some common Distribution routines
 */
abstract class AbstractDistribution implements Distribution {

	protected AbstractDistribution() {
	}

	@Override
	public BigFraction getProbability(int x) {
		if (x < lowerBound() || x >= upperBound()) {
			return BigFraction.ZERO;
		} else {
			return getProbabilityBounded(x);
		}
	}

	/**
	 * <p>
	 * Guaranteed not to be called outside the range of lowerBound() and
	 * upperBound()-1
	 * 
	 * <p>
	 * A derived class must either implement getProbability(x) or
	 * getProbabilityBounded(x)
	 */
	protected BigFraction getProbabilityBounded(int x) {
		return null;
	}

	/**
	 * <p>
	 * A simple default implementation. Takes O(n) with a large constant time.
	 */
	@Override
	public BigFraction getCumulativeProbability(int x) {
		BigFraction sum = BigFraction.ZERO;
		x = Math.max(x, lowerBound()); // no need to add up a bunch of zeros
		for (; x < upperBound(); ++x) {
			sum = sum.add(getProbability(x));
		}
		return sum;
	}

	/**
	 * <p>
	 * If the implementing class has a getCumulativeProbability(x) that performs
	 * O(1) then it should override this method to return <code>this</code>
	 */
	@Override
	public Distribution cacheCumulative() {
		return new CachedCumulativeDistribution(this);
	}

	@Override
	public boolean isZero() {
		return lowerBound() == 0 && upperBound() == 1
				&& getProbability(0).equals(BigFraction.ONE);
	}
}
