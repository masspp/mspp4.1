package ninja.mspp.view.panel;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.jfree.fx.FXGraphics2D;
import org.jfree.graphics2d.svg.SVGGraphics2D;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
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
		
		Graphics2D g = new FXGraphics2D(gc);		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		onDraw(g, width, height);
	}
	
	protected void savePng(File file) throws IOException {
		int width = (int)Math.floor(this.widthProperty().doubleValue());
		int height = (int)Math.floor(this.heightProperty().doubleValue());
		
		WritableImage image = new WritableImage(width, height);
		SnapshotParameters parameters = new SnapshotParameters();
		parameters.setFill(Color.TRANSPARENT);
		this.snapshot(parameters, image);
		
		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	}
	
	protected void saveSvg(File file) {
		int width = (int)Math.floor(this.widthProperty().doubleValue());
		int height = (int)Math.floor(this.heightProperty().doubleValue());
		
		SVGGraphics2D g = new SVGGraphics2D(width, height);
		this.onDraw(g, width, height);
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
	
	protected abstract void onDraw(Graphics2D g, double width, double height);
}
