package org.kleemann.diceprobabilities;

public class Point {

	private final float x;
	private final float y;
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getX() { return x; }
	
	public float getY() { return y; }
	
	public Point add(Point that) {
		return new Point(x+that.x, y+that.y);
	}
	
	public Point sub(Point that) {
		return new Point(x-that.x, y-that.y);
	}
	
	public Point mid(Point that) {
		return new Point( (x+that.x)/2.0f, (y+that.y)/2.0f );
	}
	
	@Override
	public String toString() { return "("+x+","+"y"+")"; }
}

