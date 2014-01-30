package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>This is a distribution that is the sum of other distributions.
 * 
 * <p>O(m*n) where m and n are the sizes of the two distributions.
 */
public class MultinomialDistribution implements Distribution {

	private BigFraction[] vals;
	
	public MultinomialDistribution(Distribution d1, Distribution d2) {
		// find the new range of the distribution.
		// It should equal the sum of the highest values of the two distributions
		
		// d6 has size of 7; max sum is 12
		// 7+7-1 equals allocation of 13 (we count zero)
		vals = new BigFraction[d1.size()+d2.size()-1];

		for (int i=0 ; i<vals.length ; ++i) {
			vals[i] = BigFraction.ZERO;
		}
		
		for (int i1=0 ; i1<d1.size() ; ++i1) {
			for (int i2=0 ; i2<d2.size(); ++i2) {
				final int sum = i1 + i2;
				final BigFraction prob = d1.getProbability(i1).multiply(d2.getProbability(i2));
				vals[sum] = vals[sum].add(prob);
			}
		}
	}
	
	@Override
	public int size() {
		return vals.length-1;
	}

	@Override
	public BigFraction getProbability(int x) {
		// TODO Auto-generated method stub
		if (x>=0 && x<vals.length) {
			return vals[x];
		} else {
			return BigFraction.ZERO;
		}
	}

	/**
	 * <p>This is currently O(n)
	 */
	@Override
	public BigFraction getCumulativeProbability(int x) {
		BigFraction sum = BigFraction.ZERO;
		for (int i=x ; i<vals.length ; ++i) {
			sum = sum.add(vals[i]);
		}
		return sum;
	}

}
