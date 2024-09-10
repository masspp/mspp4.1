package ninja.mspp.core.model.ms;

import java.util.List;

public class TicChromatogram extends Chromatogram {
	private List<Spectrum> spectra;
	
	public TicChromatogram(Sample sample) {
		super(sample, 0, "TIC", -1.0);
		this.spectra = sample.getSpectra();
	}

	@Override
	protected DataPoints onReadDataPoints() {
		DataPoints points = new DataPoints();
		
		for(Spectrum spectrum : this.spectra) {
			if(spectrum.getMsLevel() == 1) {
				double x = spectrum.getRt();
				double y = spectrum.getTic();
				
				Point point = new Point(x, y);
				points.add(point);
			}
		}
		return points;
	}
}
