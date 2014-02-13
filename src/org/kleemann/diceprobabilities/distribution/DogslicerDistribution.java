package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>
 * The Dogslicer is a peculiar distribution named after the pathfinder weapon
 * type. It's a d6 where a one becomes 3. Note: this makes DogslicerDistribution
 * not a DieDistribution since DieDistributions are equal probabilities between
 * 1 and n.
 */
public class DogslicerDistribution extends AbstractDistribution {

	// the total number of sides of the die
	public final static int SIDES = 6;;

	// the equal probability of getting any side of the die
	// cache this for efficiency
	private BigFraction probability;

	public DogslicerDistribution() {
		this.probability = new BigFraction(1, SIDES);
	}

	@Override
	public int lowerBound() {
		return 2;
	}

	@Override
	public int upperBound() {
		return SIDES + 1;
	}

	@Override
	public BigFraction getProbabilityBounded(int x) {
		if (x == 3) {
			return probability.multiply(2);
		} else {
			return probability;
		}
	}
}
