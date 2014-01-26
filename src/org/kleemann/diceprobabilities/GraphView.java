package org.kleemann.diceprobabilities;

import org.apache.commons.math3.fraction.BigFraction;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {

	private Distribution distribution1 = new ZeroDistribution();
	private int target1 = 0;
	
	private Distribution distribution2 = new ZeroDistribution();
	private int target2 = 0;
	
	public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GraphView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public static interface Setter {
		public void setResult(Distribution distribution, int target);
	}
	
	private class SetGraph1 implements Setter {
		public void setResult(Distribution d, int target) {
			distribution1 = d;
			target1 = target;
			invalidate();
		}
	}
	
	public Setter getSetter1() { return new SetGraph1(); }
	
	private class SetGraph2 implements Setter {
		public void setResult(Distribution d, int target) {
			distribution2 = d;
			target2 = target;
			invalidate();
		}
	}
	
	public Setter getSetter2() { return new SetGraph2(); }
	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		blueBackground(canvas);
		interpolatedSolid(canvas);
	}

	private void blueBackground(Canvas canvas) {
		Paint p = new Paint();
		p.setARGB(255, 0, 0, 64); // dark blue; full alpha		
		canvas.drawPaint(p);		
	}
	
	private void interpolatedSolid(Canvas canvas) {

		final int h = canvas.getHeight();
		final int w = canvas.getWidth();

		// draw the larger distribution first
		Distribution[] dist = new Distribution[2];
		int[] target = new int[2];
		int[] fill = new int[2];
		if (greaterCumulative(distribution1,distribution2)) {
			dist[0] = distribution1;
			target[0] = target1;
			fill[0] = Color.RED;
			dist[1] = distribution2;
			target[1] = target2;
		} else {
			dist[0] = distribution2;
			target[0] = target2;
			fill[0] = Color.YELLOW;
			dist[1] = distribution1;
			target[1] = target1;
			fill[1] = Color.RED;
		}
		
		final int largestSize = Math.max(dist[0].size(), dist[1].size());
		
		 // add a few extra values after the distribution peaks; multiples of 10
		final int maxX = (largestSize+10) - (largestSize % 10);
		Point[] pt = new Point[maxX];
		
		Paint p = new Paint();
        p.setStrokeWidth(5f);
		
		for (int j=0 ; j<2 ; ++j) {
			for (int i=0 ; i<maxX ; ++i) {
				float x = (float)i/maxX;
				
				// TODO: it would be more efficient to compute all the cumulative
				// distributions at once
				float y = BigFraction.ONE.subtract(dist[j].getCumulativeProbability(i)).floatValue();
				
				// scale unit coords to world coords
				x *= w;
				y *= h;
				
				pt[i] = new Point(x,y);
			}
	
			Interpolate interpolate = new Interpolate(pt);
			Path path = interpolate.getPath();
			
			// connect the path to origin and starting point
			path.lineTo(pt[pt.length-1].getX(), h);
			path.lineTo(0.0f, h);
			path.lineTo(0.0f, 0.0f);
			path.lineTo(pt[0].getX(),pt[0].getY());
			
			p.setStyle(Paint.Style.FILL);
	        p.setColor(fill[j]);
			canvas.drawPath(path, p);
			p.setStyle(Paint.Style.STROKE);
	        p.setColor(Color.WHITE);
			canvas.drawPath(path, p);
			
			// draw target line
			p.setColor(Color.BLACK);
			// TODO: need to scale this
			final float targetF = ((float)target[j]/maxX) * w;
			canvas.drawLine(targetF, 0.0f, targetF, (float)h, p);
		}
		
		// add some tick marks to the 5 and 10 x spots
		//p.setStrokeWidth(3f);
		p.setColor(Color.GRAY);
		for (int i=0 ; i<=maxX ; ++i) {
			if (i % 10 == 0) {
				final float x = (float)i/maxX * w;
				canvas.drawLine(x, h, x, h-(float)h/10, p);
			} else if (i % 5 == 0) {
				final float x = (float)i/maxX * w;
				canvas.drawLine(x, h, x, h-(float)h/20, p);				
			}
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
