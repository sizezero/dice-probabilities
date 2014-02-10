package org.kleemann.diceprobabilities.special;

import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.ConstantDistribution;
import org.kleemann.diceprobabilities.distribution.CritDistribution;
import org.kleemann.diceprobabilities.distribution.DieDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.SumDistribution;

import android.content.res.Resources;
import android.util.SparseIntArray;

/**
 * <p>
 * Turns a particular die type into a critical die. e.g. the highest value is
 * actually one higher. See CritDistribution.
 */
class CritSpecial extends AbstractSpecial {

	private int critSides;

	public CritSpecial(Resources r, int sides) {
		super(String.format(r.getString(R.string.special_crit_title), sides), r
				.getString(R.string.special_crit_description));
		this.critSides = sides;
	}

	@Override
	public Distribution getDistribution(SparseIntArray sidesToCount) {
		Distribution d = ConstantDistribution.ZERO;
		for (int i = sidesToCount.size() - 1; i >= 0; --i) {
			final int sides = sidesToCount.keyAt(i);
			final int count = sidesToCount.valueAt(i);
			if (count != 0) {
				final Distribution allDiceOfOneType;
				if (sides == 1) {
					allDiceOfOneType = new ConstantDistribution(count);
				} else {
					// replace the normal die with a crit die
					if (sides == critSides) {
						allDiceOfOneType = SumDistribution.multiply(
								new CritDistribution(sides), count);

					} else {
						allDiceOfOneType = SumDistribution.multiply(
								new DieDistribution(sides), count);
					}
				}
				d = SumDistribution.add(d, allDiceOfOneType);
			}
		}
		return d;
	}
}
