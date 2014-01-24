package org.kleemann.diceprobabilities;

import android.content.Context;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class ToBeat extends CurrentDicePile {

	public ToBeat(Context context, ViewGroup container,
			OnClickListener listener) {
		super(1, context, container, listener);
	}

	protected String render() {
		final String greaterThanOrEqual = "\u2265";
		return greaterThanOrEqual+getCount(); 
	}

	/**
	 * Always show the ToBeat value even if it's zero
	 */
	protected void updateButton() {
		button.setText(render());
	}

}
