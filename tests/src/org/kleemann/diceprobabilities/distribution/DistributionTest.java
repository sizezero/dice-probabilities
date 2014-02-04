package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;
import org.kleemann.diceprobabilities.distribution.ConstantDistribution;
import org.kleemann.diceprobabilities.distribution.DieDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.MultinomialDistribution;

import junit.framework.TestCase;

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
		Distribution twoDSix = MultinomialDistribution.add(d6,d6);
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
		Distribution sum = MultinomialDistribution.add(identity,d6);
		
		for (int x=0 ; x<10 ; ++x) {
			assertEquals(d6.getProbability(x), sum.getProbability(x));
		}
	}
	
	public void testConstant() {
		Distribution c2 = new ConstantDistribution(2);
		Distribution d6 = new DieDistribution(6);
		Distribution d = MultinomialDistribution.add(c2, d6);

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
		Distribution d = MultinomialDistribution.add(c2, d6);
		
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
	
	public void testMultiply() {
		for (int i=1 ; i<12 ; ++i) {
			multiply(i);
		}
	}
	
	private void multiply(int n) {
		// need at least 4 dice to test
		DieDistribution d6 = new DieDistribution(6);
		
		// simple way
		Distribution sum1 = ConstantDistribution.ZERO;
		for (int i=0 ; i<n ; ++i) {
			sum1 = MultinomialDistribution.add(sum1, d6);
		}
		
		// complex way
		Distribution sum2 = MultinomialDistribution.multiplySlow(d6, n);
		
		assertEquals(sum1.size(), sum2.size());
		for (int i=0 ; i<sum1.size() ; ++i) {
			assertEquals(sum1.getProbability(i), sum2.getProbability(i));
		}
		
		// efficient way
		Distribution sum3 = MultinomialDistribution.multiply(d6, n);
		assertEquals(sum1.size(), sum3.size());
		for (int i=0 ; i<sum1.size() ; ++i) {
			assertEquals(sum1.getProbability(i), sum3.getProbability(i));
		}
	}
	
	public void testCumulative() {
		cumulative(ConstantDistribution.ZERO);
		cumulative(new ConstantDistribution(6));
		cumulative(new DieDistribution(6));
		cumulative(MultinomialDistribution.add(new ConstantDistribution(6), new DieDistribution(6)));
		cumulative(MultinomialDistribution.add(new DieDistribution(8), new DieDistribution(6)));
	}

	private void cumulative(Distribution d) {
		distSumsToOne(d);
		cachedCumulativeEquals(d);
	}
	
	private void distSumsToOne(Distribution d) {
		BigFraction sum = BigFraction.ZERO;
		for (int i=0 ; i<d.size(); ++i) {
			sum = sum.add(d.getProbability(i));
		}
		assertEquals(BigFraction.ONE, sum);
		assertEquals(BigFraction.ONE, d.getCumulativeProbability(0));
		assertEquals(BigFraction.ZERO, d.getCumulativeProbability(d.size()));
	}
	
	private void cachedCumulativeEquals(Distribution d) {
		Distribution c = new CachedCumulativeDistribution(d);
		assertEquals(d.size(), c.size());
		for (int i=0 ; i<d.size(); ++i) {
			assertEquals(d.getCumulativeProbability(i), c.getCumulativeProbability(i));
		}
	}
}
