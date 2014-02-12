package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>
 * A distribution that has 100% chance at a single point. This is useful for
 * representing constants that are added to dice rolls.
 */
public class ConstantDistribution extends AbstractDistribution {

	/**
	 * Useful as a starting value when summing lots of distributions
	 */
	static public final Distribution ZERO = new ConstantDistribution(0);

	private int x;

	public ConstantDistribution(int x) {
		this.x = x;
	}

	@Override
	public int lowerBound() {
		return x;
	}

	@Override
	public int upperBound() {
		return x + 1;
	}

	@Override
	public BigFraction getProbability(int x) {
		return x == this.x ? BigFraction.ONE : BigFraction.ZERO;
	}

	@Override
	public BigFraction getCumulativeProbability(int x) {
		return x > this.x ? BigFraction.ZERO : BigFraction.ONE;
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
