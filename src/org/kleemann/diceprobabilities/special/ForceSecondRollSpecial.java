package org.kleemann.diceprobabilities.special;

import java.util.ArrayList;

import org.kleemann.diceprobabilities.distribution.Distribution;

import android.util.SparseIntArray;

/**
 * <p>
 * If the first roll succeeds, you must successfully roll again in order to
 * succeed.
 */
class ForceSecondRollSpecial extends AbstractSpecial {

	public ForceSecondRollSpecial() {
		super(
				"Forced Roll",
				"If the first roll succeds, you must roll another successful check to make the roll.  Monster: Hermit Crab");
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
