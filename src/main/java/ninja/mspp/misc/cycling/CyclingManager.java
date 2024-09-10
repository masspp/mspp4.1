package ninja.mspp.misc.cycling;

import java.io.InputStream;
import java.util.Collections;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import ninja.mspp.MsppManager;
import ninja.mspp.core.annotation.method.Refresh;
import ninja.mspp.core.model.ms.Chromatogram;
import ninja.mspp.core.model.ms.DataPoints;
import ninja.mspp.core.model.ms.Point;
import ninja.mspp.core.model.ms.Spectrum;
import ninja.mspp.core.model.view.Range;
import ninja.mspp.core.view.DrawInfo;
import ninja.mspp.view.panel.ProfileCanvas;

public class CyclingManager {
	private static CyclingManager instance = null;
	
	private static final double IMAGE_SIZE     = 64.0;
	private static final double ANIMATION_TIME = 20.0;
	private static final double ANIMATION_STEP = 0.4;
	
	private Image imageF1;
	private Image imageF2;
	private Image imageM1;
	private Image imageM2;
	
	private long chromatogramStartTime;
	private long chromatogramTime;
	private Chromatogram chromatogram;
	private AnimationTimer chromatogramTimer;
	private ProfileCanvas chromatogramCanvas;
	private int prevChromIndex;
	
	private long spectrumStartTime;
	private long spectrumTime;
	private Spectrum spectrum;
	private AnimationTimer spectrumTimer;
	private ProfileCanvas spectrumCanvas;
	private int prevSpecIndex;
		
	private CyclingManager() {
		loadImages();
	}
	
	private void loadImages() {
		this.imageF1 = this.loadImage("cycling-f1.png");
		this.imageF2 = this.loadImage("cycling-f2.png");
		this.imageM1 = this.loadImage("cycling-m1.png");
		this.imageM2 = this.loadImage("cycling-m2.png");		
	}
	
	private Image loadImage(String file) {
		String path = "/ninja/mspp/images/animation/" + file;
		InputStream stream = this.getClass().getResourceAsStream(path);
		Image image = new Image(stream);
		return image;
	}
	
	public void drawChromatogram(DrawInfo<Chromatogram> info) {
		CyclingManager me = this;
		if(this.chromatogram == info.getObject()) {
			if(this.chromatogramCanvas == null) {
				this.chromatogramCanvas = info.getCanvas();
				this.chromatogramTimer = new AnimationTimer() {
					@Override
					public void handle(long now) {
						if(me.chromatogramStartTime == 0) {
							me.chromatogramStartTime = now;
						}
						me.chromatogramTime = now;
						if(me.chromatogramCanvas != null) {
							me.chromatogramCanvas.refresh();
						}
					}
				};
				this.chromatogramTimer.start();
			}
			else {
				double seconds = (double)(this.chromatogramTime - this.chromatogramStartTime) / 1000000000.0;
				this.drawChromatogram(info, seconds);
			}
		}		
	}
	
	private void drawChromatogram(DrawInfo<Chromatogram> info, double seconds) {
		if (seconds >= ANIMATION_TIME) {
			this.chromatogramCanvas = null;
			this.chromatogramTimer.stop();
			this.chromatogram = null;
			this.chromatogramStartTime = 0;
			this.chromatogramTime = 0;
		}
		else {
			int num = (int)Math.floor(seconds / ANIMATION_STEP);
			Image image = num % 2 == 0 ? this.imageM1 : this.imageM2;
			
			DataPoints points = info.getPoints();
			
			double rate = seconds / ANIMATION_TIME;
			
			Range xRange = info.getXRange();
			double x = xRange.getLength() * rate + xRange.getStart();
			double y = points.calculateInterpolationY(x);
			
			int index = Collections.binarySearch(points, new Point(x, 0));
			if(index < 0) {
				index = - index - 2;
			}
			for(int i = this.prevChromIndex + 1; i <= index && i < points.size(); i++) {
				double currentY = points.get(i).getY();
				if(currentY > y) {
					y = currentY;
				}
			}
			this.prevChromIndex = Math.max(0, index);

			double[] coordinate = {x, y, 1.0};
			double[] position = info.getMatrix().operate(coordinate);
			
			GraphicsContext gc = info.getContext();
			
			gc.drawImage(
				image,
				position[0] - IMAGE_SIZE / 2.0,
				position[1] - IMAGE_SIZE,
				IMAGE_SIZE,
				IMAGE_SIZE
			);
		}
	}	

	public void startChromatogram(Chromatogram chromatogram) {		
		this.chromatogramStartTime = 0;
		this.chromatogram = chromatogram;
		this.chromatogramCanvas = null;
		this.prevChromIndex = 0;
		
		MsppManager manager = MsppManager.getInstance();
		manager.invoke(Refresh.class, null);
	}

	public void drawSpectrum(DrawInfo<Spectrum> info) {
		CyclingManager me = this;
		if(this.spectrum == info.getObject()) {
			if(this.spectrumCanvas == null) {
				this.spectrumCanvas = info.getCanvas();
				this.spectrumTimer = new AnimationTimer() {
					@Override
					public void handle(long now) {
						if(me.spectrumStartTime == 0) {
							me.spectrumStartTime = now;
						}
						me.spectrumTime = now;
						if(me.spectrumCanvas != null) {
							me.spectrumCanvas.refresh();
						}
					}
				};
				this.spectrumTimer.start();
			}
			else {
				double seconds = (double)(this.spectrumTime - this.spectrumStartTime) / 1000000000.0;
				this.drawSpectrum(info, seconds);
			}
		}		
	}
	
	private void drawSpectrum(DrawInfo<Spectrum> info, double seconds) {
		if (seconds >= ANIMATION_TIME) {
			this.spectrumCanvas = null;
			this.spectrumTimer.stop();
			this.spectrum = null;
			this.spectrumStartTime = 0;
			this.spectrumTime = 0;
		}
		else {
			int num = (int)Math.floor(seconds / ANIMATION_STEP);
			Image image = num % 2 == 0 ? this.imageF1 : this.imageF2;
			
			DataPoints points = info.getPoints();
			
			double rate = seconds / ANIMATION_TIME;
			
			Range xRange = info.getXRange();
			double x = xRange.getLength() * rate + xRange.getStart();
			double y = points.calculateInterpolationY(x);
			
			int index = Collections.binarySearch(points, new Point(x, 0));
			if(index < 0) {
				index = - index - 2;
			}
			for(int i = this.prevSpecIndex + 1; i <= index && i < points.size(); i++) {
				double currentY = points.get(i).getY();
				if(currentY > y) {
					y = currentY;
				}
			}
			this.prevSpecIndex = Math.max(0, index);

			double[] coordinate = {x, y, 1.0};
			double[] position = info.getMatrix().operate(coordinate);
			
			GraphicsContext gc = info.getContext();
			
			gc.drawImage(
				image,
				position[0] - IMAGE_SIZE / 2.0,
				position[1] - IMAGE_SIZE,
				IMAGE_SIZE,
				IMAGE_SIZE
			);
		}
	}
	
	public void startSpectrum(Spectrum spectrum) {		
		this.spectrumStartTime = 0;
		this.spectrum = spectrum;
		this.chromatogramCanvas = null;
		this.prevSpecIndex = 0;
		
		MsppManager manager = MsppManager.getInstance();
		manager.invoke(Refresh.class, null);
	}
	
	
	public static CyclingManager getInstance() {
		if (instance == null) {
			instance = new CyclingManager();
		}
		return instance;
	}
}
