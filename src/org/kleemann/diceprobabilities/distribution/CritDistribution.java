package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

public class CritDistribution implements Distribution {

	// the total number of sides of the die
	private int sides;
	
	// the equal probability of getting any side of the die
	// cache this for efficiency
	private BigFraction probability;
	
	public CritDistribution(int sides) {
		assert(sides>0);
		this.sides = sides;
		this.probability = new BigFraction(1,sides); 
	}
	
	@Override
	public int lowerBound() {
		return 1;
	}

	@Override
	public int upperBound() {
		return sides+2;
	}

	@Override
	public BigFraction getProbability(int x) {
		if ((x>=1 && x<sides) || x==sides+1) {
			return probability;
		} else {
			return BigFraction.ZERO;
		}
	}

	@Override
	public BigFraction getCumulativeProbability(int x) {
		BigFraction sum = BigFraction.ZERO;
		x = Math.max(x, lowerBound()); // no need to add up a bunch of zeros
		for ( ; x<upperBound() ; ++x) {
			sum = sum.add(getProbability(x));
		}
		return sum;
	}
}
