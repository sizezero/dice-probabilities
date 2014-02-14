package org.kleemann.diceprobabilities.special;

import org.kleemann.diceprobabilities.distribution.Distribution;

import android.util.SparseIntArray;

/**
 * <p>
 * This class represents an item that can exist in the "special" spinner control
 * of a diceset. Both the display and behavior of the special is contained in
 * this object.
 */
public interface Special {

	/**
	 * <p>
	 * The short string that is displayed in the collapsed spinner as well as in
	 * the left column of the open spinner.
	 */
	public String getTitle();

	/**
	 * <p>
	 * The lengthy description that is displayed in the right hand column of the
	 * open spinner.
	 */
	public String getDescription();

	/**
	 * <p>
	 * Given a set of dice and this special value, calculates and returns the
	 * distribution.
	 * 
	 * <p>
	 * This function is thread safe and can be called from a non Android-UI
	 * thread.
	 */
	public Distribution getDistribution(SparseIntArray sidesToCount);
	
	/**
	 * <p>
	 * A textual description of the die roll including dice, target result
	 * and any special description.
	 * 
	 * <p>
	 * This function is thread safe and can be called from a non Android-UI
	 * thread.
	 */
	public String getFormula(SparseIntArray sidesToCount, int target, String answerProbability);

}
