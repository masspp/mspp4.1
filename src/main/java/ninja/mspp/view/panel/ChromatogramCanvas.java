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
import ninja.mspp.core.annotation.method.ChromatogramAction;
import ninja.mspp.core.annotation.method.ChromatogramCanvasBackground;
import ninja.mspp.core.annotation.method.ChromatogramCanvasForeground;
import ninja.mspp.core.model.listener.ListenerMethod;
import ninja.mspp.core.model.ms.Chromatogram;
import ninja.mspp.core.model.ms.DataPoints;
import ninja.mspp.core.model.view.Bounds;
import ninja.mspp.core.model.view.Range;
import ninja.mspp.core.view.ChromatogramActionEvent;
import ninja.mspp.core.view.DrawInfo;

public class ChromatogramCanvas extends ProfileCanvas {
	protected static final Color CHROMATOGRAM_COLOR = Color.BLUE;
	
	protected Chromatogram chromatogram;
	
	public ChromatogramCanvas() {
		super("RT", "Int.");
		this.setProfileColor(CHROMATOGRAM_COLOR);
		this.chromatogram = null;
	}
	
	private ContextMenu createActionMenu(double mz) {
		MsppManager manager = MsppManager.getInstance();
		
		ContextMenu menu = new ContextMenu();
		
		List<ListenerMethod<ChromatogramAction>> methods = manager.getMethods(ChromatogramAction.class);
		methods.sort(
			(method1, method2) -> {
				return method1.getAnnotation().order() - method2.getAnnotation().order();
			}
		);
		ChromatogramActionEvent event = new ChromatogramActionEvent(this.chromatogram, mz);
		for(ListenerMethod<ChromatogramAction> method : methods) {
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
		
		DrawInfo<Chromatogram> drawInfo = new DrawInfo<Chromatogram>(
			this.chromatogram, width, height, margin, this.points, matrix, xRange, yRange, gc, this
		);
		
		manager.invoke(ChromatogramCanvasForeground.class, drawInfo);
	}

	
	@Override
	protected void drawBackground(GraphicsContext gc, double width, double height, Bounds margin, RealMatrix matrix, Range xRange, Range yRange) {
		MsppManager manager = MsppManager.getInstance();
		
		DrawInfo<Chromatogram> drawInfo = new DrawInfo<Chromatogram>(
			this.chromatogram, width, height, margin, this.points, matrix, xRange, yRange, gc, this
		);
		
		manager.invoke(ChromatogramCanvasBackground.class, drawInfo);		
	}
	
	
	public void setChromatogram(Chromatogram chromatogram) {
		DataPoints points = chromatogram.readDataPoints();
		this.chromatogram = chromatogram;
		this.setPoints(points);
	}
}
