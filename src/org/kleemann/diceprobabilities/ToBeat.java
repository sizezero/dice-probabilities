package org.kleemann.diceprobabilities;

import android.view.View;
import android.widget.Button;

public class ToBeat extends CurrentDicePile {

	public ToBeat(Button button, View.OnClickListener listener) {
		super(1, button, listener);
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
