package org.kleemann.diceprobabilities.special;

import org.apache.commons.math3.fraction.BigFraction;
import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.ScaleCumulativeDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;

import android.content.res.Resources;
import android.util.SparseIntArray;

/**
 * <p>
 * If you would defeat a bane with the Undead trait here, roll 1d6. On a 1, the
 * monster is undefeated.
 */
public class DesecratedVaultSpecial extends AbstractSpecial {

	private final static BigFraction FIVE_SIXTHS = new BigFraction(5, 6);

	public DesecratedVaultSpecial(Resources r) {
		super(r.getString(R.string.special_desecrated_vault_title), r
				.getString(R.string.special_desecrated_vault_description));
	}

	@Override
	public Distribution getDistribution(SparseIntArray sidesToCount) {
		Distribution d = super.getDistribution(sidesToCount);

		return ScaleCumulativeDistribution.scale(d,
				new ScaleCumulativeDistribution.Scale() {
					public BigFraction scale(BigFraction pS, int x) {
						// P(S) = 1 - P(F)
						// P(new) = (1/6) * 0 + (5/6) * P(S)
						// P(new) = (5/6) * P(S)
						return FIVE_SIXTHS.multiply(pS);
					}
				});
	}
}
