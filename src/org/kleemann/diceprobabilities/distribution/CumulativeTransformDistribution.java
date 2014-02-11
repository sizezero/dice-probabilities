package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>
 * This distribution performs operations on each of the distinct cumulative
 * distribution values. This is a common way to combine distributions.
 * 
 * <p>
 * These operations will not change the lower and upper bounds of the
 * distribution: possible things are still possible and impossible things are
 * still impossible.
 * 
 * <p>
 * The constructors of this class use Cumulative values extensively so it may be
 * more efficient to pass a CachedCumulativeDistribution
 */
public class CumulativeTransformDistribution extends AbstractDistribution {

	private final int lower;
	private final int upper;
	private BigFraction[] vals;
	private BigFraction[] cums;

	private CumulativeTransformDistribution(Distribution d) {
		this.lower = d.lowerBound();
		this.upper = d.upperBound();
		final int n = upper - lower;
		this.vals = new BigFraction[n];
		this.cums = new BigFraction[n];
		for (int i = 0, x = lower; x < upper; ++i, ++x) {
			this.cums[i] = d.getCumulativeProbability(x);
		}
		// vals is uninitialized
	}

	private void calculateValsFromCums() {
		BigFraction last = BigFraction.ZERO;
		for (int i = vals.length - 1; i >= 0; --i) {
			vals[i] = cums[i].subtract(last);
			last = cums[i];
		}
	}

	/**
	 * <p>
	 * If you fail the check you can try again.
	 */
	public static Distribution secondChance(Distribution d) {
		CumulativeTransformDistribution n = new CumulativeTransformDistribution(
				d);
		for (int i = 0; i < n.vals.length; ++i) {
			// P(S) = 1 - P(F)
			// P(new) = P(S) + P(F) * P(S)
			final BigFraction pS = n.cums[i];
			final BigFraction pF = BigFraction.ONE.subtract(pS);
			n.cums[i] = pS.add(pF.multiply(pS));
		}
		n.calculateValsFromCums();
		return n;
	}

	@Override
	public int lowerBound() {
		return lower;
	}

	@Override
	public int upperBound() {
		return upper;
	}

	@Override
	protected BigFraction getProbabilityBounded(int x) {
		return vals[x - lower];
	}

	@Override
	public BigFraction getCumulativeProbability(int x) {
		if (x < lower) {
			return BigFraction.ONE;
		} else if (x >= upper) {
			return BigFraction.ZERO;
		} else {
			return cums[x - lower];
		}
	}
}
