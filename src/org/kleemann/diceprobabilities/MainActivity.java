package org.kleemann.diceprobabilities;

import java.text.DecimalFormat;

import org.apache.commons.math3.fraction.BigFraction;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener {

	private PoolDicePile pd12;
	private PoolDicePile pd10;
	private PoolDicePile pd8;
	private PoolDicePile pd6;
	private PoolDicePile pd4;
	private PoolDicePile pd1;
	private PoolDicePile pResult;
	
	private CurrentDicePile cd12;
	private CurrentDicePile cd10;
	private CurrentDicePile cd8;
	private CurrentDicePile cd6;
	private CurrentDicePile cd4;
	private CurrentDicePile cd1;
	private CurrentDicePile cResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		
		pd12 = new PoolDicePile(12, (Button)findViewById(R.id.pool_d12));
		pd10 = new PoolDicePile(10, (Button)findViewById(R.id.pool_d10));
		pd8 = new PoolDicePile(8, (Button)findViewById(R.id.pool_d8));
		pd6 = new PoolDicePile(6, (Button)findViewById(R.id.pool_d6));
		pd4 = new PoolDicePile(4, (Button)findViewById(R.id.pool_d4));
		pd1 = new PoolDicePile(1, (Button)findViewById(R.id.pool_constant));
		pResult = new PoolDicePile(1, (Button)findViewById(R.id.pool_target));
		
		cd12 = new CurrentDicePile(12, (Button)findViewById(R.id.current_d12), this);
		pd12.setIncrementer(cd12);
		cd10 = new CurrentDicePile(10, (Button)findViewById(R.id.current_d10), this);
		pd10.setIncrementer(cd10);
		cd8 = new CurrentDicePile(8, (Button)findViewById(R.id.current_d8), this);
		pd8.setIncrementer(cd8);
		cd6 = new CurrentDicePile(6, (Button)findViewById(R.id.current_d6), this);
		pd6.setIncrementer(cd6);
		cd4 = new CurrentDicePile(4, (Button)findViewById(R.id.current_d4), this);
		pd4.setIncrementer(cd4);
		cd1 = new CurrentDicePile(1, (Button)findViewById(R.id.current_constant), this);
		pd1.setIncrementer(cd1);
		cResult = new ToBeat((Button)findViewById(R.id.current_target), this);
		pResult.setIncrementer(cResult);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * <p>Anytime any of the current dice are changed, this is called.  Recalculate the probabilities.
	 */
	@Override
	public void onClick(View v) {
		BigFraction f = getCumulativeProbability();
		DecimalFormat formatter = new DecimalFormat("##.#%");
		final String approximatelyEqualTo = "\u2245";
		String s = f.toString() + " "+ approximatelyEqualTo +" " + formatter.format(f.doubleValue());
		
		Button prob = (Button)findViewById(R.id.current_probability);
		prob.setText(s);
	}
	
	private BigFraction getCumulativeProbability() {
		// TODO: may want to do this in the background
		
		Distribution d = new ZeroDistribution(); // identity
		boolean atLeastOne = false;
		for (int i=0 ; i<cd12.getCount() ; ++i) {
			d = new MultinomialDistribution(d, new DieDistribution(12));
			atLeastOne = true;
		}
		for (int i=0 ; i<cd10.getCount() ; ++i) {
			d = new MultinomialDistribution(d, new DieDistribution(10));
			atLeastOne = true;
		}
		for (int i=0 ; i<cd8.getCount() ; ++i) {
			d = new MultinomialDistribution(d, new DieDistribution(8));
			atLeastOne = true;
		}
		for (int i=0 ; i<cd6.getCount() ; ++i) {
			d = new MultinomialDistribution(d, new DieDistribution(6));
			atLeastOne = true;
		}
		for (int i=0 ; i<cd4.getCount() ; ++i) {
			d = new MultinomialDistribution(d, new DieDistribution(4));
			atLeastOne = true;
		}
		if (cd1.getCount() > 0) {
			d = new MultinomialDistribution(d, new ConstantDistribution(cd1.getCount()));
			atLeastOne = true;
		}
		if (atLeastOne) {
			return d.getCumulativeProbability(cResult.getCount());
		} else { 
			return BigFraction.ZERO;
		}
	}
}
