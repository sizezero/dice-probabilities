package org.kleemann.diceprobabilities;

import org.kleemann.diceprobabilities.graph.GraphView;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private View topButtonGroup;
	
	private DiceSet diceSet1;
	private DiceSet diceSet2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		
		this.topButtonGroup = (View)findViewById(R.id.top_button_group);

		GraphView graph = (GraphView)findViewById(R.id.graph); 
		
		this.diceSet1 = new DiceSet(
				(Button)findViewById(R.id.pool1_d12),
				(Button)findViewById(R.id.pool1_d10),
				(Button)findViewById(R.id.pool1_d8),
				(Button)findViewById(R.id.pool1_d6),
				(Button)findViewById(R.id.pool1_d4),
				(Button)findViewById(R.id.pool1_constant),
				(Button)findViewById(R.id.pool1_target),
				(Button)findViewById(R.id.current1_d12),
				(Button)findViewById(R.id.current1_d10),
				(Button)findViewById(R.id.current1_d8),
				(Button)findViewById(R.id.current1_d6),
				(Button)findViewById(R.id.current1_d4),
				(Button)findViewById(R.id.current1_constant),
				(Button)findViewById(R.id.current1_target),
				(Button)findViewById(R.id.clear1),
				(TextView)findViewById(R.id.answer1_fraction),
				(TextView)findViewById(R.id.answer1_probability),
				graph.getSetter1()
				);

		this.diceSet2 = new DiceSet(
				(Button)findViewById(R.id.pool2_d12),
				(Button)findViewById(R.id.pool2_d10),
				(Button)findViewById(R.id.pool2_d8),
				(Button)findViewById(R.id.pool2_d6),
				(Button)findViewById(R.id.pool2_d4),
				(Button)findViewById(R.id.pool2_constant),
				(Button)findViewById(R.id.pool2_target),
				(Button)findViewById(R.id.current2_d12),
				(Button)findViewById(R.id.current2_d10),
				(Button)findViewById(R.id.current2_d8),
				(Button)findViewById(R.id.current2_d6),
				(Button)findViewById(R.id.current2_d4),
				(Button)findViewById(R.id.current2_constant),
				(Button)findViewById(R.id.current2_target),
				(Button)findViewById(R.id.clear2),
				(TextView)findViewById(R.id.answer2_fraction),
				(TextView)findViewById(R.id.answer2_probability),
				graph.getSetter2()
				);
		
		((Button)findViewById(R.id.copy_down)).setOnClickListener(new Copy(diceSet2, diceSet1));
		((Button)findViewById(R.id.copy_up)).setOnClickListener(new Copy(diceSet1, diceSet2));
		
		graph.setOnClickListener(new GraphPress());
	}

	private static class Copy implements View.OnClickListener {
		
		private DiceSet destination;
		private DiceSet source;

		public Copy(DiceSet destination, DiceSet source) {
			this.destination = destination;
			this.source = source;
		}
		
		@Override
		public void onClick(View v) {
			destination.copyFrom(source);
		}		
	}
	
	private class GraphPress implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// toggle visibility
			if (topButtonGroup.getVisibility() == View.VISIBLE) {
				topButtonGroup.setVisibility(View.INVISIBLE);
			} else {
				topButtonGroup.setVisibility(View.VISIBLE);
			}
		}		
	}
	
}
