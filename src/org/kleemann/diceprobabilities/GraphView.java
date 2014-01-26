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

	private Distribution distribution = null;
	private int target = 0;
	
	public GraphView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GraphView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sets a new interpolated value
	 */
	public void setResult(Distribution distribution, int target) {
		this.distribution = distribution;
		this.target = target;
		invalidate();
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		blueBackground(canvas);
		interpolatedSolid(canvas, Color.RED);
	}

	private void blueBackground(Canvas canvas) {
		Paint p = new Paint();
		p.setARGB(255, 0, 0, 255); // blue; full alpha		
		canvas.drawPaint(p);		
	}
	
	private void interpolatedSolid(Canvas canvas, int color) {
		
		if (distribution==null) {
			return;
		}
		
		final int h = canvas.getHeight();
		final int w = canvas.getWidth();
		
		 // add a few extra values after the distribution peaks; multiples of 10
		final int maxX = (distribution.size()+10) - (distribution.size() % 10);  
		Point[] pt = new Point[maxX];
		for (int i=0 ; i<maxX ; ++i) {
			
			float x = (float)i/maxX;
			// TODO: it would be more efficient to compute all the cumulative
			// distributions at once
			float y = BigFraction.ONE.subtract(distribution.getCumulativeProbability(i)).floatValue();
			
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
		
		Paint p = new Paint();
		
		p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth(5f);
        p.setColor(color);
		canvas.drawPath(path, p);
		p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE);
		canvas.drawPath(path, p);
		
		// draw target line
		p.setColor(Color.BLACK);
		// TODO: need to scale this
		final float targetF = ((float)target/maxX) * w;
		canvas.drawLine(targetF, 0.0f, targetF, (float)h, p);

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
}
