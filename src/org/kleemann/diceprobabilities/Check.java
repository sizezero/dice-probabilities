package org.kleemann.diceprobabilities;

import java.util.Random;

import org.kleemann.diceprobabilities.distribution.Distribution;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * <p>
 * Bring up a dialog that shows the results of a simulated roll of the dice
 * resulting in a check.
 */
public class Check {

	private static final int ANIMATION_START_DELAY_MILLISECONDS = 500;
	private static final int ANIMATION_PROGRESS_DURATION_MILLISECONDS = 2500;
	private static final float ANIMATION_PROGRESS_DECELERATE_SCALE = 2.0f;

	private AlertDialog dialog;
	private TextView checkTest;
	private ProgressBar checkTargetProgress;
	private ProgressBar checkActualProgress;
	private TextView checkResult;

	private String probabilityText;
	private Distribution distribution;
	private int target;
	private Random random;

	private Wrapper wrapper;

	/**
	 * <p>
	 * On button click, the check dialog will be brought up.
	 * 
	 * @param button
	 */
	public Check(Button button) {
		Activity activity = (Activity) button.getContext();

		// inflate the dialog view from the resource and create the dialog with
		// that view
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Check");
		LayoutInflater inflater = activity.getLayoutInflater();
		View v = inflater.inflate(R.layout.check_dialog, null);
		builder.setView(v);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// no special action when user clicks ok
					}
				});
		this.dialog = builder.create();

		// get references to the dynamic controls in the view
		this.checkTest = (TextView) v.findViewById(R.id.check_test);
		this.checkTargetProgress = (ProgressBar) v
				.findViewById(R.id.check_target_progress);
		this.checkActualProgress = (ProgressBar) v
				.findViewById(R.id.check_actual_progress);
		this.checkResult = (TextView) v.findViewById(R.id.check_result);

		this.random = new Random();
		this.wrapper = new Wrapper();
		button.setOnClickListener(wrapper);
	}

	/**
	 * <p>
	 * In order to perform the check we need the target and the distribution.
	 * The probability text is handy so we don't have to re-render it.
	 * 
	 * @param probabilityText
	 * @param distribution
	 * @param target
	 */
	public void set(String probabilityText, Distribution distribution,
			int target) {
		this.probabilityText = probabilityText;
		this.distribution = distribution;
		this.target = target;
	}

	private static int percentageToColor(float f) {
		float a = f - 0.5f;
		if (a > 0.5f) {
			a -= 0.5f;
		} else {
			a += 0.5f;

		}
		a = a * a;
		if (a > 0.0f) {
			a -= 0.5f;
		} else {
			a += 0.5f;

		}
		final int gradient = (int) (a * Color.RED + (1.0f - a) * Color.GREEN);
		return gradient;
	}

	/**
	 * <p>
	 * Lots of listeners: original mouse click, dialog show, and animation
	 * events. Better to hide this complexity in an inner class.
	 * 
	 * @author robert
	 */
	private class Wrapper implements View.OnClickListener,
			DialogInterface.OnShowListener, Animator.AnimatorListener {
		private ObjectAnimator progressAnim;
		private float targetProb;
		private float actualProb;

		Wrapper() {
			dialog.setOnShowListener(this);
		}

		/**
		 * <p>
		 * Called when the button is pressed to bring up the dialog. Calculate
		 * odds and create objects that will be used while the dialog is up.
		 */
		@Override
		public void onClick(View v) {
			float targetProb = distribution.getCumulativeProbability(target)
					.floatValue();
			float actualProb = random.nextFloat();

			this.targetProb = targetProb;
			this.actualProb = actualProb;

			// clear old values
			checkTest.setText("");
			checkTargetProgress
					.setProgress((int) ((1.0f - targetProb) * 10000));
			checkActualProgress.setProgress(0);
			checkResult.setText("");

			// setup the animator for the actual progress bar
			progressAnim = ObjectAnimator.ofFloat(this, "progress", 0f,
					1.0f - actualProb);
			progressAnim.setDuration(ANIMATION_PROGRESS_DURATION_MILLISECONDS);
			progressAnim.setInterpolator(new DecelerateInterpolator(
					ANIMATION_PROGRESS_DECELERATE_SCALE));
			progressAnim.addListener(this);
			progressAnim.setStartDelay(ANIMATION_START_DELAY_MILLISECONDS);

			dialog.show();
		}

		/**
		 * <p>
		 * When the dialog is rendered, start the animation
		 */
		@Override
		public void onShow(DialogInterface dialog) {
			progressAnim.start();
		}

		/**
		 * <p>
		 * The animation has an initial delay; after that show the text
		 * description of the check
		 */
		@Override
		public void onAnimationStart(Animator animator) {
			checkTest.setText(probabilityText + " chance to roll target "
					+ target);
		}

		/**
		 * <p>The bulk of the animation updates the progress bar
		 * @param percentage
		 */
		@SuppressWarnings("unused")
		void setProgress(float percentage) {
			checkActualProgress.setProgress((int) (percentage * 10000));
			// TODO: change progress bar color
			// p.getProgressDrawable().setColorFilter(
			// percentageToColor(percentage), PorterDuff.Mode.SRC_OVER);
		}

		@Override
		public void onAnimationCancel(Animator animator) {
		}

		/**
		 * <p>Display the final result after the animation has finished
		 */
		@Override
		public void onAnimationEnd(Animator animator) {
			checkResult.setText(actualProb <= targetProb ? "Success"
					: "Failure");
		}

		@Override
		public void onAnimationRepeat(Animator animator) {
		}

	}
}
