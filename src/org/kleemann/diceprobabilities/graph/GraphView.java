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

public class GraphView extends View {

	/**
	 * Just a payload into the background calculation job
	 */
	private static class CalculateIn {
		public long serial = -1;
		public Distribution[] dist = new Distribution[]{ ConstantDistribution.ZERO, ConstantDistribution.ZERO};
		public int[] target = new int[2];
		public String[] answerText = new String[]{"", ""};
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
		public Path [] path = new Path[2];
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
	
	// drawing objects
	
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
    	pGraphSolid1.setStrokeWidth(getResources().getDimension(R.dimen.graph_stroke_width));
    	pGraphSolid1.setStyle(Paint.Style.FILL);
    	pGraphSolid1.setColor(getResources().getColor(R.color.graph_solid1));
        
        pGraphSolid2 = new Paint(pGraphSolid1);
        pGraphSolid2 .setColor(getResources().getColor(R.color.graph_solid2));
        
        pGraphStroke = new Paint(pGraphSolid1);
        pGraphStroke.setStyle(Paint.Style.STROKE);
        pGraphStroke.setColor(getResources().getColor(R.color.graph_stroke));

        pAnswer = new Paint(pGraphStroke);
    	pAnswer.setStrokeWidth(getResources().getDimension(R.dimen.answer_stroke_width));
		pAnswer.setColor(getResources().getColor(R.color.graph_answer));

		pRuler = new Paint(pGraphStroke);
    	pRuler.setStrokeWidth(getResources().getDimension(R.dimen.ruler_stroke_width));
		pRuler.setColor(getResources().getColor(R.color.graph_ruler));
		TypedValue typedValue = new TypedValue();
		getResources().getValue(R.dimen.ruler_percent_5, typedValue, true);
		this.rulerPercent5 = typedValue.getFloat(); 
		getResources().getValue(R.dimen.ruler_percent_10, typedValue, true);
		this.rulerPercent10 = typedValue.getFloat(); 
		
		crosshairs = getResources().getDrawable(R.drawable.crosshairs);
		crosshairRadius = getResources().getDimension(R.dimen.crosshair_radius);
		
		pAnswerText = new Paint();
		pAnswerText.setColor(getResources().getColor(R.color.graph_formula_text));
		pAnswerText.setTextSize(getResources().getDimension(R.dimen.formula_text_size));
		answerTextOffsetY = getResources().getDimension(R.dimen.formula_text_offset_y);
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
		public void setResult(Distribution distribution, int target, String answerText);
	}
	
	private class SetGraph implements Setter {
		private final int i;
		public SetGraph(int i) { this.i = i; }
		public void setResult(Distribution d, int target, String answerText) {
			in.dist[i] = d;
			in.target[i] = target;
			in.answerText[i] = answerText;
			++in.serial;
			startCalculation();
		}
	}
	
	public Setter getSetter1() { return new SetGraph(0); }
	
	public Setter getSetter2() { return new SetGraph(1); }
	
	public void setVerbose(boolean verbose) {
		if (this.verbose != verbose) {
			this.verbose = verbose;
			invalidate();
		}
	}
	
	public boolean getVerbose() { return verbose; }
	
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
			new CalculatePoints().execute(new CalculateIn(in));
		}
	}
	
	private class CalculatePoints extends AsyncTask<CalculateIn, Void, CalculateOut> {

		/**
		 * This thread is run in the background so it
		 * shouldn't access any other objects in this class or call android gui methods.
		 */
		@Override
		protected CalculateOut doInBackground(CalculateIn... arg0) {
			CalculateIn cin = arg0[0];
			CalculateOut cout = new CalculateOut();
			cout.serial = cin.serial;

			// don't display anything if both graphs are trivial
			if (cin.dist[0].size() <= 1 && cin.dist[1].size() <= 1) {
				return cout;
			}
			
			// draw the larger distribution first
			Distribution[] dist = new Distribution[2];
			if (greaterCumulative(cin.dist[0], cin.dist[1])) {
				dist[0] = cin.dist[0];
				cout.target[0] = cin.target[0];
				cout.paint[0] = pGraphSolid1;
				cout.answerText[0] = cin.answerText[0];
				dist[1] = cin.dist[1];
				cout.target[1] = cin.target[1];
				cout.paint[1] = pGraphSolid2;
				cout.answerText[1] = cin.answerText[1];
			} else {
				dist[0] = cin.dist[1];
				cout.target[0] = cin.target[1];
				cout.paint[0] = pGraphSolid2;
				cout.answerText[0] = cin.answerText[1];
				dist[1] = cin.dist[0];
				cout.target[1] = cin.target[0];
				cout.paint[1] = pGraphSolid1;
				cout.answerText[1] = cin.answerText[0];
			}
			
			final int largestSize = Math.max(dist[0].size(), dist[1].size());
			
			 // add a few extra values after the distribution peaks; multiples of 10
			final int maxX = (largestSize+10) - (largestSize % 10);
			cout.ticks = maxX;
			Point[] pt = new Point[maxX];
			
			for (int j=0 ; j<2 ; ++j) {
				
				// don't display a trivial graph
				if (dist[j].size() <= 1) {
					cout.path[j] = new Path();
					cout.answer[j] = new Point(0.0f, cin.height);
					continue;
				}
				
				for (int i=0 ; i<maxX ; ++i) {
					float x = (float)i/maxX;
					
					// TODO: it would be more efficient to compute all the cumulative
					// distributions at once
					float y = BigFraction.ONE.subtract(dist[j].getCumulativeProbability(i)).floatValue();
					
					pt[i] = new Point(x*cin.width, y*cin.height); // scale to global coords
				}
		
				Interpolate interpolate = new Interpolate(pt);
				cout.path[j] = interpolate.getPath();
				
				// connect the path to origin and starting point
				// move off the screen to the bottom and left a bit so we don't see the stroke
				float leftWall = -cin.width/5; 
				float bottomWall = cin.height * 1.2f; 
				cout.path[j].lineTo(pt[pt.length-1].getX(), bottomWall);
				cout.path[j].lineTo(0.0f, cin.height);
				cout.path[j].lineTo(leftWall, bottomWall);
				cout.path[j].lineTo(leftWall, 0.0f);
				cout.path[j].lineTo(pt[0].getX(),pt[0].getY());
				
				cout.answer[j] = new Point(
						(float)cout.target[j]/cout.ticks * cin.width,
						(1.0f - dist[j].getCumulativeProbability(cout.target[j]).floatValue()) * cin.height);
			}

			return cout;
		}
		
		@Override
		protected void onPostExecute(CalculateOut cout) {
			running = false;
			if (cout.serial == in.serial) {
				out = cout;
				invalidate();
			} else {
				// something has changed since the last time; restart calculation
				startCalculation();
			}
		}
	}

	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawPaint(pBackground);
		interpolatedSolid(canvas);
	}

	private void interpolatedSolid(Canvas canvas) {

		// don't display anything if the background calculation has not occured once
		if (out.path[0] == null) {
			return;
		}
		
		final int h = canvas.getHeight();
		final int w = canvas.getWidth();

		// draw each graph solid and outline
		for (int j=0 ; j<2 ; ++j) {
			canvas.drawPath(out.path[j], out.paint[j]);
			canvas.drawPath(out.path[j], pGraphStroke);
		}

		// draw answer line
		for (int j=0 ; j<2 ; ++j) {
			final float y = out.answer[j].getY();
			canvas.drawLine(0.0f, y, (float)w, y, pAnswer);
		}
		
		// anything after this line only appears in verbose mode
		// (when the buttons are hidden)
		if (!verbose) {
			return;
		}

		// add some tick marks to the 5 and 10 x spots
		{
			final float y10 = h-(float)h*rulerPercent10;
			final float y5 = h-(float)h*rulerPercent5;
			for (int i=0 ; i<=out.ticks ; ++i) {
				if (i % 10 == 0) {
					final float x = (float)i/out.ticks * w;
					canvas.drawLine(x, h, x, y10, pRuler);
				} else if (i % 5 == 0) {
					final float x = (float)i/out.ticks * w;
					canvas.drawLine(x, h, x, y5, pRuler);
				}
			}
		}

		// display the crosshairs on the target spot
		for (int j=0 ; j<2 ; ++j) {
			final float x = out.answer[j].getX();
			final float y = out.answer[j].getY();
			crosshairs.setBounds(
					(int)(x-crosshairRadius),
					(int)(y-crosshairRadius),
					(int)(x+crosshairRadius),
					(int)(y+crosshairRadius));
			crosshairs.draw(canvas);
		}

		// display the answer text
		for (int j=0 ; j<2 ; ++j) {
			canvas.drawText(out.answerText[j], 0.0f, out.answer[j].getY()+answerTextOffsetY, pAnswerText);
		}
		
		/*
		// draw bounds X
        p.setColor(Color.YELLOW);
        // box
        canvas.drawLine(0, 0, w-1, 0, p);               
        canvas.drawLine(0, h-1, w-1, h-1, p);           
        canvas.drawLine(0, 0, 0, h-1, p);               
        canvas.drawLine(w-1, 0, w-1, h-1, p);
        // X
        canvas.drawLine(0, 0, w-1, h-1, p);               
        canvas.drawLine(0, h-1, w-1, 0, p);               
		*/
	}
	
	/**
	 * Returns true if d1>d2
	 */
	private static boolean greaterCumulative(Distribution d1, Distribution d2) {
		// assumes cumulative distributions are strictly decreasing
		final int n = Math.max(d1.size(),d2.size())+1;
		for (int i=0 ; i<n ; ++i) {
			// TODO this looks O(n*m) due to the slow implementation of getCumulativeProbability()
			if (d1.getCumulativeProbability(i).compareTo(d2.getCumulativeProbability(i)) == 1) {
				return true;
			}
		}
		return false;
	}
}
