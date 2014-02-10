package org.kleemann.diceprobabilities.special;

import java.util.ArrayList;

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
	 * A list of dice components that is used to construct the formula that is
	 * displayed on the background graph.
	 * 
	 * <p>
	 * This function is thread safe and can be called from a non Android-UI
	 * thread.
	 */
	public ArrayList<String> getFormulaDice(SparseIntArray sidesToCount);

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
}
