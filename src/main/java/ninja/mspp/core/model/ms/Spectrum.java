package ninja.mspp.core.model.ms;

import java.util.ArrayList;
import java.util.List;

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
	private double precursorMass;
	private double minMz;
	private double maxMz;
	private boolean centroidMode;
	private double tic;
	
	private Spectrum precursor;
	private List<Spectrum> products;
	
	
	public Spectrum(Sample sample, String name, String title, int scanNumber, double rt, int msLevel, Polarity polarity,
			double precursorMass, double minMz, double maxMz, boolean centroidMode) {
		this.sample = sample;
		this.name = name;
		this.title = title;
		this.scanNumber = scanNumber;
		this.rt = rt;
		this.msLevel = msLevel;
		this.polarity = polarity;
		this.precursorMass = precursorMass;
		this.minMz = minMz;
		this.maxMz = maxMz;
		this.centroidMode = centroidMode;
		this.tic = Double.NaN;
		
		this.precursor = null;
		this.products = null;
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
	
	public double getPrecursorMass() {
		return this.precursorMass;
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
	
	public void setPrecursor(Spectrum precursor) {
		this.precursor = precursor;
	}
	
	public Spectrum getPrecursor() {
		return this.precursor;
	}
	
	public void addProduct(Spectrum product) {
		if(this.products == null) {
			this.products = new ArrayList<Spectrum>();
		}
		this.products.add(product);
	}
	
	public List<Spectrum> getProducts() {
		return this.products;
	}
	
	@Override
	public String toString() {
		String string = String.format(
			"Scan %d (rt = %.2f",
			this.scanNumber,
			this.rt
		);
		
		if(this.precursorMass > 0.0) {
			string += String.format(", precursor=%.2f",  this.precursorMass);
		}
		string += ")";
		
		return string;
	}

	protected abstract DataPoints onReadDataPoints();
}
