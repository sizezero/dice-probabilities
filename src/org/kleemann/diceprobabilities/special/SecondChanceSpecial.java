package org.kleemann.diceprobabilities.special;

import org.apache.commons.math3.fraction.BigFraction;
import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.ScaleCumulativeDistribution;

import android.content.res.Resources;
import android.util.SparseIntArray;

/**
 * <p>
 * If the first roll fails you get a second chance to roll in order to succeed.
 */
class SecondChanceSpecial extends AbstractSpecial {

	public SecondChanceSpecial(Resources r) {
		super(r.getString(R.string.special_second_chance_title), r
				.getString(R.string.special_second_chance_description));
	}

	@Override
	public Distribution getDistribution(SparseIntArray sidesToCount) {
		Distribution d = super.getDistribution(sidesToCount);

		return ScaleCumulativeDistribution.scale(d,
				new ScaleCumulativeDistribution.Scale() {
					public BigFraction scale(BigFraction pS, int x) {
						// P(S) = 1 - P(F)
						// P(new) = P(S) + P(F) * P(S)
						final BigFraction pF = BigFraction.ONE.subtract(pS);
						return pS.add(pF.multiply(pS));
					}
				});
	}
}
