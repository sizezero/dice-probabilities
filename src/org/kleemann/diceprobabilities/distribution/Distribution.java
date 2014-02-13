package org.kleemann.diceprobabilities.distribution;

import org.apache.commons.math3.fraction.BigFraction;

/**
 * <p>
 * Represents a discrete distribution that exists over the integer set 0 to
 * size()-1 A distribution consists of discrete events (x values) that have a
 * probabilty (returned by getProbability(y)) that each have an independent
 * probability. Independence means:
 * 
 * <p>
 * <ol>
 * 
 * <li>only one event can happen.
 * 
 * <li>one event is guaranteed to happen.
 * 
 * </ol>
 * 
 * <p>
 * A side effect of this is that all probabilities in a distribution must add up
 * to 100% (one)
 * 
 * <p>
 * In addition to the probability of event x it also can be useful to look at the
 * Cumulative probability which is the probability that an event greater than or
 * equal to x occurs. This is returned by getCumulativeProbability(x)
 * 
 * <p>
 * All our implementations of Distribution are immutable and thus thread-safe
 */
public interface Distribution {

	/**
	 * Although the distribution returns legal values across all integers, it is
	 * guaranteed that there are no non-zero values in the range of lowerBound()
	 * to upperBound()-1. This can help with enumeration.
	 */
	int lowerBound();

	int upperBound();

	/**
	 * <p>
	 * Returns the probability of the event at the point x. There is no range
	 * for x so "out of bounds" values return a probability of zero.
	 * 
	 * <p>
	 * Note: x values in this application generally represent sums of dice
	 * rolls.
	 * 
	 * <p>
	 * For all implementations of Distribution this method runs in constant
	 * time; O(1)
	 */
	BigFraction getProbability(int x);

	/**
	 * <p>
	 * Returns the sum of all probabilities in this distribution from point x
	 * and greater.
	 * 
	 * <p>
	 * This usually indicates succeeding at a roll where the target is x.
	 * 
	 * <p>
	 * Some implementations run in constant time some in linear time. If many
	 * calls to this function are made, call cacheCumulative()
	 */
	BigFraction getCumulativeProbability(int x);

	/**
	 * <p>
	 * If the function getCumulativeProbability(x) of this object has worse
	 * performance than O(1) then a Distribution will be returned that has
	 * performance of O(1). If the current object already has O(1) performance
	 * then it will return itself.
	 * 
	 * <p>
	 * If you are going to call getCumulativeProbability(x) many times then you
	 * should call this function first.
	 */
	Distribution cacheCumulative();

	/**
	 * <p>
	 * A handy way to test if the distribution is zero. e.g.
	 * ConstantDistribution.ZERO or new ConstantDistribution(0)
	 */
	boolean isZero();
}
