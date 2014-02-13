package org.kleemann.diceprobabilities.special;

import java.util.ArrayList;

import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.ConstantDistribution;
import org.kleemann.diceprobabilities.distribution.DieDistribution;
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

	/**
	 * <p>
	 * Use capital D for crits
	 */
	@Override
	protected void addFormulaDie(int sides, int count, ArrayList<String> dice) {
		if (sides == 1) {
			super.addFormulaDie(sides, count, dice);
		} else {
			dice.add(count + "D" + sides);
		}
	}

	@Override
	protected Distribution accumulateDiceStack(int sides, int count,
			Distribution accumulator) {
		final Distribution allDiceOfOneType;
		if (sides == 1) {
			allDiceOfOneType = new ConstantDistribution(count);
		} else {
			// replace the normal die with a dogslicer die
			if (sides == DogslicerDistribution.SIDES) {
				allDiceOfOneType = SumDistribution.multiply(
						new DogslicerDistribution(), count);

			} else {
				// Note: passing DieDistribution to multiply is more efficient
				// than CritDistribution
				allDiceOfOneType = SumDistribution.multiply(
						new DieDistribution(sides), count);
			}
		}
		return SumDistribution.add(accumulator, allDiceOfOneType);
	}

}
