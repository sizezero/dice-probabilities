package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

import junit.framework.TestCase;

/**
 * <p>
 * https://github.com/sizezero/dice-probabilities/issues/11
 */
public class Iss11 extends TestCase {

	/**
	 * <p>
	 * The original bug
	 */
	public void testSimple() {
		Distribution c4 = new CritDistribution(4);
		assertEquals(BigFraction.ONE, c4.getCumulativeProbability(1));
	}

	/**
	 * <p>
	 * Extra tests to insure that crits have cumulative probability of one.
	 */
	public void testCumulative() {
		for (int s = 1; s < 8; ++s) {
			SlowTest.distSumsToOne(new CritDistribution(s));
		}
	}

}
