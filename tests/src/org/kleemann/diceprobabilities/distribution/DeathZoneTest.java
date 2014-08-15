package org.kleemann.diceprobabilities.distribution;

import junit.framework.TestCase;

import org.apache.commons.math3.fraction.BigFraction;

public class DeathZoneTest extends TestCase {

	public void testFour() {
		Distribution d4 = new DeathZoneDieDistribution(4);
		assertEquals(BigFraction.ZERO, d4.getProbability(-2));
		assertEquals(BigFraction.ZERO, d4.getProbability(-1));
		assertEquals(new BigFraction(2, 4), d4.getProbability(0));
		assertEquals(BigFraction.ZERO, d4.getProbability(1));
		assertEquals(BigFraction.ZERO, d4.getProbability(2));
		assertEquals(new BigFraction(1, 4), d4.getProbability(3));
		assertEquals(new BigFraction(1, 4), d4.getProbability(4));
		assertEquals(BigFraction.ZERO, d4.getProbability(5));
		assertEquals(BigFraction.ZERO, d4.getProbability(6));
	}

	public void testSix() {
		Distribution d6 = new DeathZoneDieDistribution(6);
		assertEquals(BigFraction.ZERO, d6.getProbability(-2));
		assertEquals(BigFraction.ZERO, d6.getProbability(-1));
		assertEquals(new BigFraction(2, 6), d6.getProbability(0));
		assertEquals(BigFraction.ZERO, d6.getProbability(1));
		assertEquals(BigFraction.ZERO, d6.getProbability(2));
		assertEquals(new BigFraction(1, 6), d6.getProbability(3));
		assertEquals(new BigFraction(1, 6), d6.getProbability(4));
		assertEquals(new BigFraction(1, 6), d6.getProbability(5));
		assertEquals(new BigFraction(1, 6), d6.getProbability(6));
		assertEquals(BigFraction.ZERO, d6.getProbability(7));
		assertEquals(BigFraction.ZERO, d6.getProbability(8));
	}

}
