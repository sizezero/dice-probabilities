package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>
 * This is a distribution that is the sum of other distributions.
 * 
 * <p>
 * If event x on the distribution is the probability of getting that number then
 * each point on the sum distribution is the cumulative product of each way the
 * source distributions can be summed.
 * 
 * <p>
 * 
 * <pre>
 * Take two 6 sided dice as an example: sum.getProbability(5) =
 * d1.getProbability(1) * d2.getProbability(4) + 
 * d1.getProbability(2) * d2.getProbability(3) + 
 * d1.getProbability(3) * d2.getProbability(2) +
 * d1.getProbability(4) * d2.getProbability(1)
 * </pre>
 * 
 * <p>
 * For efficiency there is also an integer multiply function that adds a
 * distribution to itself n times.
 */
public class SumDistribution extends AbstractDistribution {

	final private BigFraction[] vals;
	final private int lower;
	final private int upper;

	/**
	 * <p>
	 * This is the simplest form of summation. It is guaranteed to work for
	 * every input parameter but takes O(m*n) with a high constant factor due to
	 * a multiply and add of a BigFraction at each iteration.
	 */
	private SumDistribution(Distribution d1, Distribution d2) {
		// find the new range of the distribution.
		// It should be bounded by the sum of the lowest two values and the sum
		// of the highest two values
		lower = d1.lowerBound() + d2.lowerBound();
		upper = d1.upperBound() + d2.upperBound() - 1;

		// d6 has size of 7; max sum is 12
		// 7+7-1 equals allocation of 13 (we count zero)
		vals = new BigFraction[upper - lower];

		for (int i = 0; i < vals.length; ++i) {
			vals[i] = BigFraction.ZERO;
		}

		for (int i1 = d1.lowerBound(); i1 < d1.upperBound(); ++i1) {
			for (int i2 = d2.lowerBound(); i2 < d2.upperBound(); ++i2) {
				final int sum = i1 + i2;
				final BigFraction product = d1.getProbability(i1).multiply(
						d2.getProbability(i2));
				final int i = sum - lower;
				vals[i] = vals[i].add(product);
			}
		}
	}

	/**
	 * <p>
	 * Adds the distribution by itself n times. This acts like a multiply.
	 * 
	 * <p>
	 * The method batches the adds so around log2(n) adds are made instead of n
	 * adds.
	 * 
	 * <pre>
	 * e.g. a call of multiplyLog(DiceDistribution(6), 10)
	 * results in arithmetic similar to the following:
	 * 
	 * 2d = d.add(d)
	 * 4d = 2d.add(2d)
	 * 8d = 4d.add(4d)
	 * 10d = 8d.add(2d)
	 * 
	 * </pre>
	 */
	private static Distribution multiplyLog(Distribution d, int n) {
		assert (n > 1);
		// for small values of n there is no need to do anything special
		if (n < 4) {
			Distribution sum = new SumDistribution(d, d);
			for (int i = 2; i < n; ++i) {
				sum = new SumDistribution(sum, d);
			}
			return sum;
		}

		// create the multiples of two that we will use to construct the sum
		final int log = log2(n);
		Distribution sums[] = new Distribution[log + 1];
		sums[0] = d;
		for (int i = 1; i <= log; ++i) {
			sums[i] = new SumDistribution(sums[i - 1], sums[i - 1]);
		}

		// add large multiples that are less than n until the entire sum is
		// reached
		Distribution r = null;
		while (n > 0) {
			// find the largest power of 2 that is less than or equal to n
			final int lg = log2(n);
			r = r == null ? sums[lg] : new SumDistribution(r, sums[lg]);
			n -= 1 << lg;
		}
		return r;
	}

	private static int log2(int n) {
		if (n <= 0)
			throw new IllegalArgumentException();
		return 31 - Integer.numberOfLeadingZeros(n);
	}

	/**
	 * <p>
	 * This is a form of multiply that uses longs to multiply a distribution
	 * that has equal occurrences (dice) for most of the calculations. Only
	 * works for multiplies < 20 for 12 sided dice. Anything beyond that result
	 * in a long underflow and an incorrect result.
	 */
	private SumDistribution(DieDistribution d, int mult) {

		lower = mult;
		upper = d.getSides() * mult + 1;

		final BigFraction unit = d.getProbability(1).pow(mult);

		long[] tgt = new long[d.getSides() + 1];
		for (int i = 1; i <= d.getSides(); ++i) {
			tgt[i] = 1;
		}

		for (int m = 1; m < mult; ++m) { // loop executes mult -1 times
			long[] old = tgt.clone();
			tgt = new long[old.length + d.getSides()];
			for (int o = 0; o < old.length; ++o) {
				for (int s = 1; s <= d.getSides(); ++s) {
					final int sum = o + s;
					tgt[sum] = tgt[sum] + old[o];
				}
			}
		}

		// convert tgt to BigFraction
		vals = new BigFraction[upper - lower];
		for (int i = 0; i < vals.length; ++i) {
			vals[i] = unit.multiply(tgt[i + lower]);
		}
	}

	@Override
	public int lowerBound() {
		return lower;
	}

	@Override
	public int upperBound() {
		return upper;
	}

	@Override
	public BigFraction getProbabilityBounded(int x) {
		return vals[x - lower];
	}

	/**
	 * <p>
	 * Sums two distributions.
	 */
	public static Distribution add(Distribution d1, Distribution d2) {
		if (d1.isZero()) {
			return d2;
		} else if (d2.isZero()) {
			return d1;
		} else {
			return new SumDistribution(d1, d2);
		}
	}

	/**
	 * <p>
	 * Add the given DieDistribution to itself n times. Much more efficient than
	 * repeated calls to add(Distribution,Distribution)
	 */
	public static Distribution multiply(DieDistribution d, int mult) {
		if (mult == 0) {
			return ConstantDistribution.ZERO;
		} else if (mult == 1) {
			return d;
		} else if (d.getSides() <= 12 && mult <= 15) {
			return new SumDistribution(d, mult);
		} else {
			return multiplyLog(d, mult);
		}
	}

	/**
	 * <p>
	 * A more generic form of multiply that can multiply any type of
	 * Distribution not just DieDistribution.
	 */
	public static Distribution multiply(Distribution d, int mult) {
		if (mult == 0) {
			return ConstantDistribution.ZERO;
		} else if (mult == 1) {
			return d;
		} else {
			return multiplyLog(d, mult);
		}
	}
}
