package org.kleemann.diceprobabilities.special;

import java.util.ArrayList;

import org.apache.commons.math3.fraction.BigFraction;
import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.ScaleCumulativeDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;

import android.content.res.Resources;
import android.util.SparseIntArray;

/**
 * <p>
 * If the first roll succeeds, you must successfully roll again in order to
 * succeed.
 */
class ForcedRerollSpecial extends AbstractSpecial {

	public ForcedRerollSpecial(Resources r) {
		super(r.getString(R.string.special_forced_reroll_title), r
				.getString(R.string.special_forced_reroll_description));
	}

	@Override
	public ArrayList<String> getFormulaDice(SparseIntArray sidesToCount) {
		ArrayList<String> dice = super.getFormulaDice(sidesToCount);
		dice.add("x2");
		return dice;
	}

	@Override
	public Distribution getDistribution(SparseIntArray sidesToCount) {
		Distribution d = super.getDistribution(sidesToCount);

		return ScaleCumulativeDistribution.scale(d,
				new ScaleCumulativeDistribution.Scale() {
					public BigFraction scale(BigFraction pS, int x) {
						// P(S) = 1 - P(F)
						// P(new) = 1 - ( P(F) + P(S) * P(F) )
						final BigFraction pF = BigFraction.ONE.subtract(pS);
						return BigFraction.ONE.subtract(pF.add(pS.multiply(pF)));
					}
				});
	}
}
