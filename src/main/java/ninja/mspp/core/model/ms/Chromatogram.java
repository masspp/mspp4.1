package ninja.mspp.core.model.ms;

public abstract class Chromatogram {
	private Sample sample;
	private int chromatogramNumber;
	private String name;
	private Double mz;
	
	public Chromatogram(Sample sample, int chromatogramNumber, String name, Double mz) {
		this.sample = sample;
		this.chromatogramNumber = chromatogramNumber;
		this.name = name;
		this.mz = mz;
	}
	
	public Sample getSample() {
		return this.sample;
	}
	
	public int getChromatogramNumber() {
		return this.chromatogramNumber;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Double getMz() {
		return this.mz;
	}

	public DataPoints readDataPoints() {
		return onReadDataPoints();
	}
	
	protected abstract DataPoints onReadDataPoints();
}
