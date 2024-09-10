package ninja.mspp.core.model.view;

public class Rect {
	private double startX;
	private double startY;
	private double endX;
	private double endY;
	
	public Rect(double startX, double startY, double endX, double endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}

	public double getStartX() {
		return startX;
	}

	public void setStartX(double startX) {
		this.startX = startX;
	}

	public double getStartY() {
		return startY;
	}

	public void setStartY(double startY) {
		this.startY = startY;
	}

	public double getEndX() {
		return endX;
	}

	public void setEndX(double endX) {
		this.endX = endX;
	}

	public double getEndY() {
		return endY;
	}

	public void setEndY(double endY) {
		this.endY = endY;
	}
	
	public double getWidth() {
		return (this.endX - this.startX);
	}
	
	public double getHeight() {
		return (this.endY - this.startY);
	}
	
	public double getCenterX() {
		return ((this.startX + this.endX) / 2.0);
	}
	
	public double getCenterY() {
		return ((this.startY + this.endY)/ 2.0);
	}
}
