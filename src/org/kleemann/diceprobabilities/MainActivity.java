package org.kleemann.diceprobabilities;

import java.text.DecimalFormat;

import org.apache.commons.math3.fraction.BigFraction;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private DiceSet diceSet1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		
		this.diceSet1 = new DiceSet(
				(Button)findViewById(R.id.pool_d12),
				(Button)findViewById(R.id.pool_d10),
				(Button)findViewById(R.id.pool_d8),
				(Button)findViewById(R.id.pool_d6),
				(Button)findViewById(R.id.pool_d4),
				(Button)findViewById(R.id.pool_constant),
				(Button)findViewById(R.id.pool_target),
				(Button)findViewById(R.id.current_d12),
				(Button)findViewById(R.id.current_d10),
				(Button)findViewById(R.id.current_d8),
				(Button)findViewById(R.id.current_d6),
				(Button)findViewById(R.id.current_d4),
				(Button)findViewById(R.id.current_constant),
				(Button)findViewById(R.id.current_target),
				(Button)findViewById(R.id.current_probability),
				(GraphView)findViewById(R.id.graph)
				);
		
	}

	/* Don't need menu for now
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	*/

}
