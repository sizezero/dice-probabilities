package org.kleemann.diceprobabilities;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * 100% chance of returning zero.  Useful as an identity distrubution to
 * add other distributions to.  I.e. adding a distribution D to this will
 * result in D.
 */
public class ZeroDistribution implements Distribution {

	@Override
	public int size() {
		return 1;
	}

	@Override
	public BigFraction getProbability(int x) {
		return x==0 ? BigFraction.ONE : BigFraction.ZERO;
	}

	@Override
	public BigFraction getCumulativeProbability(int x) {
		return x>=0 ? BigFraction.ONE : BigFraction.ZERO;
	}

}
