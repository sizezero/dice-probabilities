package org.kleemann.diceprobabilities.graph;

import java.util.Arrays;

import android.graphics.Path;

/**
 * <p>Given a set of points, interpolates the control points between them creating a smooth path.
 * 
 * <p>This is an immutable object
 */
class Interpolate {

	private static final float SCALE = 0.3f;
	
	private final Point[] p;
	
	// each point has two control points. These are the two ends of the tangent
	// that passes through the point.
	//
	// the first control point of the first point is the same as the first point
	// the second control point of the last point is the same as the last point
	private final Point[] c;
	
	public Interpolate(Point[] pts) {
		// defensive copy
		this.p = Arrays.copyOf(pts, pts.length);
		
		c = new Point[p.length*2];
		
		if (pts.length == 1) {
			c[0] = p[0];
			c[1] = p[0];
			return;
		} else if (p.length == 2) {
			final Point mid = p[0].mid(p[1]);
			
			c[0] = p[0];
			c[1] = mid;

			c[2] = mid;
			c[3] = p[1];
			return;
		}
		
		// at least 3 points
		
		// first point
		c[0] = p[0];
		c[1] = p[0].mid(p[1]);
		
		for (int i=1 ; i<p.length-1 ; ++i) {
			final Point p0 = p[i-1];
			final Point p1 = p[i];
			final Point p2 = p[i+1];
			
			final Vector tangent0 = new Vector(p2,p0).normalize();
			//Log.v("TAG", "tangent0 "+tangent0);
			final Vector v0 = tangent0.scale(SCALE * new Vector(p1,p0).magnitude());
			//Log.v("TAG", "v0 "+v0);
			c[i*2] = v0.add(p1);
			
			final Vector tangent1 = new Vector(p0,p2).normalize();
			//Log.v("TAG", "tangent1 "+tangent1);
			final Vector v1 = tangent1.scale(SCALE * new Vector(p1,p2).magnitude());
			//Log.v("TAG", "v1 "+v1);
			c[i*2+1] = v1.add(p1);
		}
		
		// last point
		c[c.length-2] = p[p.length-1].mid(p[p.length-2]);
		c[c.length-1] = p[p.length-1];
	}

	/**
	 * Returns the first control point of the specified segment point (towards the previous point)
	 * @param i
	 * @return
	 */
	public Point getC1(int i) { return c[i*2]; }
	
	/**
	 * Returns the second control point of the specified segment point (towards the next point)
	 * @param i
	 * @return
	 */
	public Point getC2(int i) { return c[i*2+1]; }
	
	public Path getPath() {
		Path path = new Path();
		path.moveTo(p[0].getX(), p[0].getY());
		for (int i=0 ; i<p.length-1 ; ++i) {
			//final Point p0 = p[i];
			final Point p1 = p[i+1];
			final Point c0 = c[i*2+1]; // second control point of first segment point
			final Point c1 = c[i*2+2]; // first control point of second segment point
			//Log.v("TAG", "p0"+p0+" c0"+c0+" c1"+c1+" p1"+p1);
			//path.moveTo(p0.getX(), p0.getY());
			path.cubicTo(c0.getX(), c0.getY(), c1.getX(), c1.getY(), p1.getX(), p1.getY());
		}
		return path;
	}
}
