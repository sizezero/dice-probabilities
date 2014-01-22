package org.kleemann.diceprobabilities;

import android.content.Context;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;

public class PoolDicePile implements View.OnClickListener {

	private int sides;
	private Button button;
	private CurrentDicePile current;
	
	public PoolDicePile(int sides, Context context, ViewGroup container) {
		this.sides = sides;
		button = new Button(context);
		container.addView(button);
		button.setText(render());
	}
	
	private String render() { return "+d"+sides; }

	public void setIncrementer(CurrentDicePile current) {
		this.current = current;
		button.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		current.increment();
	}
	
}
