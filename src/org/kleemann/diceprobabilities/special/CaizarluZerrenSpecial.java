package org.kleemann.diceprobabilities.special;

import org.apache.commons.math3.fraction.BigFraction;
import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.ScaleCumulativeDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;

import android.content.res.Resources;
import android.util.SparseIntArray;

/**
 * <p>
 * When you attempt to defeat Caizarlu Zerren, after you make the roll, roll
 * 1d6. On a 1 or 2, start the check over. Cards Played on the previous
 * check do not affect the new check.
 */
class CaizarluZerrenSpecial extends AbstractSpecial {
	public CaizarluZerrenSpecial(Resources r) {
		super(r.getString(R.string.special_caizerlu_zerren_title), r
				.getString(R.string.special_caizerlu_zerren_description));
	}

	@Override
	public Distribution getDistribution(SparseIntArray sidesToCount) {
		Distribution d = super.getDistribution(sidesToCount);

		return ScaleCumulativeDistribution.scale(d,
				new ScaleCumulativeDistribution.Scale() {
					public BigFraction scale(BigFraction pS, int x) {
						// P(S) = 1 - P(F)
						// P(new) = 1 - ( P(F) + P(S) * (2/6) * P(F)
						final BigFraction pF = BigFraction.ONE.subtract(pS);
						return BigFraction.ONE.subtract(pF.add(pS.multiply(pF)
								.multiply(BigFraction.ONE_THIRD)));
					}
				});
	}
}
