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
				final BigFraction product = d1.getProbability(i1).multiply(d2.getProbability(i2));
				vals[sum] = vals[sum].add(product);
			}
		}
	}
	
	@Override
	public int size() {
		return vals.length;
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

	/**
	 * <p>Adds the distribution by itself n times.  This acts like a multiply.
	 * 
	 * <p>The method batches the adds so that only log2(n) adds are made
	 */
	public static Distribution multiply(Distribution d, int n) {
		// for small values of n there is no need to do anything special
		if (n==0) {
			return ConstantDistribution.ZERO;
		} else if (n==1) {
			return d;
		} else if (n<4) {
			Distribution sum = new MultinomialDistribution(d, d);
			for (int i=2 ; i<n ; ++i ) {
				sum = new MultinomialDistribution(sum, d);
			}
			return sum;
		}
		
		// create the multiples of two that we will use to construct the sum
		final int log = log2(n);
		Distribution sums[] = new Distribution[log+1];
		sums[0] = d;
		for (int i=1 ; i<=log ; ++i) {
			sums[i] = new MultinomialDistribution(sums[i-1], sums[i-1]);
		}
		
		// reduce n to zero
		Distribution r = ConstantDistribution.ZERO;
		while (n > 0) {
			// find the largest power of 2 that is less than or equal to n
			final int lg = log2(n);
			r = new MultinomialDistribution(r, sums[lg]);
			n -= 1 << lg;
		}
		return r;
	}
	
    private static int log2(int n) {
        if (n <= 0)
            throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros(n);
    }
}
