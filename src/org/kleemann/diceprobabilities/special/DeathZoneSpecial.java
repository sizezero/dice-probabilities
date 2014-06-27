package org.kleemann.diceprobabilities.special;

import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.ConstantDistribution;
import org.kleemann.diceprobabilities.distribution.DeathZoneDieDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.SumDistribution;

import android.content.res.Resources;

/**
 * Crit for the Death Zone location. Ones and twos count as zeros.
 */
public class DeathZoneSpecial extends AbstractSpecial {

	public DeathZoneSpecial(Resources r) {
		super(r.getString(R.string.special_death_zone_title), r
				.getString(R.string.special_death_zone_description));
	}

	@Override
	protected Distribution accumulateDiceStack(int sides, int count,
			Distribution accumulator) {
		final Distribution allDiceOfOneType;
		if (sides == 1) {
			allDiceOfOneType = new ConstantDistribution(count);
		} else {
			final Distribution singleDie = new DeathZoneDieDistribution(sides);
			allDiceOfOneType = SumDistribution.multiply(singleDie, count);
		}
		return SumDistribution.add(accumulator, allDiceOfOneType);
	}

}
