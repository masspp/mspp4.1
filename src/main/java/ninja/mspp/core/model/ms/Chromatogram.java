package ninja.mspp.core.model.ms;

public abstract class Chromatogram {
	private Integer chromatogramNumber;
	private String name;
	private Double mz;
	
	public Chromatogram(Integer chromatogramNumber, String name, Double mz) {
		this.chromatogramNumber = chromatogramNumber;
		this.name = name;
		this.mz = mz;
	}
	
	public Integer getChromatogramNumber() {
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
