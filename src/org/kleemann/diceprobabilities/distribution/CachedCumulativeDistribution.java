package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>
 * This is a simple way to cache values called from
 * Distribution.getCumulativeDistribution() so that the call is O(1) instead of
 * O(n). It should wrapped around a distribution after all the sums and
 * multiplies are done.
 */
class CachedCumulativeDistribution extends AbstractDistribution {

	private final Distribution d;
	private final BigFraction[] cums;

	CachedCumulativeDistribution(Distribution d) {
		this.d = d;
		if (d.lowerBound() < d.upperBound()) {
			cums = new BigFraction[d.upperBound() - d.lowerBound()];
			int i = cums.length - 1;
			cums[i] = d.getProbability(d.upperBound() - 1);
			--i;
			for (int x = d.upperBound() - 2; x >= d.lowerBound(); --x, --i) {
				cums[i] = cums[i + 1].add(d.getProbability(x));
			}
		} else {
			cums = null;
		}
	}

	@Override
	public int lowerBound() {
		return d.lowerBound();
	}

	@Override
	public int upperBound() {
		return d.upperBound();
	}

	@Override
	public BigFraction getProbability(int x) {
		return d.getProbability(x);
	}

	@Override
	public BigFraction getCumulativeProbability(int x) {
		if (x < d.lowerBound()) {
			return BigFraction.ONE;
		} else if (x >= d.upperBound()) {
			return BigFraction.ZERO;
		} else {
			return cums[x - d.lowerBound()];
		}
	}

	/**
	 * <p>
	 * This class has an efficient getCumulativeProbability(x)
	 */
	@Override
	public Distribution cacheCumulative() {
		return this;
	}
}
