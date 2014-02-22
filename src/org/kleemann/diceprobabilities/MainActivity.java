package org.kleemann.diceprobabilities;

import org.kleemann.diceprobabilities.graph.GraphView;
import org.kleemann.diceprobabilities.special.SpecialSpinner;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * <p>The main entry point for the program.  This android application consists 
 * of a single activity. (screen)
 * 
 *  <p>All GUI resources (ids from xml layouts) are referenced from this class.
 */
public class MainActivity extends Activity {

	// the topmost View that contains all the buttons 
	private View topButtonGroup;
	
	private GraphView graph;
	
	private DiceSet diceSet1;
	private DiceSet diceSet2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		
		this.topButtonGroup = (View)findViewById(R.id.top_button_group);

		this.graph = (GraphView)findViewById(R.id.graph); 
		
		this.diceSet1 = new DiceSet(
				(ViewGroup)findViewById(R.id.ds1_pool),
				(ViewGroup)findViewById(R.id.ds1_current),
				new DiceSet.DieType[]{
						new DiceSet.DieType(1, 
								(Button)findViewById(R.id.ds1_pool_constant), 
								(Button)findViewById(R.id.ds1_current_constant)),
						new DiceSet.DieType(4, 
								(Button)findViewById(R.id.ds1_pool_d4), 
								(Button)findViewById(R.id.ds1_current_d4)),
						new DiceSet.DieType(6, 
								(Button)findViewById(R.id.ds1_pool_d6), 
								(Button)findViewById(R.id.ds1_current_d6)),
						new DiceSet.DieType(8, 
								(Button)findViewById(R.id.ds1_pool_d8), 
								(Button)findViewById(R.id.ds1_current_d8)),
						new DiceSet.DieType(10, 
								(Button)findViewById(R.id.ds1_pool_d10), 
								(Button)findViewById(R.id.ds1_current_d10)),
						new DiceSet.DieType(12, 
								(Button)findViewById(R.id.ds1_pool_d12), 
								(Button)findViewById(R.id.ds1_current_d12)),
				},
				new SpecialSpinner(this, (Spinner)findViewById(R.id.ds1_special)),
				new DiceSet.TargetParam[]{
						new DiceSet.TargetParam(-5,
								(Button)findViewById(R.id.ds1_pool_target_m5)), 
						new DiceSet.TargetParam(-1,
								(Button)findViewById(R.id.ds1_pool_target_m1)), 
						new DiceSet.TargetParam(1,
								(Button)findViewById(R.id.ds1_pool_target_1)), 
						new DiceSet.TargetParam(5,
								(Button)findViewById(R.id.ds1_pool_target_5)), 
				},
				(Button)findViewById(R.id.ds1_current_target),
				(Button)findViewById(R.id.ds1_clear),
				(TextView)findViewById(R.id.ds1_answer_fraction),
				(Button)findViewById(R.id.ds1_answer_probability),
				graph.getSetter1()
				);

		this.diceSet2 = new DiceSet(
				(ViewGroup)findViewById(R.id.ds2_pool),
				(ViewGroup)findViewById(R.id.ds2_current),
				new DiceSet.DieType[]{
						new DiceSet.DieType(1, 
								(Button)findViewById(R.id.ds2_pool_constant), 
								(Button)findViewById(R.id.ds2_current_constant)),
						new DiceSet.DieType(4, 
								(Button)findViewById(R.id.ds2_pool_d4), 
								(Button)findViewById(R.id.ds2_current_d4)),
						new DiceSet.DieType(6, 
								(Button)findViewById(R.id.ds2_pool_d6), 
								(Button)findViewById(R.id.ds2_current_d6)),
						new DiceSet.DieType(8, 
								(Button)findViewById(R.id.ds2_pool_d8), 
								(Button)findViewById(R.id.ds2_current_d8)),
						new DiceSet.DieType(10, 
								(Button)findViewById(R.id.ds2_pool_d10), 
								(Button)findViewById(R.id.ds2_current_d10)),
						new DiceSet.DieType(12, 
								(Button)findViewById(R.id.ds2_pool_d12), 
								(Button)findViewById(R.id.ds2_current_d12)),
				},
				new SpecialSpinner(this, (Spinner)findViewById(R.id.ds2_special)),
				new DiceSet.TargetParam[]{
						new DiceSet.TargetParam(-5,
								(Button)findViewById(R.id.ds2_pool_target_m5)), 
						new DiceSet.TargetParam(-1,
								(Button)findViewById(R.id.ds2_pool_target_m1)), 
						new DiceSet.TargetParam(1,
								(Button)findViewById(R.id.ds2_pool_target_1)), 
						new DiceSet.TargetParam(5,
								(Button)findViewById(R.id.ds2_pool_target_5)), 
				},
				(Button)findViewById(R.id.ds2_current_target),
				(Button)findViewById(R.id.ds2_clear),
				(TextView)findViewById(R.id.ds2_answer_fraction),
				(Button)findViewById(R.id.ds2_answer_probability),
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

	private boolean getVerbose() { return graph.getVerbose(); }
	
	/**
	 * <p>Verbose refers to the verbosity of the graph.  When the 
	 * graph is showing more detail in verbose mode, hide the buttons.
	 * 
	 *  <p>The user toggles between these modes by pressing on the graph.
	 */
	private void setVerbose(boolean verbose) {
		if (verbose) {
			topButtonGroup.setVisibility(View.INVISIBLE);
			graph.setVerbose(true);
		} else {
			topButtonGroup.setVisibility(View.VISIBLE);
			graph.setVerbose(false);
		}
	}

	private class GraphPress implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// toggle
			setVerbose(!getVerbose());
		}		
	}

	/**
	 * <p>We only save the "source" data items. e.g. the dice and target counts.
	 * All other values such as probabilities and distribution curves are 
	 * recalculated.
	 */
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
	    diceSet1.saveInstanceState(savedInstanceState, "1");
	    diceSet2.saveInstanceState(savedInstanceState, "2");
		savedInstanceState.putBoolean("verbosity", getVerbose());
	}
	
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    // Always call the superclass so it can restore the view hierarchy
	    super.onRestoreInstanceState(savedInstanceState);
	    diceSet1.restoreInstanceState(savedInstanceState, "1");
	    diceSet2.restoreInstanceState(savedInstanceState, "2");
	    setVerbose(savedInstanceState.getBoolean("verbosity"));
	}
}
