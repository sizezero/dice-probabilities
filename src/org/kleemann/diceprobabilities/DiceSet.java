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
import android.util.SparseIntArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * <p>This class contains the behavior for a set of pool dice, 
 * current dice, and result values. 
 */
public class DiceSet {

	public static class DieType {
		private int sides;
		private Button pool;
		private Button current;
		public DieType(int sides, Button pool, Button current) {
			this.sides = sides;
			this.pool = pool;
			this.current = current;
		}
		public int getSides() { return sides; }
		public Button getPool() { return pool; }
		public Button getCurrent() { return current; }
	}
	
	private CurrentDicePile[] dice;
	private CurrentDicePile target;
	
	private TextView answer_fraction;
	private TextView answer_probability;
	private GraphView.Setter graphSetter;
	
	// every time the dice are changed; this is incremented
	private long serial = 0;
	// true if the background distribution calculation is running
	private boolean running = false;

	private final DecimalFormat answerFormatter;
	private final int maxFractionChars;

	private static final String APPROXIMATELY_EQUAL_TO = "\u2245";
	private static final String GREATER_THAN_OR_EQUAL_TO = "\u2265"; 
	private static final String RIGHT_ARROW = "\u21e8"; 
	
	public DiceSet(
			DieType[] dieType,
			Button clear,
			TextView answer_fraction,
			TextView answer_probability,
			GraphView.Setter graphSetter
			) {
		
		this.answerFormatter = new DecimalFormat(clear.getResources().getString(R.string.answer_format));
		this.maxFractionChars = clear.getResources().getInteger(R.integer.max_fraction_chars); 
		
		CurrentDiceChanged diceChanged = new CurrentDiceChanged();
		dice = new CurrentDicePile[dieType.length-1]; // don't allocate space for the target
		int i=0;
		for (DieType dt : dieType) {
			if (dt.getSides() == 0) {
				// zero sides signifies target
				target = new Target(dt.getCurrent(),diceChanged);
				new PoolDicePile(dt.getPool(), target);
			} else {
				dice[i] = new CurrentDicePile(dt.getSides(), dt.getCurrent(), diceChanged);
				new PoolDicePile(dt.getPool(), dice[i]);
				++i;
			}
		}
		
		clear.setOnClickListener(new Clear());
		
		this.answer_fraction = answer_fraction;
		this.answer_probability = answer_probability;
		this.graphSetter = graphSetter;
	}

	/**
	 * Copies all values from the specified "other" dice set
	 */
	public void copyFrom(DiceSet that) {
		for (int i=0 ; i<dice.length ; ++i) {
			dice[i].setCount(that.dice[i].getCount());
		}
		target.setCount(that.target.getCount());
	}
	
	private class Clear implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			for (CurrentDicePile c : dice) {
				c.clear();
			}
			target.clear();
		}		
	}

	public void saveInstanceState(Bundle savedInstanceState, String prefix) {
		for (CurrentDicePile c : dice) {
			savedInstanceState.putInt(prefix+"d"+c.getSides(), c.getCount());
		}
		savedInstanceState.putInt(prefix+"target", target.getCount());
	}
	
	public void restoreInstanceState(Bundle savedInstanceState, String prefix) {
		for (CurrentDicePile c : dice) {
			c.setCount(savedInstanceState.getInt(prefix+"d"+c.getSides()));
		}
		target.setCount(savedInstanceState.getInt(prefix+"target"));
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
			for (CurrentDicePile c : dice) {
				r.sidesToCount.put(c.getSides(),c.getCount());
			}
			r.target = target.getCount();
			
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
		public SparseIntArray sidesToCount = new SparseIntArray();
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
			for (int i=0 ; i<r.sidesToCount.size() ; ++i) {
				final int sides = r.sidesToCount.keyAt(i);
				final int count = r.sidesToCount.valueAt(i);
				if (count != 0) {
					dice.add(count+"d"+sides);
					d = new MultinomialDistribution(d, MultinomialDistribution.multiply(new DieDistribution(sides), count));
				}
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
				String fraction = f.toString();
				if (fraction.length() > maxFractionChars) {
					fraction = "! / !";
				}
				out.answerFraction = fraction + " " + APPROXIMATELY_EQUAL_TO + " ";
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
