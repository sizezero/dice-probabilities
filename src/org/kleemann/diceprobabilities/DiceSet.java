package org.kleemann.diceprobabilities;

import java.text.DecimalFormat;

import org.apache.commons.math3.fraction.BigFraction;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

/**
 * <p>This class contains the behavior for a set of pool dice, 
 * current dice, and result values. 
 */
public class DiceSet {

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
	
	private Button answerButton;
	private GraphView.Setter graphSetter;
	
	// every time the dice are changed; this is incremented
	private long serial = 0;
	// true if the background distribution calculation is running
	private boolean running = false;
		
	public DiceSet(
			Button pd12Button,
			Button pd10Button,
			Button pd8Button,
			Button pd6Button,
			Button pd4Button,
			Button pd1Button,
			Button pResultButton,
			Button cd12Button,
			Button cd10Button,
			Button cd8Button,
			Button cd6Button,
			Button cd4Button,
			Button cd1Button,
			Button cResultButton,
			Button answerButton,
			GraphView.Setter graphSetter
			) {
		
		
		pd12 = new PoolDicePile(12, pd12Button);
		pd10 = new PoolDicePile(10, pd10Button);
		pd8 = new PoolDicePile(8, pd8Button);
		pd6 = new PoolDicePile(6, pd6Button);
		pd4 = new PoolDicePile(4, pd4Button);
		pd1 = new PoolDicePile(1, pd1Button);
		pResult = new PoolDicePile(1, pResultButton);
		
		CurrentDiceChanged listener = new CurrentDiceChanged();
		cd12 = new CurrentDicePile(12, cd12Button, listener);
		pd12.setIncrementer(cd12);
		cd10 = new CurrentDicePile(10, cd10Button, listener);
		pd10.setIncrementer(cd10);
		cd8 = new CurrentDicePile(8, cd8Button, listener);
		pd8.setIncrementer(cd8);
		cd6 = new CurrentDicePile(6, cd6Button, listener);
		pd6.setIncrementer(cd6);
		cd4 = new CurrentDicePile(4, cd4Button, listener);
		pd4.setIncrementer(cd4);
		cd1 = new CurrentDicePile(1, cd1Button, listener);
		pd1.setIncrementer(cd1);
		cResult = new ToBeat(cResultButton, listener);
		pResult.setIncrementer(cResult);
		
		this.answerButton = answerButton;
		this.graphSetter = graphSetter;
	}

	private class CurrentDiceChanged implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			++serial;
			startDistributionCalculation();
		}
	}

	/**
	 * Attempts to start a new background distribution calculation. If a calculation is already 
	 * running then let it run.
	 */
	private void startDistributionCalculation() {
		if (!running) {
			running = true;
			RecalculateIn r = new RecalculateIn();
			r.serial = serial;
			r.d12 = cd12.getCount();
			r.d10 = cd10.getCount();
			r.d8 = cd8.getCount();
			r.d6 = cd6.getCount();
			r.d4 = cd4.getCount();
			r.constant = cd1.getCount();
			r.target = cResult.getCount();
			
			answerButton.setText("?");
			
			new CalculateDistribution().execute(r);
		}
	}
	
	/**
	 * Just a payload into the background calculation job
	 */
	private static class RecalculateIn {
		public long serial;
		public int d12;
		public int d10;
		public int d8;
		public int d6;
		public int d4;
		public int constant;
		public int target;
	}
	
	/**
	 * Just a payload out of the background calculation job
	 */
	private static class RecalculateOut {
		public long serial;
		public Distribution distribution;
		public int target;
	}
	
	private class CalculateDistribution extends AsyncTask<RecalculateIn, Void, RecalculateOut> {

		/**
		 * Calculate the full distribution of the current dice. This thread is run in the background so it
		 * shouldn't access any other objects in this class or call android gui methods.
		 */
		@Override
		protected RecalculateOut doInBackground(RecalculateIn... arg0) {
			RecalculateIn r = arg0[0];
			
			Distribution d = new ZeroDistribution(); // identity
			for (int i=0 ; i<r.d12 ; ++i) {
				d = new MultinomialDistribution(d, new DieDistribution(12));
			}
			for (int i=0 ; i<r.d10 ; ++i) {
				d = new MultinomialDistribution(d, new DieDistribution(10));
			}
			for (int i=0 ; i<r.d8 ; ++i) {
				d = new MultinomialDistribution(d, new DieDistribution(8));
			}
			for (int i=0 ; i<r.d6 ; ++i) {
				d = new MultinomialDistribution(d, new DieDistribution(6));
			}
			for (int i=0 ; i<r.d4 ; ++i) {
				d = new MultinomialDistribution(d, new DieDistribution(4));
			}
			if (r.constant > 0) {
				d = new MultinomialDistribution(d, new ConstantDistribution(r.constant));
			}
			RecalculateOut out = new RecalculateOut();
			out.serial = r.serial;
			out.distribution = d;
			out.target = r.target;
			return out;
		}
		
		@Override
		protected void onPostExecute(RecalculateOut r) {
			running = false;
			if (r.serial == serial) {

				BigFraction f = r.distribution.getCumulativeProbability(r.target);
				DecimalFormat formatter = new DecimalFormat("##.#%");
				final String approximatelyEqualTo = "\u2245";
				String s = f.toString() + " "+ approximatelyEqualTo +" " + formatter.format(f.doubleValue());
			
				answerButton.setText(s);
				graphSetter.setResult(r.distribution, r.target);
			} else {
				// the dice have changed since we started the background task
				// run the calculation again
				startDistributionCalculation();
			}
		}
	}
}
