package flash.geom;


public class Point {

	public int length;

	public int x;

	public int y;

	public Point() {
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
		this.length = (int)Math.sqrt(x * x + y * y);
	}

	@Override
	public String toString() {
		return "Point [x=" + x + ", y=" + y + "]";
	}
	
}
