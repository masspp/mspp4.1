package ninja.mspp.core.model.ms;

public abstract class Spectrum {
	public enum Polarity {
		POSITIVE,
		NEGATIVE,
		UNKNOWN
	}
		
	private String name;
	private String title;
	private int scanNumber;
	private double rt;
	private int msLevel;
	private Polarity polarity;
	private double precursor;
	private double minMz;
	private double maxMz;
	private boolean centroidMode;
	
	
	public Spectrum(String name, String title, int scanNumber, double rt, int msLevel, Polarity polarity,
			double precursor, double minMz, double maxMz, boolean centroidMode) {
		this.name = name;
		this.title = title;
		this.scanNumber = scanNumber;
		this.rt = rt;
		this.msLevel = msLevel;
		this.polarity = polarity;
		this.precursor = precursor;
		this.minMz = minMz;
		this.maxMz = maxMz;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public int getScanNumber() {
		return this.scanNumber;
	}
	
	public double getRt() {
		return this.rt;
	}
	
	public int getMsLevel() {
		return this.msLevel;
	}
	
	public Polarity getPolarity() {
		return this.polarity;
	}
	
	public double getPrecursor() {
		return this.precursor;
	}
	
	public double getMinMz() {
		return this.minMz;
	}
	
	public double getMaxMz() {
		return this.maxMz;
	}
	
	public boolean isCentroidMode() {
		return this.centroidMode;
	}
	
	public DataPoints readDataPoints() {
		return onReadDataPoints();
	}
	
	protected abstract DataPoints onReadDataPoints();
}
