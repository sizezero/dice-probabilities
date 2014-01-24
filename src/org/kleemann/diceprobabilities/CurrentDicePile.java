package org.kleemann.diceprobabilities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CurrentDicePile implements View.OnClickListener {

	private int sides;
	private int count;
	protected Button button;
	private View.OnClickListener listener;
	
	public CurrentDicePile(int sides, Context context, ViewGroup container, View.OnClickListener listener) {
		this.sides = sides;
		this.count = 0;
		button = new Button(context);
		container.addView(button);
		button.setOnClickListener(this);
		this.listener = listener;
		updateButton();
	}
	
	protected void updateButton() {
		button.setText(render());
		button.setVisibility(count==0 ? View.GONE : View.VISIBLE);
	}
	
	protected String render() {
		return sides==1 ? "+"+count : count+"d"+sides; 
	}

	public int getCount() { return count; }
	
	public void onClick(View v) { decrement(); }
	
	public void setCount(int count) {
		assert(count >= 0);
		this.count = count;
		updateButton();
		listener.onClick(button);		
	}
	
	public void increment() { setCount(count+1); }

	public void decrement() {
		if (count != 0) {
			setCount(count-1);
		}
	}
}
