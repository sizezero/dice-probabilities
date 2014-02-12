package org.kleemann.diceprobabilities.distribution;

import junit.framework.TestCase;

import org.apache.commons.math3.fraction.BigFraction;

public class DistributionTest extends TestCase {

	public void testDSix() {
		Distribution d6 = new DieDistribution(6);
		assertEquals(BigFraction.ONE, d6.getCumulativeProbability(0));
		assertEquals(BigFraction.ONE, d6.getCumulativeProbability(1));
		assertEquals(new BigFraction(5,6), d6.getCumulativeProbability(2));
		assertEquals(new BigFraction(4,6), d6.getCumulativeProbability(3));
		assertEquals(new BigFraction(3,6), d6.getCumulativeProbability(4));
		assertEquals(new BigFraction(2,6), d6.getCumulativeProbability(5));
		assertEquals(new BigFraction(1,6), d6.getCumulativeProbability(6));
		assertEquals(BigFraction.ZERO, d6.getCumulativeProbability(7));
		assertEquals(BigFraction.ZERO, d6.getCumulativeProbability(8));
	}
	
	public void testTwoDSix() {
		Distribution d6 = new DieDistribution(6);
		Distribution twoDSix = SumDistribution.add(d6,d6);
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
		
		assertEquals(BigFraction.ONE, twoDSix.getCumulativeProbability(2));
	}
	
	public void testIdentity() {
		// 100% chance of rolling zero
		Distribution identity = ConstantDistribution.ZERO;
		
		Distribution d6 = new DieDistribution(6);
		Distribution sum = SumDistribution.add(identity,d6);
		
		for (int x=0 ; x<10 ; ++x) {
			assertEquals(d6.getProbability(x), sum.getProbability(x));
		}
	}
	
	public void testConstant() {
		Distribution c2 = new ConstantDistribution(2);
		Distribution d6 = new DieDistribution(6);
		Distribution d = SumDistribution.add(c2, d6);

		assertEquals(BigFraction.ZERO, d.getProbability(0));
		assertEquals(BigFraction.ZERO, d.getProbability(1));
		assertEquals(BigFraction.ZERO, d.getProbability(2));
		assertEquals(new BigFraction(1,6), d.getProbability(3));
		assertEquals(new BigFraction(1,6), d.getProbability(4));
		assertEquals(new BigFraction(1,6), d.getProbability(5));
		assertEquals(new BigFraction(1,6), d.getProbability(6));
		assertEquals(new BigFraction(1,6), d.getProbability(7));
		assertEquals(new BigFraction(1,6), d.getProbability(8));
		assertEquals(BigFraction.ZERO, d.getProbability(9));
		assertEquals(BigFraction.ZERO, d.getProbability(10));
	}
	
	public void testCumulativeConstant() {
		Distribution c2 = new ConstantDistribution(2);
		Distribution d6 = new DieDistribution(6);
		Distribution d = SumDistribution.add(c2, d6);
		
		assertEquals(BigFraction.ONE, d.getCumulativeProbability(0));
		assertEquals(BigFraction.ONE, d.getCumulativeProbability(1));
		assertEquals(BigFraction.ONE, d.getCumulativeProbability(2));
		assertEquals(BigFraction.ONE, d.getCumulativeProbability(3));
		assertEquals(new BigFraction(5,6), d.getCumulativeProbability(4));
		assertEquals(new BigFraction(4,6), d.getCumulativeProbability(5));
		assertEquals(new BigFraction(3,6), d.getCumulativeProbability(6));
		assertEquals(new BigFraction(2,6), d.getCumulativeProbability(7));
		assertEquals(new BigFraction(1,6), d.getCumulativeProbability(8));
		assertEquals(BigFraction.ZERO, d.getCumulativeProbability(9));
		assertEquals(BigFraction.ZERO, d.getCumulativeProbability(10));
	}
	
	public void testNegativeConstant() {
		Distribution cn4 = new ConstantDistribution(-4);
		Distribution d6 = new DieDistribution(6);
		Distribution d = SumDistribution.add(cn4, d6);

		assertEquals(BigFraction.ZERO, d.getProbability(-6));
		assertEquals(BigFraction.ZERO, d.getProbability(-5));
		assertEquals(BigFraction.ZERO, d.getProbability(-4));
		assertEquals(new BigFraction(1,6), d.getProbability(-3));
		assertEquals(new BigFraction(1,6), d.getProbability(-2));
		assertEquals(new BigFraction(1,6), d.getProbability(-1));
		assertEquals(new BigFraction(1,6), d.getProbability(0));
		assertEquals(new BigFraction(1,6), d.getProbability(1));
		assertEquals(new BigFraction(1,6), d.getProbability(2));
		assertEquals(BigFraction.ZERO, d.getProbability(3));
		assertEquals(BigFraction.ZERO, d.getProbability(4));
	}
}
