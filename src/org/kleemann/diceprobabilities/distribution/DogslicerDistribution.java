package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>
 * The Dogslicer is a peculiar distribution named after the pathfinder weapon
 * type. It's a d6 where a one becomes 3. Note: this makes DogslicerDistribution
 * not a DieDistribution since DieDistributions are equal probabilities between
 * 1 and n.
 */
public class DogslicerDistribution implements Distribution {

	// the total number of sides of the die
	private int sides;

	// the equal probability of getting any side of the die
	// cache this for efficiency
	private BigFraction probability;

	public DogslicerDistribution() {
		assert (sides > 0);
		this.sides = 6;
		this.probability = new BigFraction(1, sides);
	}

	@Override
	public int lowerBound() {
		return 2;
	}

	@Override
	public int upperBound() {
		return sides + 1;
	}

	@Override
	public BigFraction getProbability(int x) {
		if (x == 3) {
			return probability.multiply(2);
		} else if (x >= 2 && x <= sides) {
			return probability;
		} else {
			return BigFraction.ZERO;
		}
	}

	@Override
	public BigFraction getCumulativeProbability(int x) {
		BigFraction sum = BigFraction.ZERO;
		x = Math.max(x, lowerBound()); // no need to add up a bunch of zeros
		for (; x < upperBound(); ++x) {
			sum = sum.add(getProbability(x));
		}
		return sum;
	}
}
