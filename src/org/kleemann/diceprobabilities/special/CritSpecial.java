package org.kleemann.diceprobabilities.special;

import java.util.ArrayList;

import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.CritDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.SumDistribution;

import android.content.res.Resources;

/**
 * <p>
 * Turns a particular die type into a critical die. e.g. the highest value
 * receives some extra bonus. See CritDistribution.
 */
class CritSpecial extends AbstractSpecial {

	private final int critSides;
	private final int bonus;

	public CritSpecial(Resources r, int sides, int bonus, String weapon) {
		super(String.format(r.getString(R.string.special_crit_title), sides,
				bonus), String.format(
				r.getString(R.string.special_crit_description), bonus, weapon));
		this.critSides = sides;
		this.bonus = bonus;
	}

	@Override
	protected Distribution accumulateDiceStack(int sides, int count,
			Distribution accumulator) {
		if (sides == critSides) {
			final Distribution allDiceOfOneType = SumDistribution.multiply(
					new CritDistribution(sides, bonus), count);
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
		if (sides == critSides) {
			dice.add(count + "D" + sides);
		} else {
			super.addFormulaDie(sides, count, dice);
		}
	}

}
