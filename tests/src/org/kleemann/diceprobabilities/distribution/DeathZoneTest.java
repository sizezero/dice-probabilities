package org.kleemann.diceprobabilities.distribution;

import junit.framework.TestCase;

import org.apache.commons.math3.fraction.BigFraction;

public class DeathZoneTest extends TestCase {

	public void testFour() {
		Distribution d4 = new DeathZoneDieDistribution(4);
		assertEquals(BigFraction.ZERO, d4.getProbability(-2));
		assertEquals(BigFraction.ZERO, d4.getProbability(-1));
		assertEquals(new BigFraction(1, 2), d4.getProbability(0));
		assertEquals(BigFraction.ZERO, d4.getProbability(1));
		assertEquals(BigFraction.ZERO, d4.getProbability(2));
		assertEquals(new BigFraction(1, 4), d4.getProbability(3));
		assertEquals(new BigFraction(1, 4), d4.getProbability(4));
		assertEquals(BigFraction.ZERO, d4.getProbability(5));
		assertEquals(BigFraction.ZERO, d4.getProbability(6));
	}

}
