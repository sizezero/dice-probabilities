package org.kleemann.diceprobabilities.special;

import org.kleemann.diceprobabilities.distribution.Distribution;

import android.util.SparseIntArray;

class CaizarluZerrenSpecial extends AbstractSpecial {
	public CaizarluZerrenSpecial() {
		super(
				"C. Zerren",
				"When you attempt to defeat Caizarlu Zerren, after you make the roll, roll 1d6. On a 1 or 2, start teh check over. Cards Playued on the previous check do not affect the new check.");
	}

	@Override
	public Distribution getDistribution(SparseIntArray sidesToCount) {
		Distribution d = super.getDistribution(sidesToCount);
		// TODO: reroll a third of the time
		return d;
	}
}

