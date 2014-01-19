package org.kleemann.diceprobabilities;

import android.graphics.Path;
import android.util.Log;

/**
 * <p>A simple best fit algorithm to create a curved path through a series of points 
 */
public class BestFit {

	public static Path getPath(float[] points) {
		Path path = new Path();

		// chop the points array into separate x and y arrays
		float[] x = new float[points.length/2]; 
		float[] y = new float[x.length]; 
		for (int i=0 ; i<x.length ; ++i){
			x[i] = points[i*2];
			y[i] = points[i*2+1];
		}
		
		/*computes control points p1 and p2 for x and y direction*/
		//px = computeControlPoints(x);
		//py = computeControlPoints(y);
		
		float[] px1 = new float[x.length];
		float[] px2 = new float[x.length];
		controlPoints(x, px1, px2);
		
		float[] py1 = new float[y.length];
		float[] py2 = new float[y.length];
		controlPoints(y, py1, py2);
		
		/*updates path settings, the browser will draw the new spline*/
		//for (i=0;i<3;i++)
		//	S[i].setAttributeNS(null,"d",
		//		path(x[i],y[i],px.p1[i],py.p1[i],px.p2[i],py.p2[i],x[i+1],y[i+1]));

		for (int i=0 ; i<x.length-1 ; ++i) {
			Log.v("TAG", "("+x[i]+","+y[i]+")" + "("+px1[i]+","+py1[i]+")" + "("+px2[i]+","+py2[i]+")" + "("+x[i+1]+","+y[i+1]+")" );
			path.moveTo(x[i], y[i]);
			path.cubicTo(px1[i], py1[i], px2[i], py2[i], x[i+1], y[i+1]);
		}
		
		return path;
	}
	
	/**
	 * Taken nearly verbatim from a javascript implementation:
	 *
	 * http://www.particleincell.com/blog/2012/bezier-splines/
	 */
	private static void controlPoints(float[] knots, float[] p1, float[] p2) {
		//p1=new Array();
		//p2=new Array();
		//n = K.length-1;
		
		/*rhs vector*/
		//a=new Array();
		//b=new Array();
		//c=new Array();
		//r=new Array();
		float a[] = new float[knots.length];
		float b[] = new float[knots.length];
		float c[] = new float[knots.length];
		float r[] = new float[knots.length];
		
		/*left most segment*/
		//a[0]=0;
		//b[0]=2;
		//c[0]=1;
		//r[0] = K[0]+2*K[1];
		a[0] = 0.0f;
		b[0] = 2.0f;
		c[0] = 1.0f;
		r[0] = knots[0] + 2.0f*knots[1];
		
		/*internal segments*/
		//for (i = 1; i < n - 1; i++)
		//{
		//	a[i]=1;
		//	b[i]=4;
		//	c[i]=1;
		//	r[i] = 4 * K[i] + 2 * K[i+1];
		//}

		for (int i=1 ; i<knots.length-2 ; ++i) {
			a[i] = 1.0f;
			b[i] = 4.0f;
			c[i] = 1.0f;
			r[i] = 4.0f * knots[i] + 2.0f * knots[i+1];
		}
		
		/*right segment*/
		//a[n-1]=2;
		//b[n-1]=7;
		//c[n-1]=0;
		//r[n-1] = 8*K[n-1]+K[n];
		
		a[knots.length-2] = 2.0f;
		b[knots.length-2] = 7.0f;
		c[knots.length-2] = 0.0f;
		r[knots.length-2] = 8.0f * knots[knots.length-2]*knots[knots.length-1];
		
		/*solves Ax=b with the Thomas algorithm (from Wikipedia)*/
		//for (i = 1; i < n; i++)
		//{
		//	m = a[i]/b[i-1];
		//	b[i] = b[i] - m * c[i - 1];
		//	r[i] = r[i] - m*r[i-1];
		//}
	 
		for (int i=1 ; i<knots.length-1 ; ++i) {
			float m = a[i]/b[i-1];
			b[i] = b[i] - m*c[i-1];
			r[i] = r[i] - m*r[i-1];
		}
		
		//p1[n-1] = r[n-1]/b[n-1];
		//for (i = n - 2; i >= 0; --i)
		//	p1[i] = (r[i] - c[i] * p1[i+1]) / b[i];
			
		p1[knots.length-2] = r[knots.length-2] / b[knots.length-2];
		for (int i=knots.length-3 ; i>=0 ; --i) {
			p1[i] = (r[i] - c[i] * p1[i+1]) / b[i];
		}
		
		/*we have p1, now compute p2*/
		//for (i=0;i<n-1;i++)
		//	p2[i]=2*K[i+1]-p1[i+1];
		
		for (int i=0 ; i<knots.length-2 ; ++i) {
			p2[i] = 2.0f * knots[i+1] - p1[i+1];
		}
		
		//p2[n-1]=0.5*(K[n]+p1[n-1]);
		
		p2[knots.length-2] = 0.5f * (knots[knots.length-1] + p1[knots.length-2]);
		
		//return {p1:p1, p2:p2};
	}
}
