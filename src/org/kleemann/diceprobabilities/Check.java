package org.kleemann.diceprobabilities;

import java.util.Random;

import org.kleemann.diceprobabilities.distribution.Distribution;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
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
 * 
 * <p>
 * NOTE: this class uses the android Animator classes which rely on java
 * introspection. This class should not be obfuscated by proguard or the release
 * build will silently fail to animate.
 */
public class Check {

	private static final int ANIMATION_START_DELAY_MILLISECONDS = 500;
	private static final int ANIMATION_PROGRESS_DURATION_CONSTANT_MILLISECONDS = 500;
	private static final int ANIMATION_PROGRESS_DURATION_VARIABLE_MILLISECONDS = 1500;
	private static final float ANIMATION_PROGRESS_DECELERATE_SCALE = 2.0f;

	private final AlertDialog dialog;
	private final TextView checkTest;
	private final ProgressBar checkTargetProgress;
	private final ProgressBar checkActualProgress;
	private final TextView checkResult;

	private String probabilityText;
	private Distribution distribution;
	private int target;
	private final int maxRoll;

	private final Wrapper wrapper;

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

		this.maxRoll = button.getResources().getInteger(
				R.integer.max_roll);
		
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

	/**
	 * <p>
	 * Lots of listeners: original mouse click, dialog show, and animation
	 * events. Better to hide this complexity in an inner class.
	 * 
	 * @author robert
	 */
	private class Wrapper implements View.OnClickListener,
			DialogInterface.OnShowListener, Animator.AnimatorListener {

		private Random random;
		private ObjectAnimator progressAnim;
		private float targetProb;
		private float actualProb;

		private final int successColor;
		private final int failureColor;
		private final String successText;
		private final String failureText;
		private final String descriptionFormat;

		Wrapper() {
			random = new Random();
			dialog.setOnShowListener(this);

			final Resources r = checkResult.getResources();
			successColor = r.getColor(R.color.check_success);
			failureColor = r.getColor(R.color.check_failure);
			successText = r.getString(R.string.check_success);
			failureText = r.getString(R.string.check_failure);
			descriptionFormat = r.getString(R.string.check_description);
		}

		/**
		 * <p>
		 * Called when the button is pressed to bring up the dialog. Calculate
		 * odds and create objects that will be used while the dialog is up.
		 */
		@Override
		public void onClick(View v) {
			this.targetProb = distribution.getCumulativeProbability(target)
					.floatValue();
			this.actualProb = random.nextFloat();

			// clear old values
			checkTest.setText("");
			checkTargetProgress
					.setProgress((int) ((1.0f - targetProb) * maxRoll));
			checkActualProgress.setProgress(0);
			checkResult.setText("");

			// setup the animator for the actual progress bar
			progressAnim = ObjectAnimator.ofFloat(this, "progress", 0f,
					1.0f - actualProb);
			final int duration = ANIMATION_PROGRESS_DURATION_CONSTANT_MILLISECONDS
					+ (int) ((1.0f - actualProb) * ANIMATION_PROGRESS_DURATION_VARIABLE_MILLISECONDS);
			progressAnim.setDuration(duration);
			progressAnim.setInterpolator(new DecelerateInterpolator(
					ANIMATION_PROGRESS_DECELERATE_SCALE));
			progressAnim.addListener(this);
			progressAnim.setStartDelay(ANIMATION_START_DELAY_MILLISECONDS);

			dialog.show();
		}

		/**
		 * <p>
		 * When the dialog is rendered, show the text description of the check
		 * and start the animation
		 */
		@Override
		public void onShow(DialogInterface dialog) {
			checkTest.setText(String.format(descriptionFormat, probabilityText,
					target));
			progressAnim.start();
		}

		@Override
		public void onAnimationStart(Animator animator) {
		}

		/**
		 * <p>
		 * The bulk of the animation updates the progress bar
		 * 
		 * @param percentage
		 */
		@SuppressWarnings("unused")
		void setProgress(float percentage) {
			checkActualProgress.setProgress((int) (percentage * maxRoll));
		}

		@Override
		public void onAnimationCancel(Animator animator) {
		}

		/**
		 * <p>
		 * Display the final result after the animation has finished
		 */
		@Override
		public void onAnimationEnd(Animator animator) {
			if (actualProb <= targetProb) {
				checkResult.setText(successText);
				checkResult.setTextColor(successColor);
			} else {
				checkResult.setText(failureText);
				checkResult.setTextColor(failureColor);
			}
		}

		@Override
		public void onAnimationRepeat(Animator animator) {
		}

	}
}
