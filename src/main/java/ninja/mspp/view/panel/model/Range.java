package ninja.mspp.view.panel.model;

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
}
