package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>
 * I distribution that is similar to DieDistribution except that the highest
 * value of the die is replaced with the number of sides plus 1. Note: this
 * makes CritDistribution not a DieDistribution since DieDistributions are equal
 * probabilities between 1 and n.
 * 
 * <p>
 * e.g. a d6 is {1,2,3,4,5,6} but a crit d6 is {1,2,3,4,5,7}
 */
public class CritDistribution extends AbstractDistribution {

	// the total number of sides of the die
	private int sides;

	// the equal probability of getting any side of the die
	// cache this for efficiency
	private BigFraction probability;

	public CritDistribution(int sides) {
		assert (sides > 0);
		this.sides = sides;
		this.probability = new BigFraction(1, sides);
	}

	@Override
	public int lowerBound() {
		return 1;
	}

	@Override
	public int upperBound() {
		return sides + 2;
	}

	@Override
	public BigFraction getProbability(int x) {
		if ((x >= 1 && x < sides) || x == sides + 1) {
			return probability;
		} else {
			return BigFraction.ZERO;
		}
	}
}
