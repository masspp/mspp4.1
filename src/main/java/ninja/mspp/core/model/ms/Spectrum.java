package ninja.mspp.core.model.ms;

public abstract class Spectrum {
	public enum Polarity {
		POSITIVE,
		NEGATIVE,
		UNKNOWN
	}

	private Sample sample;
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
	private double tic;
	
	
	public Spectrum(Sample sample, String name, String title, int scanNumber, double rt, int msLevel, Polarity polarity,
			double precursor, double minMz, double maxMz, boolean centroidMode) {
		this.sample = sample;
		this.name = name;
		this.title = title;
		this.scanNumber = scanNumber;
		this.rt = rt;
		this.msLevel = msLevel;
		this.polarity = polarity;
		this.precursor = precursor;
		this.minMz = minMz;
		this.maxMz = maxMz;
		this.centroidMode = centroidMode;
		this.tic = Double.NaN;
	}
	
	public Sample getSample() {
		return this.sample;
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
		DataPoints points = this.onReadDataPoints();
		if(Double.isNaN(this.tic)) {
			double tic = 0.0;
			for(Point point : points) {
				tic += point.getY();
			}
			this.tic = tic;
		}
		return points;
	}
	
	public double getTic() {
		return tic;
	}

	public void setTic(double tic) {
		if(Double.isNaN(this.tic)) {
			this.readDataPoints();
		}
		this.tic = tic;
	}

	protected abstract DataPoints onReadDataPoints();
}
