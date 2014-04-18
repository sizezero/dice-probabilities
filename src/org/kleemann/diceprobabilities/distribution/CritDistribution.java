package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>
 * A distribution that is similar to DieDistribution except that the highest
 * value of the die receives a bonus. Note: this makes CritDistribution not a
 * DieDistribution since DieDistributions are equal probabilities between 1 and
 * n.
 * 
 * <p>
 * e.g. a d6 is {1,2,3,4,5,6} but a crit +1 d6 is {1,2,3,4,5,7}
 */
public class CritDistribution extends AbstractDistribution {

	// the total number of sides of the die
	private final int sides;

	private final int bonus;
	
	// the equal probability of getting any side of the die
	// cache this for efficiency
	private final BigFraction probability;

	public CritDistribution(int sides, int bonus) {
		assert (sides > 0);
		this.sides = sides;
		this.bonus = bonus;
		this.probability = new BigFraction(1, sides);
	}

	@Override
	public int lowerBound() {
		return 1;
	}

	@Override
	public int upperBound() {
		return sides + 1 + bonus;
	}

	@Override
	public BigFraction getProbability(int x) {
		if ((x >= 1 && x < sides) || x == sides + bonus) {
			return probability;
		} else {
			return BigFraction.ZERO;
		}
	}
}
