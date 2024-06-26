package ninja.mspp.core.model.ms;

import java.util.ArrayList;
import java.util.List;

public class Sample {
	private String id;
	private String name;
	private List<Spectrum> spectra;
	private List<Chromatogram> chromatograms;
	
	public Sample(String id, String name) {
		this.id = id;
		this.name = name;
		
		this.spectra = new ArrayList<Spectrum>();
		this.chromatograms = new ArrayList<Chromatogram>();
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<Spectrum> getSpectra() {
		return this.spectra;
	}
	
	public List<Chromatogram> getChromatograms() {
		return this.chromatograms;
	}
}
