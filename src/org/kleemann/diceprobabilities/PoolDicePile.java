package org.kleemann.diceprobabilities;

import android.view.View;
import android.widget.Button;

/**
 * Provides the behavior for a button that, when pressed, adds to a paired button
 * that represents the current count of that number of dice. 
 */
public class PoolDicePile implements View.OnClickListener {

	private CurrentDicePile current;
	
	public PoolDicePile(Button button, CurrentDicePile current) {
		this.current = current;
		button.setText(render());
		button.setOnClickListener(this);
	}
	
	private String render() {
		return current.getSides()==1 ? "+1" : "+d"+current.getSides();
	}

	public void onClick(View v) {
		current.increment();
	}
	
}
