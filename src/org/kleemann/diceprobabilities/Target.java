package org.kleemann.diceprobabilities;

import android.view.View;
import android.widget.Button;

public class Target extends CurrentDicePile {

	private static final String GREATER_THAN_OR_EQUAL_TO = "\u2265"; 
	
	private final int defaultTarget;
	
	public Target(Button button, View.OnClickListener listener) {
		super(1, button, listener);
		this.defaultTarget = button.getResources().getInteger(R.integer.default_target);
		// slightly inefficient since constructor sets count to zero
		count = defaultTarget;
		updateButton();
	}

	@Override
	protected String render() {
		return GREATER_THAN_OR_EQUAL_TO + getCount(); 
	}

	/**
	 * Always show the ToBeat value even if it's zero
	 */
	@Override
	protected void updateButton() {
		button.setText(render());
	}
	
	@Override
	public void clear() {
		setCount(defaultTarget);
	}

}
