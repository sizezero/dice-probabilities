package org.kleemann.diceprobabilities.graph;

import org.apache.commons.math3.fraction.BigFraction;
import org.kleemann.diceprobabilities.R;
import org.kleemann.diceprobabilities.distribution.ConstantDistribution;
import org.kleemann.diceprobabilities.distribution.Distribution;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * <p>
 * A custom ui control that displays the background graph on the main activity.
 * Graphs for two distributions are displayed.
 * 
 * <p>
 * The basic graph just shows the probability curves and the horizontal lines
 * showing the percentages of the two distributions. This is the false verbose
 * setting and is what is shown when all the ui buttons are displayed.
 * 
 * <p>
 * The enhanced graph adds formulas, percentages, crosshairs and horizontal tick
 * markers. This is the true verbose setting and is what is shown when all the
 * ui buttons are hidden.
 * 
 * <p>
 * When either of the set distribution and target methods are called, a
 * background thread is run to regerate all display objects. This allows the
 * onDraw() method to be quick and efficient.
 */
public class GraphView extends View {

	/**
	 * Just a payload into the background calculation job
	 */
	private static class CalculateIn {
		public long serial = -1;
		public Distribution[] dist = new Distribution[] {
				ConstantDistribution.ZERO, ConstantDistribution.ZERO };
		public int[] target = new int[2];
		public String[] answerText = new String[] { "", "" };
		public int width;
		public int height;

		public CalculateIn() {
		}

		public CalculateIn(CalculateIn that) {
			this.serial = that.serial;
			this.dist = that.dist.clone();
			this.target = that.target.clone();
			this.answerText = that.answerText.clone();
			this.width = that.width;
			this.height = that.height;
		}
	}

	private CalculateIn in = new CalculateIn();

	/**
	 * Just a payload out of the background calculation job
	 */
	private static class CalculateOut {
		public long serial = -1;
		public Path[] path = new Path[2];
		public Paint[] paint = new Paint[2];
		public int[] target = new int[2];
		public Point[] answer = new Point[2];
		public int ticks;
		public String[] answerText = new String[2];
	}

	private CalculateOut out = new CalculateOut();

	// true if the background distribution calculation is running
	private boolean running = false;

	private boolean verbose = false;

	// all data members below here are thread-safe

	private Paint pBackground;
	private Paint pGraphSolid1;
	private Paint pGraphSolid2;
	private Paint pGraphStroke;
	private Paint pAnswer;
	private Paint pRuler;
	private float rulerPercent5;
	private float rulerPercent10;
	private Drawable crosshairs;
	private final float crosshairRadius;
	private Paint pAnswerText;
	private final float answerTextOffsetY;
	{
		pBackground = new Paint();
		pBackground.setColor(getResources().getColor(R.color.graph_background));

		pGraphSolid1 = new Paint();
		pGraphSolid1.setStrokeWidth(getResources().getDimension(
				R.dimen.graph_stroke_width));
		pGraphSolid1.setStyle(Paint.Style.FILL);
		pGraphSolid1.setColor(getResources().getColor(R.color.graph_solid1));

		pGraphSolid2 = new Paint(pGraphSolid1);
		pGraphSolid2.setColor(getResources().getColor(R.color.graph_solid2));

		pGraphStroke = new Paint(pGraphSolid1);
		pGraphStroke.setStyle(Paint.Style.STROKE);
		pGraphStroke.setColor(getResources().getColor(R.color.graph_stroke));

		pAnswer = new Paint(pGraphStroke);
		pAnswer.setStrokeWidth(getResources().getDimension(
				R.dimen.answer_stroke_width));
		pAnswer.setColor(getResources().getColor(R.color.graph_answer));

		pRuler = new Paint(pGraphStroke);
		pRuler.setStrokeWidth(getResources().getDimension(
				R.dimen.ruler_stroke_width));
		pRuler.setColor(getResources().getColor(R.color.graph_ruler));
		TypedValue typedValue = new TypedValue();
		getResources().getValue(R.dimen.ruler_percent_5, typedValue, true);
		this.rulerPercent5 = typedValue.getFloat();
		getResources().getValue(R.dimen.ruler_percent_10, typedValue, true);
		this.rulerPercent10 = typedValue.getFloat();

		crosshairs = getResources().getDrawable(R.drawable.crosshairs);
		crosshairRadius = getResources().getDimension(R.dimen.crosshair_radius);

		pAnswerText = new Paint();
		pAnswerText.setColor(getResources()
				.getColor(R.color.graph_formula_text));
		pAnswerText.setTextSize(getResources().getDimension(
				R.dimen.formula_text_size));
		answerTextOffsetY = getResources().getDimension(
				R.dimen.formula_text_offset_y);
	}

	public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GraphView(Context context) {
		super(context);
	}

	public static interface Setter {
		public void setResult(Distribution distribution, int target,
				String answerText);
	}

	private class SetGraph implements Setter {
		private final int i;

		public SetGraph(int i) {
			this.i = i;
		}

		public void setResult(Distribution d, int target, String answerText) {
			in.dist[i] = d;
			in.target[i] = target;
			in.answerText[i] = answerText;
			++in.serial;
			startCalculation();
		}
	}

	public Setter getSetter1() {
		return new SetGraph(0);
	}

	public Setter getSetter2() {
		return new SetGraph(1);
	}

	public void setVerbose(boolean verbose) {
		if (this.verbose != verbose) {
			this.verbose = verbose;
			invalidate();
		}
	}

	public boolean getVerbose() {
		return verbose;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		in.width = w;
		in.height = h;
		++in.serial;
		startCalculation();
	}

	private void startCalculation() {
		if (!running) {
			running = true;
			// not sure if cloning is necessary to protect thread access
			new CalculatePoints().executeOnExecutor(
					AsyncTask.THREAD_POOL_EXECUTOR, new CalculateIn(in));
		}
	}

	private class CalculatePoints extends
			AsyncTask<CalculateIn, Void, CalculateOut> {

		/**
		 * This thread is run in the background so it shouldn't access any
		 * non-thread-safe objects in this class or call android gui methods.
		 */
		@Override
		protected CalculateOut doInBackground(CalculateIn... arg0) {
			CalculateIn in = arg0[0];
			CalculateOut out = new CalculateOut();
			out.serial = in.serial;

			// don't display anything if both graphs are trivial
			if (in.dist[0].isZero() && in.dist[1].isZero()) {
				return out;
			}

			// draw the larger distribution first so that both
			// are visible
			Distribution[] dist = new Distribution[2];
			if (greaterCumulative(in.dist[0], in.dist[1])) {
				dist[0] = in.dist[0];
				out.target[0] = in.target[0];
				out.paint[0] = pGraphSolid1;
				out.answerText[0] = in.answerText[0];
				dist[1] = in.dist[1];
				out.target[1] = in.target[1];
				out.paint[1] = pGraphSolid2;
				out.answerText[1] = in.answerText[1];
			} else {
				dist[0] = in.dist[1];
				out.target[0] = in.target[1];
				out.paint[0] = pGraphSolid2;
				out.answerText[0] = in.answerText[1];
				dist[1] = in.dist[0];
				out.target[1] = in.target[0];
				out.paint[1] = pGraphSolid1;
				out.answerText[1] = in.answerText[0];
			}

			final int largestSize = Math.max(dist[0].upperBound(),
					dist[1].upperBound());

			// add a few extra values after the distribution peaks; multiples of
			// 10
			out.ticks = (largestSize + 10) - (largestSize % 10);
			Point[] pt = new Point[out.ticks];

			// for each of the two distributions
			for (int j = 0; j < 2; ++j) {

				// don't display a trivial graph
				if (dist[j].isZero()) {
					out.path[j] = new Path();
					out.answer[j] = new Point(0.0f, in.height);
					continue;
				}

				// find the points of the curve
				for (int i = 0; i < out.ticks; ++i) {
					float x = (float) i / out.ticks;

					// TODO: it would be more efficient to compute all the
					// cumulative
					// distributions at once
					float y = BigFraction.ONE.subtract(
							dist[j].getCumulativeProbability(i)).floatValue();

					pt[i] = new Point(x * in.width, y * in.height); // scale to
																	// global
																	// coords
				}

				// smooth the curve and create a Path object
				out.path[j] = new Interpolate(pt).getPath();

				// Right now we just have a curvy line. Connect it the end point
				// to the origin and then to the starting point to make a 2d
				// solid.
				// Move off the screen to the bottom and left a bit so we don't
				// see the stroke
				// on that part of the solid.
				float leftWall = -in.width / 5;
				float bottomWall = in.height * 1.2f;
				out.path[j].lineTo(pt[pt.length - 1].getX(), bottomWall);
				out.path[j].lineTo(0.0f, in.height);
				out.path[j].lineTo(leftWall, bottomWall);
				out.path[j].lineTo(leftWall, 0.0f);
				out.path[j].lineTo(pt[0].getX(), pt[0].getY());

				out.answer[j] = new Point((float) out.target[j] / out.ticks
						* in.width, (1.0f - dist[j].getCumulativeProbability(
						out.target[j]).floatValue())
						* in.height);
			}

			return out;
		}

		/**
		 * <p>
		 * Run from the GUI thread. Safe to call GUI methods and access and data
		 * members in DiceSet.
		 */
		@Override
		protected void onPostExecute(CalculateOut cout) {
			running = false;
			if (cout.serial == in.serial) {
				out = cout;
				invalidate();
			} else {
				// something has changed since the last time; restart
				// calculation
				startCalculation();
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPaint(pBackground);

		// don't display anything if the background calculation has not occurred
		// once
		if (out.path[0] == null) {
			return;
		}

		final int h = canvas.getHeight();
		final int w = canvas.getWidth();

		// draw each graph solid and outline
		for (int j = 0; j < 2; ++j) {
			canvas.drawPath(out.path[j], out.paint[j]);
			canvas.drawPath(out.path[j], pGraphStroke);
		}

		// draw horizontal answer line
		for (int j = 0; j < 2; ++j) {
			final float y = out.answer[j].getY();
			canvas.drawLine(0.0f, y, (float) w, y, pAnswer);
		}

		// anything after this line only appears in verbose mode
		// (when the buttons are hidden)
		if (!verbose) {
			return;
		}

		// add some tick marks to the 5 and 10 x spots
		{
			final float y10 = h - (float) h * rulerPercent10;
			final float y5 = h - (float) h * rulerPercent5;
			for (int i = 0; i <= out.ticks; ++i) {
				if (i % 10 == 0) {
					final float x = (float) i / out.ticks * w;
					canvas.drawLine(x, h, x, y10, pRuler);
				} else if (i % 5 == 0) {
					final float x = (float) i / out.ticks * w;
					canvas.drawLine(x, h, x, y5, pRuler);
				}
			}
		}

		// display the crosshairs on the target spot
		for (int j = 0; j < 2; ++j) {
			final float x = out.answer[j].getX();
			final float y = out.answer[j].getY();
			crosshairs.setBounds((int) (x - crosshairRadius),
					(int) (y - crosshairRadius), (int) (x + crosshairRadius),
					(int) (y + crosshairRadius));
			crosshairs.draw(canvas);
		}

		// display the answer text
		for (int j = 0; j < 2; ++j) {
			canvas.drawText(out.answerText[j], 0.0f, out.answer[j].getY()
					+ answerTextOffsetY, pAnswerText);
		}

		/*
		 * // draw bounds X p.setColor(Color.YELLOW); // box canvas.drawLine(0,
		 * 0, w-1, 0, p); canvas.drawLine(0, h-1, w-1, h-1, p);
		 * canvas.drawLine(0, 0, 0, h-1, p); canvas.drawLine(w-1, 0, w-1, h-1,
		 * p); // X canvas.drawLine(0, 0, w-1, h-1, p); canvas.drawLine(0, h-1,
		 * w-1, 0, p);
		 */
	}

	/**
	 * <p>
	 * Returns true if d1>d2
	 * 
	 * <p>
	 * TODO: move to a utility class
	 */
	private static boolean greaterCumulative(Distribution d1, Distribution d2) {
		// assumes cumulative distributions are strictly decreasing
		final int n = Math.max(d1.upperBound(), d2.upperBound()) + 1;
		for (int i = 0; i < n; ++i) {
			// TODO this looks O(n*m) due to the slow implementation of
			// getCumulativeProbability()
			if (d1.getCumulativeProbability(i).compareTo(
					d2.getCumulativeProbability(i)) == 1) {
				return true;
			}
		}
		return false;
	}
}
