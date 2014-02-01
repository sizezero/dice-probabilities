package org.kleemann.diceprobabilities.graph;

/**
 * <p>A vector (direction and magnitude) represented by a 
 * Point object. 
 */
class Vector {

	private final Point pt;
	
	/**
	 * Creates a vector from the origin to the specified point
	 * @param pt
	 */
	public Vector(Point pt) {
		this.pt = pt;
	}
	
	/**
	 * Returns the vector that points from the src to the dst point
	 * @param src
	 * @param dst
	 */
	public Vector(Point src, Point dst) {
		this.pt = dst.sub(src);
	}

	public float magnitude() {
		final double w2 = (double)pt.getX()*pt.getX();
		final double h2 = (double)pt.getY()*pt.getY();
		// sqrt only takes double
		return (float)Math.sqrt(w2+h2);
	}
	
	public Vector scale(float f) {
		// TODO: is this mathematically correct?
		final Point n = new Point(pt.getX()*f, pt.getY()*f);
		return new Vector(n);
	}
	
	public Vector normalize() {
		return scale( 1.0f / magnitude() );
	}
	
	public Point add(Point that) {
		return this.pt.add(that);
	}
	
	public Point sub(Point that) {
		return this.pt.sub(that);
	}
	
	@Override
	public String toString() { return pt.toString(); }
}
