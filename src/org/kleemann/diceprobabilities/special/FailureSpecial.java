package org.kleemann.diceprobabilities.special;

import org.apache.commons.math3.fraction.BigFraction;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.ScaleCumulativeDistribution;

import android.util.SparseIntArray;

/**
 * <p>
 * After a successful check, failure still occurs with the given probability.
 */
class FailureSpecial extends AbstractSpecial {

	private BigFraction success;

	public FailureSpecial(String title, String description, BigFraction failure) {
		super(title, description);
		this.success = BigFraction.ONE.subtract(failure);
	}

	@Override
	public Distribution getDistribution(SparseIntArray sidesToCount) {
		Distribution d = super.getDistribution(sidesToCount);

		// we need to adjust the cumulative distribution past it's lower bound
		// all the way to zero since failure always results if a the final
		// success fails rolled.
		// There is no 100% with this distribution.

		return ScaleCumulativeDistribution.scale(d, 0, d.upperBound(),
				new ScaleCumulativeDistribution.Scale() {
					public BigFraction scale(BigFraction pS, int x) {
						// P(S) = 1 - P(F)
						// P(new) = (1/6) * 0 + (5/6) * P(S)
						// P(new) = (5/6) * P(S)
						return success.multiply(pS);
					}
				});

	}
}
