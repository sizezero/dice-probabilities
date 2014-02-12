package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

import junit.framework.TestCase;

/**
 * <p>
 * Throwing all the big, exhaustive, slow tests into their own class.
 */
public class SlowTest extends TestCase {

	public void testManyMultiply() {
		int[] sides = { 4, 6, 8, 12 };
		for (int s : sides) {
			for (int mult = 0; mult <= 10; ++mult) {
				multiply(s, mult);
			}
		}
	}

	public void testLargeMultiply() {
		for (int mult = 8; mult <= 25; ++mult) {
			multiply(12, mult);
		}
	}

	private void multiply(int s, int n) {
		DieDistribution die = new DieDistribution(s);

		// simple way
		Distribution sum1 = ConstantDistribution.ZERO;
		for (int i = 0; i < n; ++i) {
			sum1 = SumDistribution.add(sum1, die);
		}

		// efficient way
		Distribution d = die;
		Distribution sum2 = SumDistribution.multiply(d, n);

		assertEquals(sum1.lowerBound(), sum2.lowerBound());
		assertEquals(sum1.upperBound(), sum2.upperBound());
		for (int i = sum1.lowerBound() - 2; i < sum1.lowerBound() + 2; ++i) {
			assertEquals(sum1.getProbability(i), sum2.getProbability(i));
		}

		// most efficient way
		Distribution sum3 = SumDistribution.multiply(die, n);
		assertEquals(sum1.lowerBound(), sum3.lowerBound());
		assertEquals(sum1.upperBound(), sum3.upperBound());
		for (int i = sum1.lowerBound() - 2; i < sum1.upperBound() + 2; ++i) {
			assertEquals(sum1.getProbability(i), sum3.getProbability(i));
		}
	}

	public void testCumulative() {
		cumulative(ConstantDistribution.ZERO);
		cumulative(new ConstantDistribution(6));
		cumulative(new DieDistribution(6));
		cumulative(SumDistribution.add(new ConstantDistribution(6),
				new DieDistribution(6)));
		cumulative(SumDistribution.add(new DieDistribution(8),
				new DieDistribution(6)));
	}

	private void cumulative(Distribution d) {
		distSumsToOne(d);
		cachedCumulativeEquals(d);
	}

	/**
	 * <p>
	 * Called from other tests.
	 */
	public static void distSumsToOne(Distribution d) {
		BigFraction sum = BigFraction.ZERO;
		for (int i = d.lowerBound(); i < d.upperBound(); ++i) {
			sum = sum.add(d.getProbability(i));
		}
		assertEquals(BigFraction.ONE, sum);
		assertEquals(BigFraction.ONE,
				d.getCumulativeProbability(d.lowerBound()));
		assertEquals(BigFraction.ZERO,
				d.getCumulativeProbability(d.upperBound()));
	}

	private void cachedCumulativeEquals(Distribution d) {
		Distribution c = new CachedCumulativeDistribution(d);
		assertEquals(d.lowerBound(), c.lowerBound());
		assertEquals(d.upperBound(), c.upperBound());
		for (int i = d.lowerBound(); i < d.upperBound(); ++i) {
			assertEquals(d.getCumulativeProbability(i),
					c.getCumulativeProbability(i));
		}
	}

}
