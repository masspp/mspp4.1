package ninja.mspp.core.model.view;

import ninja.mspp.core.model.ms.Point;

public class Bounds {
	private int top;
	private int right;
	private int bottom;
	private int left;
	
	public Bounds(int top, int right, int bottom, int left) {
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}
	
	public int getTop() {
		return top;
	}
	
	public int getRight() {
		return right;
	}
	
	public int getBottom() {
		return bottom;
	}
	
	public int getLeft() {
		return left;
	}
	
	public int getWidth() {
		return (this.right - this.left);
	}
	
	public int getHeight() {
		return (this.bottom - this.top);
	}
	
	@Override
	public String toString() {
		return "Bounds (top:" + this.top + ", right:" + this.right + ", bottom:" + this.bottom + ", left:" + this.left + ")";
	}

	public boolean isInBound(Point point) {
		boolean isInBound = (point.getX() >= this.left) && (point.getX() <= this.right) && (point.getY() >= this.top) && (point.getY() <= this.bottom);
		return isInBound;
	}
}
