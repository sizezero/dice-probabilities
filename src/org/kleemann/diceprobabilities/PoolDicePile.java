package org.kleemann.diceprobabilities;

import android.view.View;
import android.widget.Button;

public class PoolDicePile implements View.OnClickListener {

	private int sides;
	private Button button;
	private CurrentDicePile current;
	
	public PoolDicePile(int sides, Button button) {
		this.sides = sides;
		this.button = button;
		button.setText(render());
	}
	
	private String render() {
		return sides==1 ? "+1" : "+d"+sides;
	}

	public void setIncrementer(CurrentDicePile current) {
		this.current = current;
		button.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		current.increment();
	}
	
}
