package ninja.mspp.view.mode.normal;

import ninja.mspp.view.panel.ChromatogramCanvas;
import ninja.mspp.view.panel.SpectrumCanvas;

public class NormalViewModeManager {
	private static NormalViewModeManager instance;
	
	private SpectrumCanvas spectrumCanvas;
	private ChromatogramCanvas chromatogramCanvas;
	
	private NormalViewModeManager() {
		this.spectrumCanvas = null;
		this.chromatogramCanvas = null;
	}
	
	public SpectrumCanvas getSpectrumCanvas() {
		return spectrumCanvas;
	}
	
	public void setSpectrumCanvas(SpectrumCanvas spectrumCanvas) {
		this.spectrumCanvas = spectrumCanvas;
	}
	
	public ChromatogramCanvas getChromatogramCanvas() {
		return chromatogramCanvas;
	}
	
	public void setChromatogramCanvas(ChromatogramCanvas chromatogramCanvas) {
		this.chromatogramCanvas = chromatogramCanvas;
	}
	
	public static NormalViewModeManager getInstance() {
		if (instance == null) {
			instance = new NormalViewModeManager();
		}
		return instance;
	}
}
