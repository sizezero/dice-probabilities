package org.kleemann.diceprobabilities;

import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {

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

	/* (non-Javadoc)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		//bounds(canvas);
		//curvy(canvas);
		curvy2(canvas);
	}

	private void blueBackground(Canvas canvas) {
		Paint p = new Paint();
		p.setARGB(255, 0, 0, 255); // blue; full alpha		
		canvas.drawPaint(p);		
	}
	
	private void curvy(Canvas canvas) {
		final int h = canvas.getHeight();
		final int w = canvas.getWidth();
		
		// break the drawable image into 0-1, 0-1
		final float[] points = {
		                        0.1f, 0.0f,
		                        0.2f, 0.0f,
		                        0.3f, 0.1f,
		                        0.4f, 0.25f,
		                        0.5f, 0.5f,
		                        0.6f, 0.75f,
		                        0.7f, 0.9f,
		                        0.8f, 1.0f,
		                        0.9f, 1.0f,
		                        0.99f, 0.99f
		};
		//final float[] points = { 0.1f,0.2f, 0.5f,0.2f, 0.7f,0.8f };
		
		/*
		float[] mid = new float[points.length-2];
		for (int i=0 ; i<mid.length ; ++i) {
			mid[i] = (points[i]+points[i+2]) / 2.0f;
		}
		
		// for now construct a path that moves through mid-points
		Path pa = new Path();
        
		pa.moveTo(points[0]*w, points[1]*h);
		for (int i=0 ; i<mid.length ; i += 2) {
			pa.quadTo(points[i]*w,points[i+1]*h, mid[i]*w,mid[i+1]*h);
		}
		//pa.lineTo(w,h);
        */

		blueBackground(canvas);
		
		// scale points into a duplicated array
		float[] s = new float[points.length];
		for (int i=0 ; i<points.length ; i+=2) {
			s[i] = points[i] * w;
			s[i+1] = points[i+1] * h;
		}
		
		Path path = BestFit.getPath(s);
		
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5f);
        p.setColor(Color.WHITE);
		canvas.drawPath(path, p);		
	}

	private void curvy2(Canvas canvas) {
		final int h = canvas.getHeight();
		final int w = canvas.getWidth();
		
		// break the drawable image into 0-1, 0-1
		Point[] points = {
		                        new Point(0.1f, 0.0f),
		                        new Point(0.2f, 0.0f),
		                        new Point(0.3f, 0.1f),
		                        new Point(0.4f, 0.25f),
		                        new Point(0.5f, 0.5f),
		                        new Point(0.6f, 0.75f),
		                        new Point(0.7f, 0.9f),
		                        new Point(0.8f, 1.0f),
		                        new Point(0.9f, 1.0f),
		                        new Point(0.99f, 0.99f)
		};
		
		blueBackground(canvas);
		
		for (int i=0 ; i<points.length ; ++i) {
			points[i] = new Point(points[i].getX()*w, points[i].getY()*h);
		}
		Interpolate interpolate = new Interpolate(points);
		Path path = interpolate.getPath();
		
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5f);
        p.setColor(Color.WHITE);
		canvas.drawPath(path, p);
	}

	private void bounds(Canvas canvas) {
		final int h = canvas.getHeight();
		final int w = canvas.getWidth();
		
		Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(0f);

        // blue background
        blueBackground(canvas);
        
        // draw a bounding box in white
        p.setColor(Color.RED);
		canvas.drawLine(0, 0, w-1, 0, p);		
		canvas.drawLine(0, h-1, w-1, h-1, p);		
		canvas.drawLine(0, 0, 0, h-1, p);		
		canvas.drawLine(w-1, 0, w-1, h-1, p);
		
		// The above bounding box lacks the bottom horz line in the emulator
		// on an actual nexus 5 it lacks the top horz line and the right vert line
		
		// diagonal
		p.setColor(Color.YELLOW);
		canvas.drawLine(0,0,w-1,h-1,p);
		canvas.drawLine(0,h-1,w-1,0,p);
		
		// emulator appears to be truncated to some degree (~10%)
		// due to small screen resolution
	}
}
