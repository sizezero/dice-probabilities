package org.kleemann.diceprobabilities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CurrentDicePile implements View.OnClickListener {

	private int sides;
	private int count;
	private Button button;
	private View.OnClickListener listener;
	
	public CurrentDicePile(int sides, Context context, ViewGroup container, View.OnClickListener listener) {
		this.sides = sides;
		button = new Button(context);
		container.addView(button);
		button.setText(render());
		button.setOnClickListener(this);
		this.listener = listener;
	}
	
	protected String render() {
		return sides==1 ? "+"+count : count+"d"+sides; 
	}

	public int getCount() { return count; }
	
	public void increment() {
		++count;
		button.setText(render());
		listener.onClick(button);
	}
	
	public void onClick(View v) {
		if (count != 0) {
			--count;
			button.setText(render());
		}
		listener.onClick(button);
	}
	
	
}
