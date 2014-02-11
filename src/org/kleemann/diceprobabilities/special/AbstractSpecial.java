package org.kleemann.diceprobabilities.special;

import java.util.ArrayList;

import org.kleemann.diceprobabilities.distribution.ConstantDistribution;
import org.kleemann.diceprobabilities.distribution.DieDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.SumDistribution;

import android.util.SparseIntArray;

/**
 * <p>
 * A helper class that provides many of the common methods required to implement
 * a Special.
 */
abstract class AbstractSpecial implements Special {

	private final String title;
	private final String description;

	public AbstractSpecial(String title, String description) {
		this.title = title;
		this.description = description;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return title;
	}

	@Override
	public ArrayList<String> getFormulaDice(SparseIntArray sidesToCount) {
		ArrayList<String> dice = new ArrayList<String>();
		for (int i = sidesToCount.size() - 1; i >= 0; --i) {
			final int sides = sidesToCount.keyAt(i);
			final int count = sidesToCount.valueAt(i);
			if (count != 0) {
				addFormulaDie(sides, count, dice);
			}
		}
		return dice;
	}

	/**
	 * Override this function to change the way formula dice are created.
	 */
	protected void addFormulaDie(int sides, int count, ArrayList<String> dice) {
		if (sides == 1) {
			// d1 is really just adding a constant
			if (count < 0) {
				dice.add("- " + Integer.toString(-count));
			} else {
				dice.add(Integer.toString(count));
			}
		} else {
			dice.add(count + "d" + sides);
		}
	}

	@Override
	public Distribution getDistribution(SparseIntArray sidesToCount) {
		Distribution accumulator = ConstantDistribution.ZERO;
		for (int i = sidesToCount.size() - 1; i >= 0; --i) {
			final int sides = sidesToCount.keyAt(i);
			final int count = sidesToCount.valueAt(i);
			if (count != 0) {
				accumulator = accumulateDiceStack(sides, count, accumulator);
			}
		}
		return accumulator;
	}

	/**
	 * <p>
	 * Override this method to change the ways stacks of similar dice are added
	 * to the distribution.
	 */
	protected Distribution accumulateDiceStack(int sides, int count,
			Distribution accumulator) {
		final Distribution allDiceOfOneType;
		if (sides == 1) {
			allDiceOfOneType = new ConstantDistribution(count);
		} else {
			final DieDistribution singleDie = new DieDistribution(sides);
			allDiceOfOneType = SumDistribution.multiply(singleDie, count);
		}
		return SumDistribution.add(accumulator, allDiceOfOneType);
	}
}
