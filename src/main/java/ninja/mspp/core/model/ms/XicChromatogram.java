package ninja.mspp.core.model.ms;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XicChromatogram extends Chromatogram {
    private double tolerance;
    private List<Spectrum> spectra;
    private Map<Spectrum, Double> intensities;
    
    public XicChromatogram(Sample sample, int number, double mz, double tol) {
    	super(
    		sample,
    		number,
    		String.format("XIC [mz=%.4f]", mz),
    		mz
    	);
    	
    	this.spectra = sample.getSpectra();
    	this.tolerance = tol;
    	this.intensities = new HashMap<Spectrum, Double>();
    }

	@Override
	protected DataPoints onReadDataPoints() {
		DataPoints points = new DataPoints();
		
		double minX = this.getMz() - this.tolerance;
		if(minX < 0.0) {
			minX = 0.0;
		}
		double maxX = this.getMz() + this.tolerance;
		
		for(Spectrum spectrum : this.spectra) {
			if(spectrum.getMsLevel() == 1) {
				double x = spectrum.getRt();
				double y = 0.0;
				if(this.intensities.containsKey(spectrum)) {
					y = this.intensities.get(spectrum);
				}
				else {
					DataPoints spectrumPoints = spectrum.readDataPoints();
					y = spectrumPoints.calculateTotal(minX, maxX);
					this.intensities.put(spectrum, y);					
				}
				points.add(new Point(x, y));
			}
		}
		
		return points;
	}
}
