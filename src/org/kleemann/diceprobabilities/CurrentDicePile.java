package org.kleemann.diceprobabilities;

import android.view.View;
import android.widget.Button;

/*
 * <p>Provides the behavior for a button that represents the current 
 * number of dice of a particular type (number of sides) that is 
 * to be rolled.  If number of sides==1 then the die is considerred a 
 * constant and rendered differently.
 * 
 * <p>When the Pile's count has changed, it calls the passed listener.
 */
public class CurrentDicePile implements View.OnClickListener {

	private int sides;
	protected int count;
	final protected Button button;
	final protected View.OnClickListener changed;

	protected CurrentDicePile(int sides, Button button,
			View.OnClickListener changed) {
		this.sides = sides;
		this.count = 0;
		this.button = button;
		button.setOnClickListener(this);
		this.changed = changed;
		updateButton();
	}

	public static CurrentDicePile create(int sides, Button button,
			View.OnClickListener changed) {
		if (sides == 1) {
			return new ConstantCurrentDicePile(button, changed);
		} else {
			return new CurrentDicePile(sides, button, changed);
		}
	}
	
	protected void updateButton() {
		button.setText(count + "d" + sides);
		button.setVisibility(count == 0 ? View.GONE
				: View.VISIBLE);
	}

	public int getSides() {
		return sides;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		if (count >= 0) {
			this.count = count;
			updateButton();
			changed.onClick(button);
		}
	}

	public void increment() {
		setCount(count + 1);
	}

	public void decrement() {
		setCount(count - 1);
	}

	public void clear() {
		setCount(0);
	}

	@Override
	public void onClick(View v) {
		decrement();
	}
}
