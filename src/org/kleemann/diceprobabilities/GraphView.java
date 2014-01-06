package org.kleemann.diceprobabilities;

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
		final int h = canvas.getHeight();
		final int w = canvas.getWidth();
		
		Paint p = new Paint();
		p.setARGB(255, 0, 0, 255); // blue; full alpha		
		canvas.drawPaint(p);
		
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
		                        1.0f, 1.0f
		};
		
		// for now construct a path that moves through mid-points
		Path pa = new Path();
		p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5f);
        p.setColor(Color.WHITE);
		pa.moveTo(0, 0);
		pa.lineTo(w,h);
		canvas.drawPath(pa, p);
	}

	
}
