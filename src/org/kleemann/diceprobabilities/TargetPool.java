package org.kleemann.diceprobabilities;

import android.view.View;
import android.widget.Button;

public class TargetPool implements View.OnClickListener {

	private int count;
	private Target target;

	public TargetPool(int count, Button button, Target target) {
		this.count = count;
		this.target = target;
		button.setText(render());
		button.setOnClickListener(this);
	}
	
	private String render() {
		if (count < 0) {
			return Integer.toString(count);
		} else {
			return "+" + count;
		}
	}

	public void onClick(View v) {
		target.add(count);
	}
	
}
