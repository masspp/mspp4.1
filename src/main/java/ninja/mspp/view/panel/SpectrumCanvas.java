package ninja.mspp.view.panel;

import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ninja.mspp.MsppManager;
import ninja.mspp.core.annotation.method.SpectrumCanvasBackground;
import ninja.mspp.core.annotation.method.SpectrumCanvasForeground;
import ninja.mspp.core.model.ms.DataPoints;
import ninja.mspp.core.model.ms.Spectrum;
import ninja.mspp.core.view.DrawInfo;
import ninja.mspp.view.panel.model.Bounds;
import ninja.mspp.view.panel.model.Range;

public class SpectrumCanvas extends ProfileCanvas {
	protected static final Color SPECTRUM_COLOR = Color.RED;
	
	protected Spectrum spectrum;
	
	public SpectrumCanvas() {
		super();
		this.setProfileColor(SPECTRUM_COLOR);
		this.spectrum = null;
	}
	
	@Override
	protected void drawForeground(GraphicsContext gc, double width, double height, Bounds margin, RealMatrix matrix, Range xRange, Range yRange) {
		MsppManager manager = MsppManager.getInstance();
		
		DrawInfo<Spectrum> drawInfo = new DrawInfo<Spectrum>(
			this.spectrum, width, height, margin, this.points, matrix, xRange, yRange, gc
		);
		
		manager.invoke(SpectrumCanvasForeground.class, drawInfo);
	}

	
	@Override
	protected void drawBackground(GraphicsContext gc, double width, double height, Bounds margin, RealMatrix matrix, Range xRange, Range yRange) {
		MsppManager manager = MsppManager.getInstance();
		
		DrawInfo<Spectrum> drawInfo = new DrawInfo<Spectrum>(
			this.spectrum, width, height, margin, this.points, matrix, xRange, yRange, gc
		);
		
		manager.invoke(SpectrumCanvasBackground.class, drawInfo);		
	}
	
	public void setSpectrum(Spectrum spectrum) {
		if(spectrum != null) {
			this.setImpulseMode(spectrum.isCentroidMode());
			DataPoints points = spectrum.readDataPoints();
			this.spectrum = spectrum;
			this.setPoints(points);
		}
	}
}

