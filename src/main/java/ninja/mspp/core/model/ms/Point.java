package ninja.mspp.core.model.ms;

public class Point implements Comparable<Point> {
	private double x;
	private double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	@Override
	public String toString() {
		return "Point (" + this.x + ", " + this.y + ")";
		
	}

	@Override
	public int compareTo(Point pt) {
        if(this.x < pt.x) {
            return -1;
        }
        else if(this.x > pt.x) {
            return 1;
        }
        return 0;
	}
}
