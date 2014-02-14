package org.kleemann.diceprobabilities.special;

import java.util.ArrayList;

import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.DogslicerDistribution;
import org.kleemann.diceprobabilities.distribution.SumDistribution;

import android.content.res.Resources;

/**
 * <p>
 * For all d6 dice, ones become threes.
 */
class DogslicerSpecial extends AbstractSpecial {

	public DogslicerSpecial(Resources r) {
		super(r.getString(R.string.special_dogslicer_title), r
				.getString(R.string.special_dogslicer_description));
	}

	@Override
	protected Distribution accumulateDiceStack(int sides, int count,
			Distribution accumulator) {
		if (sides == DogslicerDistribution.SIDES) {
			final Distribution allDiceOfOneType = SumDistribution.multiply(
					new DogslicerDistribution(), count);
			return SumDistribution.add(accumulator, allDiceOfOneType);
		} else {
			return super.accumulateDiceStack(sides, count, accumulator);
		}
	}

	/**
	 * <p>
	 * Use capital D for crits
	 */
	@Override
	protected void addFormulaDie(int sides, int count, ArrayList<String> dice) {
		if (sides == DogslicerDistribution.SIDES) {
			dice.add(count + "D" + sides);
		} else {
			super.addFormulaDie(sides, count, dice);
		}
	}

}
