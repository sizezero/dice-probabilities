package org.kleemann.diceprobabilities;

import org.apache.commons.math3.fraction.BigFraction;

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
		if (x>=1 && x<=sides) {
			return probability.multiply(x);
		} else if (x>sides) {
			return BigFraction.ONE;
		} else {
			return BigFraction.ZERO;
		}
	}

}
