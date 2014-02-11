package org.kleemann.diceprobabilities.special;

import java.util.ArrayList;

import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.ConstantDistribution;
import org.kleemann.diceprobabilities.distribution.CritDistribution;
import org.kleemann.diceprobabilities.distribution.DieDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.SumDistribution;

import android.content.res.Resources;

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
			// replace the normal die with the crit die
			if (sides == critSides) {
				allDiceOfOneType = SumDistribution.multiply(
						new CritDistribution(sides), count);

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
