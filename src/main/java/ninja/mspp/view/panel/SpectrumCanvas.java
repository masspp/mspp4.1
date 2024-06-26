package ninja.mspp.view.panel;

import javafx.scene.paint.Color;
import ninja.mspp.core.model.ms.DataPoints;
import ninja.mspp.core.model.ms.Spectrum;

public class SpectrumCanvas extends ProfileCanvas {
	protected static final Color SPECTRUM_COLOR = Color.RED;
	
	protected Spectrum spectrum;
	
	public SpectrumCanvas() {
		super();
		this.setProfileColor(SPECTRUM_COLOR);
		this.spectrum = null;
	}
	
	public void setSpectrum(Spectrum spectrum) {
		this.setImpulseMode(spectrum.isCentroidMode());
		DataPoints points = spectrum.readDataPoints();
		this.spectrum = spectrum;
		this.setPoints(points);
	}
}

