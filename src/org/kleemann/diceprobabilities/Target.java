package org.kleemann.diceprobabilities;

import android.view.View;
import android.widget.Button;

/**
 * <p>
 * A special type of CurrentDicePile that represents the integer target of the
 * roll. Primary difference is the rendering of the value and a different
 * default value.
 */
public class Target implements View.OnClickListener {

	private static final String GREATER_THAN_OR_EQUAL_TO = "\u2265";

	private final int defaultTarget;
	private int count;
	private Button button;
	private View.OnClickListener changed;

	public Target(Button button, View.OnClickListener changed) {
		this.defaultTarget = button.getResources().getInteger(
				R.integer.default_target);
		this.count = defaultTarget;
		this.button = button;
		this.changed = changed;
		button.setOnClickListener(this);
		updateButton();
	}

	private void updateButton() {
		button.setText(GREATER_THAN_OR_EQUAL_TO + count);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		assert (count >= 0);
		this.count = count;
		updateButton();
		changed.onClick(button);
	}

	public void add(int count) {
		int n = this.count + count;
		setCount(n < 0 ? 0 : n);
	}

	public void clear() {
		setCount(defaultTarget);
	}

	/**
	 * Cycle between 0, 10, 20, 30
	 */
	@Override
	public void onClick(View v) {
		int n = (((count + 10) / 10) * 10);
		if (n > 30) {
			n = 0;
		}
		setCount(n);
	}
}
