package org.kleemann.diceprobabilities;

import java.text.DecimalFormat;

import org.apache.commons.math3.fraction.BigFraction;
import org.kleemann.diceprobabilities.distribution.Distribution;
import org.kleemann.diceprobabilities.graph.GraphView;
import org.kleemann.diceprobabilities.special.Special;
import org.kleemann.diceprobabilities.special.SpecialSpinner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * <p>This class contains the behavior for a set of pool dice, 
 * current dice, and result values. 
 * 
 * <p>Changes to the dice cause a background task to calculate the 
 * new result probability and distribution and report it to the 
 * passed GraphSettor.
 */
public class DiceSet {

	/**
	 * <p>Caller must pass an array of these objects to the DiceSet constructor.
	 * A pressing a pool die adds to the target die.  Pressing a target die
	 * decreases it's value.
	 */
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
	
	public static class TargetParam {
		private int increment;
		private Button button;
		public TargetParam(int increment, Button button) {
			this.increment = increment;
			this.button = button;
		}
		public int getIncrement() { return increment; }
		public Button getButton() { return button; }
	}
	
	private CurrentDicePile[] dice;
	private SpecialSpinner specialSpinner;
	private Target target;
	
	private TextView answerFraction;
	private Button answerProbability;
	private Check check;
	private GraphView.Setter graphSetter;
	
	// every time the dice are changed; this is incremented
	private long serial = 0;
	// true if the background distribution calculation job is running
	private boolean running = false;

	// data members below here are thread-safe
	
	private final DecimalFormat answerFormatter;
	private final String answerHighest;
	private final String answerSecondHighest;
	private final int maxFractionChars;
	
	private static final String APPROXIMATELY_EQUAL_TO = "\u2245";
	
	public DiceSet(
			ViewGroup poolViewGroup,
			ViewGroup currentViewGroup,
			DieType[] dieType,
			SpecialSpinner specialSpinner,
			TargetParam[] targetParam,
			Button targetButton,
			Button clear,
			TextView answerFraction,
			Button answerProbability,
			GraphView.Setter graphSetter
			) {
		
		this.answerFormatter = new DecimalFormat(
				clear.getResources().getString(R.string.answer_format));
		this.answerHighest = answerFormatter.format(1.0d);
		this.answerSecondHighest = clear.getResources().getString(R.string.answer_second_highest);
		this.maxFractionChars = clear.getResources().getInteger(R.integer.max_fraction_chars);
		
		// associate each Button object with a behavioral object
		CurrentDiceChanged diceChanged = new CurrentDiceChanged();
		dice = new CurrentDicePile[dieType.length];
		int i = 0;
		for (DieType dt : dieType) {
			dice[i] = CurrentDicePile.create(dt.getSides(), dt.getCurrent(), diceChanged);
			new PoolDicePile(dt.getPool(), dice[i]);
			++i;
		}
		
		target = new Target(targetButton, diceChanged);
		for (TargetParam tp : targetParam) {
			new TargetPool(tp.getIncrement(), tp.getButton(), target);
		}
		
		assert(target != null);

		this.specialSpinner = specialSpinner;
		specialSpinner.setChangeListener(diceChanged);
		this.answerFraction = answerFraction;
		this.answerProbability = answerProbability;
		this.check = new Check(answerProbability);
		this.graphSetter = graphSetter;

		final View.OnClickListener clearListener = new Clear();
		clear.setOnClickListener(clearListener);
		// explicit clear is necessary to set current constant to GONE
		clearListener.onClick(clear);
		
		// iss13: have the pool gridview and the current linearlayout "eat" clicks so that the user doesn't accidentally
		// select the background graph
		final View.OnClickListener doNothing = new View.OnClickListener(){
			public void onClick(View v) {}
		};
		poolViewGroup.setOnClickListener(doNothing);
		currentViewGroup.setOnClickListener(doNothing);
	}

	/**
	 * Copies all values from the specified "other" dice set
	 */
	public void copyFrom(DiceSet that) {
		for (int i=0 ; i<dice.length ; ++i) {
			dice[i].setCount(that.dice[i].getCount());
			// special case to not display constant of zero
			if (dice[i].getSides()==1 && dice[i].getCount()==0) {
				dice[i].clear();
			}
		}
		target.setCount(that.target.getCount());
		specialSpinner.setSelectedItemPosition(that.specialSpinner.getSelectedItemPosition());
	}
	
	/**
	 * <p>Resets current dice and target to their default values.
	 */
	private class Clear implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			for (CurrentDicePile c : dice) {
				c.clear();
			}
			target.clear();
			specialSpinner.setSelectedItemPosition(0);
		}		
	}

	public void saveInstanceState(Bundle savedInstanceState, String prefix) {
		for (CurrentDicePile c : dice) {
			savedInstanceState.putInt(prefix+"d"+c.getSides(), c.getCount());
		}
		savedInstanceState.putInt(prefix+"target", target.getCount());
		savedInstanceState.putInt(prefix+"spinner", specialSpinner.getSelectedItemPosition());
	}
	
	public void restoreInstanceState(Bundle savedInstanceState, String prefix) {
		for (CurrentDicePile c : dice) {
			c.setCount(savedInstanceState.getInt(prefix+"d"+c.getSides()));
			// special case to not display constant of zero
			if (c.getSides()==1 && c.getCount()==0) {
				c.clear();
			}
		}
		target.setCount(savedInstanceState.getInt(prefix+"target"));
		specialSpinner.setSelectedItemPosition(savedInstanceState.getInt(prefix+"spinner"));
	}

	/**
	 * <p>When any of the dice have changed, note the change by incrementing
	 * serial and start the background job.
	 */
	private class CurrentDiceChanged implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			++serial;
			startBackgroundJob();
		}
	}

	/**
	 * <p>Just a payload into the background calculation job.
	 * The background job cannot only write to these values.
	 */
	private static class BackgroundIn {
		public long serial;
		// current dice: number of sides, and number of dice pairs
		public SparseIntArray sidesToCount = new SparseIntArray();
		public int target;
		public Special special;
	}
	
	/**
	 * <p>Just a payload out of the background calculation job.
	 * Used both to set the answer values as well as to update
	 * the background graph.
	 */
	private static class BackgroundOut {
		public long serial;
		public Distribution distribution;
		public int target;
		public String answerFraction;
		public String answerProbability;
		public String answerFormula;
	}
	
	/**
	 * Attempts to start a new background distribution calculation. If a calculation is already 
	 * running then let it run, it will check that the data has changed and start 
	 * another Job.
	 */
	private void startBackgroundJob() {
		if (!running) {
			running = true;
			BackgroundIn in = new BackgroundIn();
			in.serial = serial;
			for (CurrentDicePile c : dice) {
				in.sidesToCount.put(c.getSides(),c.getCount());
			}
			in.target = target.getCount();
			in.special = specialSpinner.getSelected();
			
			answerFraction.setText("");
			answerProbability.setText("?");
			
			new BackgroundJob().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, in);
		}
	}
	
	private class BackgroundJob extends AsyncTask<BackgroundIn, Void, BackgroundOut> {

		/**
		 * Calculate the full distribution of the current dice. This thread is run in the background so it
		 * shouldn't access any other non-threadsafe objects in DiceSet or call android gui methods.
		 * The only threadsafe methods in DiceSet are those that are initialized in the constructor
		 * and never changed such as loaded resource values. 
		 */
		@Override
		protected BackgroundOut doInBackground(BackgroundIn... arg0) {
			BackgroundIn in = arg0[0];
			
			// calculate the distribution
			Distribution d = in.special.getDistribution(in.sidesToCount);
			// no modification to d after this; cache the cumulative values
			d = d.cacheCumulative();
			
			BackgroundOut out = new BackgroundOut();
			out.serial = in.serial;
			out.distribution = d;
			out.target = in.target;
			
			if (d.isZero()) {
				// if distribution is trivial then show minimal text
				out.answerFraction = "";
				out.answerProbability = answerFormatter.format(0.0d);
			} else {
				final BigFraction f = d.getCumulativeProbability(in.target);
				
				String fraction = f.toString();
				if (fraction.length() > maxFractionChars) {
					fraction = "! / !";
				}
				out.answerFraction = " " + APPROXIMATELY_EQUAL_TO + " " + fraction;
				
				out.answerProbability = answerFormatter.format(f.doubleValue());
				// don't round to 100% ; only show 100% if the probability is exactly 1.0
				if (out.answerProbability.equals(answerHighest) && !f.equals(BigFraction.ONE)) {
					out.answerProbability = answerSecondHighest;
				}
			}
			
			// format the textual answer of the distribution at the target
			out.answerFormula = in.special.getFormula(in.sidesToCount, in.target, out.answerProbability);
			
			return out;
		}
		
		/**
		 * <p>Run from the GUI thread.  Safe to call GUI methods and access 
		 * and data members in DiceSet.
		 */
		@Override
		protected void onPostExecute(BackgroundOut out) {
			running = false;
			if (out.serial == serial) {
				answerFraction.setText(out.answerFraction);
				answerProbability.setText(out.answerProbability);
				check.set(out.answerProbability, out.distribution, out.target);
				graphSetter.setResult(out.distribution, out.target, out.answerFormula);
			} else {
				// the dice have changed since we started the background task
				// run the calculation again
				startBackgroundJob();
			}
		}
	}
}
