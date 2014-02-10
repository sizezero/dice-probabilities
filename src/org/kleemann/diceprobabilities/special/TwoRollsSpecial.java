package org.kleemann.diceprobabilities.special;

import java.util.ArrayList;

import org.kleemann.diceprobabilities.distribution.Distribution;

import android.util.SparseIntArray;

/**
 * <p>If the first roll fails you get a second chance to roll in order to succeed.
 */
class TwoRollsSpecial extends AbstractSpecial {

	public TwoRollsSpecial() {
		super(
				"Bonus Rolls",
				"If the first roll fails, you have a second chance to roll again. Weapon: Glaive, Icy LongSpear");
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
		// TODO: take the extra roll into account
		return d;
	}
}
