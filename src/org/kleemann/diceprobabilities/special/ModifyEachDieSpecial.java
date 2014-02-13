package org.kleemann.diceprobabilities.special;

import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.ConstantDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.SumDistribution;

import android.content.res.Resources;
import android.util.SparseIntArray;

public class ModifyEachDieSpecial extends AbstractSpecial {

	private int modifier;

	public ModifyEachDieSpecial(Resources r, int modifier) {
		super(
				String.format(
						r.getString(R.string.special_modify_each_die_title),
						modifier),
				String.format(
						r.getString(R.string.special_modify_each_die_description),
						modifier));
		this.modifier = modifier;
	}

	@Override
	public Distribution getDistribution(SparseIntArray sidesToCount) {
		// count the total number of dice
		int numDice = 0;
		for (int i = sidesToCount.size() - 1; i >= 0; --i) {
			final int sides = sidesToCount.keyAt(i);
			final int count = sidesToCount.valueAt(i);
			if (count != 0) {
				// constant does not count as a die for this special
				if (sides != 1) {
					numDice += count;
				}
			}
		}

		// adjust the whole distribution by this adjustment times the number of
		// dice
		final Distribution k = new ConstantDistribution(numDice * modifier);
		return SumDistribution.add(super.getDistribution(sidesToCount), k);
	}
}
