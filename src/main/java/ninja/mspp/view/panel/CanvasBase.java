package ninja.mspp.view.panel;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class CanvasBase extends Canvas {
	public CanvasBase() {
		this.widthProperty().addListener(observable -> draw());
		this.heightProperty().addListener(observable -> draw());
	}
			
	protected void draw() {
		GraphicsContext gc = this.getGraphicsContext2D();
		double width = this.getWidth();
		double height = this.getHeight();
		
		gc.beginPath();
		gc.setFill(Color.WHITE);
		gc.setStroke(Color.WHITE);
		gc.rect(0.0,  0.0,  width,  height);
		gc.closePath();
		gc.fill();
		
		onDraw(gc, width, height);
	}
	
	@Override
	public boolean isResizable() {
		return true;
	}
	
	@Override
	public double prefWidth(double height) {
		return 0.0;
	}
	
	@Override
	public double prefHeight(double width) {
		return 0.0;
	}
	
	protected abstract void onDraw(GraphicsContext gc, double width, double height);
}
