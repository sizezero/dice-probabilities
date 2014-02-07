package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>This is a simple way to cache values called from Distribution.getCumulativeDistribution() 
 * so that the call is O(1) instead of O(n).
 * It should wrapped around a distribution after all the sums and multiplies are done. 
 */
public class CachedCumulativeDistribution implements Distribution {

	private final Distribution d;
	private final BigFraction[] cums;
	
	public CachedCumulativeDistribution(Distribution d) {
		this.d = d;
		if (d.size() > 0) {
			cums = new BigFraction[d.size()];
			cums[d.size()-1] = d.getProbability(d.size()-1);
			for (int x=d.size()-2 ; x>=0 ; --x) {
				cums[x] = cums[x+1].add(d.getProbability(x));
			}
		} else {
			cums = null;
		}
	}

	@Override
	public int size() { return d.size(); }

	@Override
	public BigFraction getProbability(int x) {
		return d.getProbability(x);
	}

	@Override
	public BigFraction getCumulativeProbability(int x) {
		if (x < 0) {
			return BigFraction.ONE;
		} else if (x >= d.size()) {
			return BigFraction.ZERO;
		} else {
			return cums[x];
		}
	}

}
