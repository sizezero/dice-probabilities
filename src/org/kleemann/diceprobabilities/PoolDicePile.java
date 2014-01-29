package org.kleemann.diceprobabilities;

import android.view.View;
import android.widget.Button;

public class PoolDicePile implements View.OnClickListener {

	private int sides;
	private CurrentDicePile current;
	
	public PoolDicePile(int sides, Button button, CurrentDicePile current) {
		this.sides = sides;
		this.current = current;
		button.setText(render());
		button.setOnClickListener(this);
	}
	
	private String render() {
		return sides==1 ? "+1" : "+d"+sides;
	}

	public void onClick(View v) {
		current.increment();
	}
	
}
