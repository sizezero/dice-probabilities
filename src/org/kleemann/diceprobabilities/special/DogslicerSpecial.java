package org.kleemann.diceprobabilities.special;

import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.ConstantDistribution;
import org.kleemann.diceprobabilities.distribution.DieDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.DogslicerDistribution;
import org.kleemann.diceprobabilities.distribution.SumDistribution;

import android.content.res.Resources;
import android.util.SparseIntArray;

/**
 * <p>For all d6 dice, ones become threes. 
 */
class DogslicerSpecial extends AbstractSpecial {
	
	public DogslicerSpecial(Resources r) {
		super(r.getString(R.string.special_dogslicer_title), r
				.getString(R.string.special_dogslicer_description));
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
					if (sides == 6) {
						allDiceOfOneType = SumDistribution.multiply(
								new DogslicerDistribution(), count);

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
