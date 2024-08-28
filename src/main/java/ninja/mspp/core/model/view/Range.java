package ninja.mspp.core.model.view;

public class Range {
	private double start;
	private double end;
	
	public Range(double start, double end) {
		this.start = start;
		this.end = end;
	}
	
	public double getStart() {
		return start;
	}
	
	public double getEnd() {
		return end;
	}
	
	public double getLength() {
		return (this.end - this.start);
	}
	
	public boolean contains(double value) {
		return (this.start <= value && value <= this.end);
	}
	
	public double getCenter() {
		return (this.start + this.end) / 2.0;
	}
	
	public String toString() {
		return String.format("[%f, %f]", this.start, this.end);
	}
}
