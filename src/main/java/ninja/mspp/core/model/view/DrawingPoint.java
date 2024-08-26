package ninja.mspp.core.model.view;

public class DrawingPoint implements Comparable<DrawingPoint> {
	private double x;
	private double minY;
	private double maxY;
	private double leftY;
	private double rightY;
	
	public DrawingPoint(double x, double minY, double maxY, double leftY, double rightY) {
		this.x = x;
		this.minY = minY;
		this.maxY = maxY;
		this.leftY = leftY;
		this.rightY = rightY;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getMinY() {
		return minY;
	}

	public void setMinY(double minY) {
		this.minY = minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}

	public double getLeftY() {
		return leftY;
	}

	public void setLeftY(double leftY) {
		this.leftY = leftY;
	}

	public double getRightY() {
		return rightY;
	}

	public void setRightY(double rightY) {
		this.rightY = rightY;
	}

	@Override
	public int compareTo(DrawingPoint o) {
		if (this.x < o.x) {
			return -1;
		}
		else if (this.x > o.x) {
			return 1;
		}
		return 0;
	}
}
