package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

public class ConstantDistribution implements Distribution {

	private int x;
	
	public ConstantDistribution(int x) {
		this.x = x;
	}
	
	@Override
	public int size() { return x+1; }

	@Override
	public BigFraction getProbability(int x) {
		return x==this.x ? BigFraction.ONE : BigFraction.ZERO;
	}

	@Override
	public BigFraction getCumulativeProbability(int x) {
		return x>=this.x ? BigFraction.ONE : BigFraction.ZERO;
	}
}
