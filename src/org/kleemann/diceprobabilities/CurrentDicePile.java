package org.kleemann.diceprobabilities;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CurrentDicePile implements View.OnClickListener {

	private int sides;
	private int count;
	private Button button;
	
	public CurrentDicePile(int sides, Context context, ViewGroup container) {
		this.sides = sides;
		button = new Button(context);
		container.addView(button);
		button.setText(render());
		button.setOnClickListener(this);
	}
	
	private String render() { return count+"d"+sides; }

	public void increment() {
		++count;
		button.setText(render());
	}
	
	public void onClick(View v) {
		if (count != 0) {
			--count;
			button.setText(render());
		}
	}
}
