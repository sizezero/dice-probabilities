package org.kleemann.diceprobabilities.special;

import org.kleemann.diceprobabilities.R;

import android.content.res.Resources;

/**
 * <p>
 * Somewhat of an oxymoron. A Special where nothing out of the ordinary happens.
 * This is the default.
 */
class NormalSpecial extends AbstractSpecial {
	public NormalSpecial(Resources r) {
		super(r.getString(R.string.special_normal_title), r
				.getString(R.string.special_normal_description));
	}

	/**
	 * If there are no specials then don't bother appending "normal" to the
	 * formula
	 */
	@Override
	protected void appendTitle(StringBuilder sb, String title) {
	}

}
