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
 */
public class ScaleCumulativeDistribution extends AbstractDistribution {

	private final int lower;
	private final int upper;
	private BigFraction[] vals;
	private BigFraction[] cums;

	private ScaleCumulativeDistribution(Distribution d) {
		// we will be calling getCumulativeProbability(x) a lot
		d = d.cacheCumulative();

		this.lower = d.lowerBound();
		this.upper = d.upperBound();
		final int n = upper - lower;
		this.vals = new BigFraction[n];
		this.cums = new BigFraction[n];
		for (int i = 0, x = lower; x < upper; ++i, ++x) {
			this.cums[i] = d.getCumulativeProbability(x);
		}
		
		// vals is uninitialized; static factories in this class must call
		// calculateValsFromCums() before returning the new object
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
		ScaleCumulativeDistribution n = new ScaleCumulativeDistribution(
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

	/**
	 * <p>
	 * If you succeed at a check you have to roll again and succeed.
	 */
	public static Distribution forcedReroll(Distribution d) {
		ScaleCumulativeDistribution n = new ScaleCumulativeDistribution(
				d);
		for (int i = 0; i < n.vals.length; ++i) {
			// P(S) = 1 - P(F)
			// P(new) = 1 - ( P(F) + P(S) * P(F) )
			final BigFraction pS = n.cums[i];
			final BigFraction pF = BigFraction.ONE.subtract(pS);
			n.cums[i] = BigFraction.ONE.subtract(pF.add(pS.multiply(pF)));
		}
		n.calculateValsFromCums();
		return n;
	}

	/**
	 * <p>
	 * When you attempt to defeat Caizarlu Zerren, after you make the roll, roll
	 * 1d6. On a 1 or 2, start the check over. Cards Played on the previous
	 * check do not affect the new check.
	 */
	public static Distribution caizarluZerren(Distribution d) {
		ScaleCumulativeDistribution n = new ScaleCumulativeDistribution(
				d);
		for (int i = 0; i < n.vals.length; ++i) {
			// P(S) = 1 - P(F)
			// P(new) = 1 - ( P(F) + P(S) * P(F) * (2/6)
			final BigFraction pS = n.cums[i];
			final BigFraction pF = BigFraction.ONE.subtract(pS);
			n.cums[i] = BigFraction.ONE.subtract(pF.add(pS.multiply(pF)
					.multiply(BigFraction.ONE_THIRD)));
		}
		n.calculateValsFromCums();
		return n;
	}

	/**
	 * <p>
	 * If you would defeat a bane with the Undead trait here, roll 1d6. On a 1,
	 * the monster is undefeated.
	 */
	public static Distribution desecratedVault(Distribution d) {
		ScaleCumulativeDistribution n = new ScaleCumulativeDistribution(
				d);
		final BigFraction fiveSixths = new BigFraction(5, 6);
		for (int i = 0; i < n.vals.length; ++i) {
			// P(S) = 1 - P(F)
			// P(new) = (1/6) * 0 + (5/6) * P(S)
			// P(new) = (5/6) * P(S)
			final BigFraction pS = n.cums[i];
			n.cums[i] = fiveSixths.multiply(pS);
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

	/**
	 * <p>
	 * This class has an efficient getCumulativeProbability(x)
	 */
	@Override
	public Distribution cacheCumulative() {
		return this;
	}
}
