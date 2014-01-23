package org.kleemann.diceprobabilities.test;

import org.apache.commons.math3.fraction.BigFraction;
import org.kleemann.diceprobabilities.ConstantDistribution;
import org.kleemann.diceprobabilities.DieDistribution;
import org.kleemann.diceprobabilities.Distribution;
import org.kleemann.diceprobabilities.MultinomialDistribution;
import org.kleemann.diceprobabilities.ZeroDistribution;

import junit.framework.TestCase;

public class DistributionTest extends TestCase {

	public void testTwoDSix() {
		Distribution d6 = new DieDistribution(6);
		Distribution twoDSix = new MultinomialDistribution(d6,d6);
		assertEquals(BigFraction.ZERO, twoDSix.getProbability(0));
		assertEquals(BigFraction.ZERO, twoDSix.getProbability(1));
		assertEquals(new BigFraction(1,36), twoDSix.getProbability(2));
		assertEquals(new BigFraction(2,36), twoDSix.getProbability(3));
		assertEquals(new BigFraction(3,36), twoDSix.getProbability(4));
		assertEquals(new BigFraction(4,36), twoDSix.getProbability(5));
		assertEquals(new BigFraction(5,36), twoDSix.getProbability(6));
		assertEquals(new BigFraction(6,36), twoDSix.getProbability(7));
		assertEquals(new BigFraction(5,36), twoDSix.getProbability(8));
		assertEquals(new BigFraction(4,36), twoDSix.getProbability(9));
		assertEquals(new BigFraction(3,36), twoDSix.getProbability(10));
		assertEquals(new BigFraction(2,36), twoDSix.getProbability(11));
		assertEquals(new BigFraction(1,36), twoDSix.getProbability(12));
		assertEquals(BigFraction.ZERO, twoDSix.getProbability(13));
		assertEquals(BigFraction.ZERO, twoDSix.getProbability(14));
		
		assertEquals(BigFraction.ONE, twoDSix.getCumulativeProbability(12));
	}
	
	public void testIdentity() {
		// 100% chance of rolling zero
		Distribution identity = new ZeroDistribution();
		//Distribution identity = new ConstantDistribution(1);
		
		Distribution d6 = new DieDistribution(6);
		Distribution sum = new MultinomialDistribution(identity,d6);
		
		for (int x=0 ; x<10 ; ++x) {
			assertEquals(d6.getProbability(x), sum.getProbability(x));
		}
	}
}
