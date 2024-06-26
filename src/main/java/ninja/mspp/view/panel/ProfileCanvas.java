package ninja.mspp.view.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ninja.mspp.core.model.ms.DataPoints;
import ninja.mspp.core.model.ms.Point;
import ninja.mspp.view.panel.model.Bounds;
import ninja.mspp.view.panel.model.DrawingData;
import ninja.mspp.view.panel.model.DrawingPoint;
import ninja.mspp.view.panel.model.Line;
import ninja.mspp.view.panel.model.Range;

public class ProfileCanvas extends CanvasBase {
	protected static final int MARGIN = 10;
	protected static final int TICK_PARAMETER = 8;
	protected static final int TICK_LENGTH = 5;

	protected DrawingData data;

	protected Stack<Range> xRanges;
	protected Stack<Range> yRanges;

	protected Color profileColor;
	
	protected Font font;
	
	protected boolean impulseMode;

	public ProfileCanvas() {
		this.data = null;
		this.xRanges = new Stack<Range>();
		this.yRanges = new Stack<Range>();
		this.profileColor = Color.BLACK;
		this.font = new Font("Monospaced", 12);
		this.impulseMode = false;
	}
	
	public void setImpulseMode(boolean impulseMode) {
		this.impulseMode = impulseMode;
		this.draw();
	}
	
	public boolean getImpulseMode() {
		return this.impulseMode;
	}
	
	public void setProfileColor(Color profileColor) {
		this.profileColor = profileColor;
		this.draw();
	}
	
	public Color getProfileColor() {
		return this.profileColor;
	}

	public void setPoints(DataPoints points) {
		this.data = new DrawingData(points);
		this.draw();
	}

	public void pushXRange(Range range) {
		this.xRanges.push(range);
		this.draw();
	}

	public void popXRange() {
		this.xRanges.pop();
		this.draw();
	}

	public void pushYRange(Range range) {
		this.yRanges.push(range);
		this.draw();
	}

	public void popYRange() {
		this.yRanges.pop();
		this.draw();
	}

	private Range getXRange() {
		double start = 0.0;
		double end = 0.0;

		if (this.xRanges.isEmpty()) {
			List<DrawingPoint> points = this.data.getPoints(0);
			start = points.get(0).getX();
			end = points.get(points.size() - 1).getX();
		} else {
			start = this.xRanges.peek().getStart();
			end = this.xRanges.peek().getEnd();
		}

		start = Math.max(0.0, start);
		end = Math.max(end, start + 0.01);
		return new Range(start, end);
	}

	private Range getYRange() {
		double start = 0.0;
		double end = 0.0;

		if (this.yRanges.isEmpty()) {
			List<DrawingPoint> points = this.data.getPoints(0);
			Range xRange = this.getXRange();

			int startIndex = Collections.binarySearch(points, new DrawingPoint(xRange.getStart(), 0.0, 0.0, 0.0, 0.0));
			int endIndex = Collections.binarySearch(points, new DrawingPoint(xRange.getEnd(), 0.0, 0.0, 0.0, 0.0));

			if (startIndex < 0) {
				startIndex = Math.max(0, -startIndex - 2);
			}
			if (endIndex < 0) {
				endIndex = -endIndex - 1;
			}

			for (int i = startIndex; i <= endIndex; i++) {
				DrawingPoint point = points.get(i);
				end = Math.max(end, point.getMaxY());
			}
			end = end * 1.15;
		} 
		else {
			start = this.yRanges.peek().getStart();
			end = this.yRanges.peek().getEnd();
		}

		start = Math.max(0.0, start);
		end = Math.max(end, start + 0.01);
		return new Range(start, end);
	}

	private double[] getTicks(Range range) {
		double unit = range.getLength() / (double) TICK_PARAMETER;
		double log10 = Math.round(Math.log10(unit));
		double scale = Math.pow(10.0, log10);
		if (scale > unit) {
			scale /= 2.0;
		}

		int startIndex = (int) Math.ceil(range.getStart() / scale);
		int endIndex = (int) Math.floor(range.getEnd() / scale);

		double[] ticks = new double[endIndex - startIndex + 1];
		for (int i = startIndex; i <= endIndex; i++) {
			ticks[i - startIndex] = i * scale;
		}

		return ticks;
	}

	private String[] getTickLabels(double[] ticks) {
		double space = ticks[1] - ticks[0];
		int log10 = (int) Math.floor(Math.log10(space));
		String format = "%.0f";
		if (log10 < 0) {
			format = String.format("%%.%df", -log10);
		}

		String[] labels = new String[ticks.length];
		for (int i = 0; i < ticks.length; i++) {
			labels[i] = String.format(format, ticks[i]);
		}
		return labels;
	}
	
	private Bounds calculateMargin(String[] xLabels, String[] yLabels) {
		int martinTop = MARGIN;
		int marginBottom = MARGIN + TICK_LENGTH;
		int marginLeft = MARGIN + TICK_LENGTH;
		int marginRight = MARGIN;

		for (String xLabl : xLabels) {
			Text text = new Text(xLabl);
			text.setFont(this.font);
			marginBottom = Math.max(marginBottom,  MARGIN + TICK_LENGTH + (int)text.getLayoutBounds().getHeight());
		}
		
		for (String yLabl : yLabels) {
			Text text = new Text(yLabl);
			text.setFont(this.font);
			marginLeft = Math.max(marginLeft, MARGIN + TICK_LENGTH + (int)text.getLayoutBounds().getWidth());
		}
		
		Bounds margin = new Bounds(martinTop, marginRight, marginBottom, marginLeft);
		return margin;		
	}
	
	private RealMatrix calculateMatrix(double width, double height, Range xRange, Range yRange, Bounds margin) {
		RealMatrix dataMatrix = new Array2DRowRealMatrix(3, 3);
		dataMatrix.setEntry(0, 0, xRange.getLength());
		dataMatrix.setEntry(0,  1,  0.0);
		dataMatrix.setEntry(0, 2, xRange.getStart());
		dataMatrix.setEntry(1, 0, 0.0);
		dataMatrix.setEntry(1, 1, yRange.getLength());
		dataMatrix.setEntry(1, 2, yRange.getStart());
		dataMatrix.setEntry(2, 0, 0.0);
		dataMatrix.setEntry(2, 1, 0.0);
		dataMatrix.setEntry(2, 2, 1.0);
		
		RealMatrix windowMatrix = new Array2DRowRealMatrix(3, 3);
		windowMatrix.setEntry(0, 0, width - margin.getLeft() - margin.getRight());
		windowMatrix.setEntry(0, 1, 0.0);
		windowMatrix.setEntry(0, 2, margin.getLeft());
		windowMatrix.setEntry(1, 0, 0.0);
		windowMatrix.setEntry(1, 1, - (height - margin.getTop() - margin.getBottom()));
		windowMatrix.setEntry(1, 2, height - margin.getBottom());
		windowMatrix.setEntry(2, 0, 0.0);
		windowMatrix.setEntry(2, 1, 0.0);
		windowMatrix.setEntry(2, 2, 1.0);
		
		RealMatrix inverse = MatrixUtils.inverse(dataMatrix);
		RealMatrix matrix = windowMatrix.multiply(inverse);
		return matrix;
	}
	
	protected List<Line> getLines(List<DrawingPoint> points, RealMatrix matrix) {
		List<Line> lines = new ArrayList<Line>();
		
		Point lastPoint = null;
		for (DrawingPoint point : points) {
			
			double[] maxXy = { point.getX(), point.getMaxY(), 1.0 };
			double[] windowMaxXy = matrix.operate(maxXy);
						
			if (this.impulseMode) {
				double[] zeroXy = { point.getX(), 0.0, 1.0 };				
				double[] windowZeroXy = matrix.operate(zeroXy);
				
				Line line = new Line(new Point(windowZeroXy[0], windowZeroXy[1]), new Point(windowMaxXy[0], windowMaxXy[1]));
				lines.add(line);
			}
			else {
				double[] leftXy = { point.getX(), point.getLeftY(), 1.0 };
				double[] windowLeftXy = matrix.operate(leftXy);
				
				double[] minXy = { point.getX(), point.getMinY(), 1.0 };
				double[] windowMinXy = matrix.operate(minXy);
				
				double[] rightXy = { point.getX(), point.getRightY(), 1.0 };
				double[] windowRightXy = matrix.operate(rightXy);
				
				if(lastPoint != null) {
					Line line = new Line(lastPoint, new Point(windowLeftXy[0], windowLeftXy[1]));
					lines.add(line);
				}
				Line line1 = new Line(new Point(windowLeftXy[0], windowLeftXy[1]), new Point(windowMinXy[0], windowMinXy[1]));
				lines.add(line1);
				Line line2 = new Line(new Point(windowMinXy[0], windowMinXy[1]), new Point(windowMaxXy[0], windowMaxXy[1]));
				lines.add(line2);
				Line line3 = new Line(new Point(windowMaxXy[0], windowMaxXy[1]), new Point(windowRightXy[0], windowRightXy[1]));
				lines.add(line3);

				lastPoint = new Point(windowRightXy[0], windowRightXy[1]);
			}
		}
		
		return lines;
	}
	
	
	protected void drawProfile(GraphicsContext gc, RealMatrix matrix, double width, double height, Bounds margin, List<DrawingPoint> points) {
		int top = (int)Math.round(margin.getTop());
		int right = (int)Math.round(width - margin.getRight());
		int bottom = (int)Math.round(height - margin.getBottom());
		int left = (int)Math.round(margin.getLeft());
		
		Bounds bounds = new Bounds(top, right, bottom, left);
        gc.setStroke(this.profileColor);
        gc.beginPath();
        
        List<Line> lines = getLines(points, matrix);
        
        for(Line line : lines) {
			List<Point> list = line.calculateCrossedPoints(bounds);
			if (list.size() >= 2) {
				gc.moveTo(list.get(0).getX(), list.get(0).getY());
				gc.lineTo(list.get(1).getX(), list.get(1).getY());
			}
        }
        gc.stroke();
    }
	
	protected void drawRect(GraphicsContext gc, Bounds margin, double width, double height) {
		gc.setStroke(Color.BLACK);
		gc.beginPath();
		gc.moveTo(margin.getLeft(), margin.getTop());
		gc.lineTo(width - margin.getRight(), margin.getTop());
		gc.lineTo(width - margin.getRight(), height - margin.getBottom());
		gc.lineTo(margin.getLeft(), height - margin.getBottom());
		gc.lineTo(margin.getLeft(), margin.getTop());
		gc.stroke();
	}
	
	protected void drawXAxis(GraphicsContext gc, double[] xTicks, String[] xLabels, RealMatrix matrix, Bounds margin,
			double width, double height) {
		gc.setStroke(Color.BLACK);
		gc.beginPath();
		
		double maxWidth = 1.0;
		for (String label : xLabels) {
			Text text = new Text(label);
			text.setFont(this.font);
			maxWidth = Math.max(maxWidth, text.getLayoutBounds().getWidth());
		}
		double unitWidth = (width - margin.getLeft() - margin.getRight()) * 0.8 / (double)xTicks.length;
		int steps = (int)Math.ceil(maxWidth / unitWidth);
		if (steps < 1) {
			steps = 1;
		}
		
		for (int i = 0; i < xTicks.length; i++) {
			double x = xTicks[i];
			double[] xy = { x, 0.0, 1.0 };
			double[] windowXy = matrix.operate(xy);

			gc.moveTo(windowXy[0], height - margin.getBottom());
			gc.lineTo(windowXy[0], height - margin.getBottom() + TICK_LENGTH);
			gc.stroke();

			if(i % steps == 0) {
				Text text = new Text(xLabels[i]);
				text.setFont(this.font);
				double textWidth = text.getLayoutBounds().getWidth();
				double textHeight = text.getLayoutBounds().getHeight();
				double xPos = windowXy[0] - textWidth / 2.0;
				if(xPos > 0.0 && xPos + textWidth < width) {
					gc.strokeText(xLabels[i], xPos, height - margin.getBottom() + TICK_LENGTH + textHeight);
				}
			}
		}
	}
	
	protected void drawYAxis(GraphicsContext gc, double[] yTicks, String[] yLabels, RealMatrix matrix, Bounds margin,
			double width, double height) {
		gc.setStroke(Color.BLACK);
		gc.beginPath();
		
		double maxHeight = 1.0;
		for (String label : yLabels) {
			Text text = new Text(label);
			text.setFont(this.font);
			maxHeight = Math.max(maxHeight, text.getLayoutBounds().getHeight());
		}
		
		double unitHeight = (height - margin.getTop() - margin.getBottom()) * 0.8 / (double)yTicks.length;
		int steps = (int)Math.ceil(maxHeight / unitHeight);
		
		for (int i = 0; i < yTicks.length; i++) {
			double y = yTicks[i];
			double[] xy = { 0.0, y, 1.0 };
			double[] windowXy = matrix.operate(xy);

			gc.moveTo(margin.getLeft(), windowXy[1]);
			gc.lineTo(margin.getLeft() - TICK_LENGTH, windowXy[1]);
			gc.stroke();

			if(i % steps == 0) {
				Text text = new Text(yLabels[i]);
				text.setFont(this.font);
				double textWidth = text.getLayoutBounds().getWidth();
				double textHeight = text.getLayoutBounds().getHeight();
				gc.strokeText(
					yLabels[i],
					margin.getLeft() - TICK_LENGTH - textWidth,
					windowXy[1] + textHeight / 3.0
				);
			}
		}
	}

	@Override
	protected void onDraw(GraphicsContext gc, double width, double height) {
		if(this.data != null) {
			gc.setFont(this.font);
		
			Range xRange = this.getXRange();
			Range yRange = this.getYRange();
			double[] xTicks = this.getTicks(xRange);
			double[] yTicks = this.getTicks(yRange);
			String[] xLabels = this.getTickLabels(xTicks);
			String[] yLabels = this.getTickLabels(yTicks);
		
			Bounds margin = this.calculateMargin(xLabels, yLabels);
			RealMatrix matrix = calculateMatrix(width, height, xRange, yRange, margin);
		
			int level = this.data.calculateLevel(width, xRange.getStart(), xRange.getEnd());
			List<DrawingPoint> points = this.data.getPoints(level);
		
			drawProfile(gc, matrix, width, height, margin, points);
			drawRect(gc, margin, width, height);
			drawXAxis(gc, xTicks, xLabels, matrix, margin, width, height);
			drawYAxis(gc, yTicks, yLabels, matrix, margin, width, height);
		}
	}
	

}