package org.kleemann.diceprobabilities;

import java.text.DecimalFormat;

import org.apache.commons.math3.fraction.BigFraction;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

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
		
		Context c = getApplicationContext();
		
		// TODO: not sure if this is where I should create ui elements that are not defined by xml
		ViewGroup container = (ViewGroup)findViewById(R.id.dice_pool);
		pd12 = new PoolDicePile(12,c,container);
		pd10 = new PoolDicePile(10,c,container);
		pd8 = new PoolDicePile(8,c,container);
		pd6 = new PoolDicePile(6,c,container);
		pd4 = new PoolDicePile(4,c,container);
		pd1 = new PoolDicePile(1,c,container);
		pResult = new PoolDicePile(1,c,container);
		
		ViewGroup container2 = (ViewGroup)findViewById(R.id.dice_current);
		cd12 = new CurrentDicePile(12,c,container2,this);
		pd12.setIncrementer(cd12);
		cd10 = new CurrentDicePile(10,c,container2,this);
		pd10.setIncrementer(cd10);
		cd8 = new CurrentDicePile(8,c,container2,this);
		pd8.setIncrementer(cd8);
		cd6 = new CurrentDicePile(6,c,container2,this);
		pd6.setIncrementer(cd6);
		cd4 = new CurrentDicePile(4,c,container2,this);
		pd4.setIncrementer(cd4);
		cd1 = new CurrentDicePile(1,c,container2,this);
		pd1.setIncrementer(cd1);
		cResult = new ToBeat(c,container2,this);
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
		//Log.v("TAG", "d0 "+d.getCumulativeProbability(cResult.getCount()).toString());
		for (int i=0 ; i<cd12.getCount() ; ++i) {
			d = new MultinomialDistribution(d, new DieDistribution(12));
		}
		//Log.v("TAG", "d12 "+d.getCumulativeProbability(cResult.getCount()).toString());
		for (int i=0 ; i<cd10.getCount() ; ++i) {
			d = new MultinomialDistribution(d, new DieDistribution(10));
		}
		//Log.v("TAG", "d10 "+d.getCumulativeProbability(cResult.getCount()).toString());
		for (int i=0 ; i<cd8.getCount() ; ++i) {
			d = new MultinomialDistribution(d, new DieDistribution(8));
		}
		//Log.v("TAG", "d8 "+d.getCumulativeProbability(cResult.getCount()).toString());
		for (int i=0 ; i<cd6.getCount() ; ++i) {
			d = new MultinomialDistribution(d, new DieDistribution(6));
		}
		//Log.v("TAG", "d6 "+d.getCumulativeProbability(cResult.getCount()).toString());
		for (int i=0 ; i<cd4.getCount() ; ++i) {
			d = new MultinomialDistribution(d, new DieDistribution(4));
		}
		//Log.v("TAG", "d4 "+d.getCumulativeProbability(cResult.getCount()).toString());
		if (cd1.getCount() > 0) {
			d = new MultinomialDistribution(d, new ConstantDistribution(cd1.getCount()));
		}
		//Log.v("TAG", "d final "+d.getCumulativeProbability(cResult.getCount()).toString());
		return d.getCumulativeProbability(cResult.getCount());
	}
}
