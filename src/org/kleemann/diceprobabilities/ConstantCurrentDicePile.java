package org.kleemann.diceprobabilities;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * <p>
 * A special version of the CurrentDicePile that is used to represent constants.
 * In most cases this can be treated like a one sided die but there are a couple
 * differences:
 * 
 * <p>
 * 1. the display is just a number; no "dN" suffix
 * 
 * <p>
 * 2. The button is only made invisible if the control is cleared.  This allows
 * the user to enter negative nubmers.
 */
public class ConstantCurrentDicePile extends CurrentDicePile {

	public ConstantCurrentDicePile(Button button, OnClickListener changed) {
		super(1, button, changed);
	}

	@Override
	protected void updateButton() {
		updateButton(false);
	}

	private void updateButton(boolean clear) {
		button.setText(count < 0 ? Integer.toString(count) : "+" + count);
		button.setVisibility(clear ? View.GONE : View.VISIBLE);
	}

	@Override
	public void setCount(int count) {
		// constant is allowed to be negative
		this.count = count;
		updateButton();
		changed.onClick(button);
	}

	public void clear() {
		this.count = 0;
		updateButton(true);
		changed.onClick(button);
	}
}
