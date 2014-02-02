package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>This is a distribution that is the sum of other distributions.
 * 
 * <p>O(m*n) where m and n are the sizes of the two distributions.
 */
public class MultinomialDistribution implements Distribution {

	private BigFraction[] vals;
	
	private MultinomialDistribution(Distribution d1, Distribution d2) {
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

	private MultinomialDistribution(DieDistribution d, int mult) {

		final BigFraction unit = d.getProbability(1).pow(mult);
		
		long[] tgt = new long[d.getSides()+1];
		for (int i=1 ; i<=d.getSides(); ++i) {
			tgt[i] = 1;
		}

		for (int m=1 ; m<mult ; ++m) { // loop executes mult -1 times
			// TODO: may be able to move allocations outside of loop
			long[] old = tgt.clone();
			tgt = new long[old.length+(d.getSides()+1) - 1];
			for (int o=0 ; o<old.length ; ++o) {
				for (int s=1 ; s<=d.getSides() ; ++s) {
					final int sum = o + s;
					tgt[sum] = tgt[sum] + old[o];
				}
			}
		}
		
		// convert tgt to BigFraction
		vals = new BigFraction[tgt.length];
		for (int i=0 ; i<vals.length ; ++i) {
			vals[i] = unit.multiply(tgt[i]);
		}
	}
	
	@Override
	public int size() {
		return vals.length;
	}

	@Override
	public BigFraction getProbability(int x) {
		return (x>=0 && x<vals.length) ? vals[x] : BigFraction.ZERO;
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

	public static Distribution add(Distribution d1, Distribution d2) {
		// the only distribution with a size of one is ZERO 
		if (d1.size()==1) {
			return d2;
		} else if (d2.size()==1) {
			return d1;
		} else {
			return new MultinomialDistribution(d1, d2);
		}
	}
	
	/**
	 * <p>Add the given DieDistribution to itself n times.  Much more efficient
	 * than repeated calls to Multinomial(Distribution,Distribution) 
	 */
	public static Distribution multiply(DieDistribution d, int mult) {
		if (mult==0) {
			return ConstantDistribution.ZERO;
		} else if (mult==1) {
			return d;
		} else {
			return new MultinomialDistribution(d, mult);
		}
	}
	
	/**
	 * <p>Adds the distribution by itself n times.  This acts like a multiply.
	 * 
	 * <p>The method batches the adds so that only log2(n) adds are made
	 */
	public static Distribution multiplySlow(Distribution d, int n) {
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
