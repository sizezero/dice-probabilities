package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * Similar to a normal die except ones and twos count as zeros
 */
public class DeathZoneDieDistribution extends AbstractDistribution {

	// the total number of sides of the die
	private final int sides;

	// the equal probability of getting any side of the die
	// cache this for efficiency
	private final BigFraction probability;

	public DeathZoneDieDistribution(int sides) {
		assert (sides > 2);
		this.sides = sides;
		this.probability = new BigFraction(1, sides);
	}

	public int getSides() {
		return sides;
	}

	@Override
	public int lowerBound() {
		return 0;
	}

	@Override
	public int upperBound() {
		// sides are one based. e.g. a six sided die uses the x values
		// 1 through 6 inclusive
		return sides + 1;
	}

	@Override
	public BigFraction getProbabilityBounded(int x) {
		if (x == 0) {
			return probability.multiply(2);
		} else if (x == 1 || x == 2) {
			return BigFraction.ZERO;
		} else {
			return probability;
		}
	}
}
