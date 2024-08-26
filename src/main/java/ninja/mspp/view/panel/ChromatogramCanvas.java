package ninja.mspp.view.panel;

import javafx.scene.paint.Color;
import ninja.mspp.core.model.ms.Chromatogram;
import ninja.mspp.core.model.ms.DataPoints;

public class ChromatogramCanvas extends ProfileCanvas {
	protected static final Color CHROMATOGRAM_COLOR = Color.BLUE;
	
	protected Chromatogram chromatogram;
	
	public ChromatogramCanvas() {
		super("RT", "Int.");
		this.setProfileColor(CHROMATOGRAM_COLOR);
		this.chromatogram = null;
	}
	
	public void setChromatogram(Chromatogram chromatogram) {
		DataPoints points = chromatogram.readDataPoints();
		this.chromatogram = chromatogram;
		this.setPoints(points);
	}
}
