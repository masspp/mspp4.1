package ninja.mspp.view.panel;

import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import ninja.mspp.MsppManager;
import ninja.mspp.core.annotation.method.SpectrumAction;
import ninja.mspp.core.annotation.method.SpectrumCanvasBackground;
import ninja.mspp.core.annotation.method.SpectrumCanvasForeground;
import ninja.mspp.core.model.listener.ListenerMethod;
import ninja.mspp.core.model.ms.DataPoints;
import ninja.mspp.core.model.ms.Spectrum;
import ninja.mspp.core.model.view.Bounds;
import ninja.mspp.core.model.view.Range;
import ninja.mspp.core.view.DrawInfo;
import ninja.mspp.core.view.SpectrumActionEvent;

public class SpectrumCanvas extends ProfileCanvas {
	protected static final Color SPECTRUM_COLOR = Color.RED;
	
	protected Spectrum spectrum;
	
	public SpectrumCanvas() {
		super("m/z", "Int.");
		this.setProfileColor(SPECTRUM_COLOR);
		this.spectrum = null;
	}
	
	private ContextMenu createActionMenu(double mz) {
		MsppManager manager = MsppManager.getInstance();
		
		ContextMenu menu = new ContextMenu();
		
		List<ListenerMethod<SpectrumAction>> methods = manager.getMethods(SpectrumAction.class);
		methods.sort(
			(method1, method2) -> {
				return method1.getAnnotation().order() - method2.getAnnotation().order();
			}
		);
		SpectrumActionEvent event = new SpectrumActionEvent(this.spectrum, mz);
		for(ListenerMethod<SpectrumAction> method : methods) {
			MenuItem item = new MenuItem(method.getAnnotation().value());
			item.setOnAction(
				(e) -> {
					method.invoke(event);
				}
			);
			menu.getItems().add(item);
		}
		
		return menu;
	}
	
	
	@Override
	protected void onMouseClicked(MouseEvent event) {
		if(event.getButton() == MouseButton.SECONDARY) {
			RealMatrix inverse = MatrixUtils.inverse(this.matrix);
			double[] coordinate = {event.getX(), event.getY(), 1.0};
			double[] data = inverse.operate(coordinate);
			double mz = data[0];
			
			ContextMenu menu = createActionMenu(mz);
			menu.show(this, event.getScreenX(), event.getScreenY());
		}
		else {
			super.onMouseClicked(event);
		}
	}
	
	@Override
	protected void drawForeground(GraphicsContext gc, double width, double height, Bounds margin, RealMatrix matrix, Range xRange, Range yRange) {
		MsppManager manager = MsppManager.getInstance();
		
		DrawInfo<Spectrum> drawInfo = new DrawInfo<Spectrum>(
			this.spectrum, width, height, margin, this.points, matrix, xRange, yRange, gc, this
		);
		
		manager.invoke(SpectrumCanvasForeground.class, drawInfo);
	}

	
	@Override
	protected void drawBackground(GraphicsContext gc, double width, double height, Bounds margin, RealMatrix matrix, Range xRange, Range yRange) {
		MsppManager manager = MsppManager.getInstance();
		
		DrawInfo<Spectrum> drawInfo = new DrawInfo<Spectrum>(
			this.spectrum, width, height, margin, this.points, matrix, xRange, yRange, gc, this
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

