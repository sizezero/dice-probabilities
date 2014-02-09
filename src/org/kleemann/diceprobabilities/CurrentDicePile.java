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
	protected Button button;
	private View.OnClickListener changed;
	
	public CurrentDicePile(int sides, Button button, View.OnClickListener changed) {
		this.sides = sides;
		this.count = 0;
		this.button = button;
		button.setOnClickListener(this);
		this.changed = changed;
		updateButton();
	}
	
	protected void updateButton() {
		button.setText(render());
		button.setVisibility(count==0 ? View.GONE : View.VISIBLE);
	}
	
	protected String render() {
		return sides==1 ? "+"+count : count+"d"+sides; 
	}

	public int getSides() { return sides; }
	
	public int getCount() { return count; }
	
	public void setCount(int count) {
		assert(count >= 0);
		this.count = count;
		updateButton();
		changed.onClick(button);		
	}
	
	public void increment() { setCount(count+1); }

	public void decrement() { add(-1); }
	
	public void clear() { setCount(0); }
	
	public void add(int count) {
		int n = getCount() + count;
		setCount(n<0 ? 0 : n);
	}
	
	@Override
	public void onClick(View v) { decrement(); }
}
