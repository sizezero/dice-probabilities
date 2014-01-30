package org.kleemann.diceprobabilities;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.commons.math3.fraction.BigFraction;
import org.kleemann.diceprobabilities.distribution.ConstantDistribution;
import org.kleemann.diceprobabilities.distribution.DieDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.distribution.MultinomialDistribution;
import org.kleemann.diceprobabilities.graph.GraphView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * <p>This class contains the behavior for a set of pool dice, 
 * current dice, and result values. 
 */
public class DiceSet {

	private CurrentDicePile cd12;
	private CurrentDicePile cd10;
	private CurrentDicePile cd8;
	private CurrentDicePile cd6;
	private CurrentDicePile cd4;
	private CurrentDicePile cd1;
	private CurrentDicePile cResult;
	
	private TextView answer_fraction;
	private TextView answer_probability;
	private GraphView.Setter graphSetter;
	
	// every time the dice are changed; this is incremented
	private long serial = 0;
	// true if the background distribution calculation is running
	private boolean running = false;

	private final DecimalFormat answerFormatter;

	private static final String APPROXIMATELY_EQUAL_TO = "\u2245";
	private static final String GREATER_THAN_OR_EQUAL_TO = "\u2265"; 
	private static final String RIGHT_ARROW = "\u21e8"; 
	
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
			Button clear,
			TextView answer_fraction,
			TextView answer_probability,
			GraphView.Setter graphSetter
			) {
		
		this.answerFormatter = new DecimalFormat(clear.getResources().getString(R.string.answer_format));
		
		CurrentDiceChanged diceChanged = new CurrentDiceChanged();
		cd12 = new CurrentDicePile(12, cd12Button, diceChanged);
		cd10 = new CurrentDicePile(10, cd10Button, diceChanged);
		cd8 = new CurrentDicePile(8, cd8Button, diceChanged);
		cd6 = new CurrentDicePile(6, cd6Button, diceChanged);
		cd4 = new CurrentDicePile(4, cd4Button, diceChanged);
		cd1 = new CurrentDicePile(1, cd1Button, diceChanged);
		cResult = new Target(cResultButton, diceChanged);

		new PoolDicePile(12, pd12Button, cd12);
		new PoolDicePile(10, pd10Button, cd10);
		new PoolDicePile(8, pd8Button, cd8);
		new PoolDicePile(6, pd6Button, cd6);
		new PoolDicePile(4, pd4Button, cd4);
		new PoolDicePile(1, pd1Button, cd1);
		new PoolDicePile(1, pResultButton, cResult);

		clear.setOnClickListener(new Clear());
		
		this.answer_fraction = answer_fraction;
		this.answer_probability = answer_probability;
		this.graphSetter = graphSetter;
	}

	/**
	 * Copies all values from the specified "other" dice set
	 */
	public void copyFrom(DiceSet that) {
		cd12.setCount(that.cd12.getCount());
		cd10.setCount(that.cd10.getCount());
		cd8.setCount(that.cd8.getCount());
		cd6.setCount(that.cd6.getCount());
		cd4.setCount(that.cd4.getCount());
		cd1.setCount(that.cd1.getCount());
		cResult.setCount(that.cResult.getCount());
		
	}
	
	private class Clear implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			cd12.clear();
			cd10.clear();
			cd8.clear();
			cd6.clear();
			cd4.clear();
			cd1.clear();
			cResult.clear();
		}		
	}

	public void saveInstanceState(Bundle savedInstanceState, String prefix) {
		savedInstanceState.putInt(prefix+"d12", cd12.getCount());
		savedInstanceState.putInt(prefix+"d10", cd10.getCount());
		savedInstanceState.putInt(prefix+"d8", cd8.getCount());
		savedInstanceState.putInt(prefix+"d6", cd6.getCount());
		savedInstanceState.putInt(prefix+"d4", cd4.getCount());
		savedInstanceState.putInt(prefix+"constant", cd1.getCount());
		savedInstanceState.putInt(prefix+"target", cResult.getCount());
	}
	
	public void restoreInstanceState(Bundle savedInstanceState, String prefix) {
		cd12.setCount(savedInstanceState.getInt(prefix+"d12"));
		cd10.setCount(savedInstanceState.getInt(prefix+"d10"));
		cd8.setCount(savedInstanceState.getInt(prefix+"d8"));
		cd6.setCount(savedInstanceState.getInt(prefix+"d6"));
		cd4.setCount(savedInstanceState.getInt(prefix+"d4"));
		cd1.setCount(savedInstanceState.getInt(prefix+"constant"));
		cResult.setCount(savedInstanceState.getInt(prefix+"target"));
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
			
			answer_fraction.setText("");
			answer_probability.setText("?");
			
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
		public String answerFraction;
		public String answerProbability;
		public String answerFormula;
	}
	
	private class CalculateDistribution extends AsyncTask<RecalculateIn, Void, RecalculateOut> {

		/**
		 * Calculate the full distribution of the current dice. This thread is run in the background so it
		 * shouldn't access any other objects in this class or call android gui methods.
		 */
		@Override
		protected RecalculateOut doInBackground(RecalculateIn... arg0) {
			RecalculateIn r = arg0[0];
			
			Distribution d = ConstantDistribution.ZERO;
			ArrayList<String> dice = new ArrayList<String>();
			if (r.d12 > 0) {
				dice.add(r.d12+"d12");
				d = new MultinomialDistribution(d, MultinomialDistribution.multiply(new DieDistribution(12), r.d12));
			}
			if (r.d10 > 0) {
				dice.add(r.d10+"d10");
				d = new MultinomialDistribution(d, MultinomialDistribution.multiply(new DieDistribution(10), r.d10));
			}
			if (r.d8 > 0) {
				dice.add(r.d8+"d8");
				d = new MultinomialDistribution(d, MultinomialDistribution.multiply(new DieDistribution(8), r.d8));
			}
			if (r.d6 > 0) {
				dice.add(r.d6+"d6");
				d = new MultinomialDistribution(d, MultinomialDistribution.multiply(new DieDistribution(6), r.d6));
			}
			if (r.d4 > 0) {
				dice.add(r.d4+"d4");
				d = new MultinomialDistribution(d, MultinomialDistribution.multiply(new DieDistribution(4), r.d4));
			}
			if (r.constant > 0) {
				dice.add(Integer.toString(r.constant));
				d = new MultinomialDistribution(d, new ConstantDistribution(r.constant));
			}
			RecalculateOut out = new RecalculateOut();
			out.serial = r.serial;
			out.distribution = d;
			out.target = r.target;
			
			// if distribution is trivial then show minimal text
			if (d.size() <= 1) {
				out.answerFraction = "";
				out.answerProbability = answerFormatter.format(0.0d);
			} else {
				BigFraction f = d.getCumulativeProbability(r.target);
				out.answerFraction = f.toString() + " " + APPROXIMATELY_EQUAL_TO + " ";
				out.answerProbability = answerFormatter.format(f.doubleValue());
			}
			
			if (dice.size()==0) {
				out.answerFormula = "";
			} else {
				StringBuilder sb = new StringBuilder(dice.get(0));
				for (int i=1 ; i<dice.size() ; ++i) {
					sb.append(" + ");
					sb.append(dice.get(i));
				}
				sb.append(" ");
				sb.append(GREATER_THAN_OR_EQUAL_TO);
				sb.append(" ");
				sb.append(r.target);
				sb.append(" ");
				sb.append(RIGHT_ARROW);
				sb.append(" ");
				sb.append(out.answerProbability);
				out.answerFormula = sb.toString();
			}
			
			return out;
		}
		
		@Override
		protected void onPostExecute(RecalculateOut r) {
			running = false;
			if (r.serial == serial) {
				answer_fraction.setText(r.answerFraction);
				answer_probability.setText(r.answerProbability);
				graphSetter.setResult(r.distribution, r.target, r.answerFormula);
			} else {
				// the dice have changed since we started the background task
				// run the calculation again
				startDistributionCalculation();
			}
		}
	}
}
