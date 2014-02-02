package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>This represents the distribution for a single die of some number
 * of sides.  Each probability in the range 1 to sides is of equal value
 * and sums to 1.
 */
public class DieDistribution implements Distribution {

	// the total number of sides of the die
	private int sides;
	
	// the equal probability of getting any side of the die
	// cache this for efficiency
	private BigFraction probability;
	
	public DieDistribution(int sides) {
		this.sides = sides;
		this.probability = new BigFraction(1,sides); 
	}
	
	public int getSides() { return sides; }
	
	@Override
	public int size() {
		// sides are one based. e.g. a six sided die uses the x values
		// 1 through 6 inclusive
		return sides+1;
	}

	@Override
	public BigFraction getProbability(int x) {
		if (x>=1 && x<=sides) {
			return probability;
		} else {
			return BigFraction.ZERO;
		}
	}

	@Override
	public BigFraction getCumulativeProbability(int x) {
		if (x>1 && x<=sides) {
			return probability.multiply(sides-x+1);
		} else if (x>sides) {
			return BigFraction.ZERO;
		} else {
			return BigFraction.ONE;
		}
	}

}
