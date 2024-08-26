package ninja.mspp.view.panel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ninja.mspp.core.model.ms.DataPoints;
import ninja.mspp.core.model.ms.Point;
import ninja.mspp.core.model.view.Bounds;
import ninja.mspp.core.model.view.DrawingData;
import ninja.mspp.core.model.view.DrawingPoint;
import ninja.mspp.core.model.view.Line;
import ninja.mspp.core.model.view.Range;

public class ProfileCanvas extends CanvasBase {
	protected static final int GRAPH_MARGIN = 30;
	protected static final int TITLE_MARGIN = 5;
	protected static final int TICK_PARAMETER = 8;
	protected static final int TICK_LENGTH = 5;

	protected DrawingData data;
	protected DataPoints points;

	protected Stack<Range> xRanges;
	protected Stack<Range> yRanges;

	protected Color profileColor;
	
	protected Font font;
	
	protected boolean impulseMode;
	
	protected RealMatrix matrix;
	protected Bounds margin;
	
	protected Point startPoint;
	protected Point currentPoint;
	protected RealMatrix startMatrix;
	
	protected String xTitle;
	protected String yTitle;


	public ProfileCanvas(String xTitle, String yTitle) {
		this.data = null;
		this.xRanges = new Stack<Range>();
		this.yRanges = new Stack<Range>();
		this.profileColor = Color.BLACK;
		this.font = new Font("Monospaced", 12);
		this.impulseMode = false;
		this.matrix = null;
		this.margin = null;
		
		this.xTitle = xTitle;
		this.yTitle = yTitle;
		
		this.startPoint = null;
		this.currentPoint = null;
		
		this.addMouseEvents();
	}
	
	protected void addMouseEvents() {
		ProfileCanvas me = this;
		this.addEventHandler(
			MouseEvent.MOUSE_PRESSED,
			(event)->{
				me.onMousePressed(event);
			}
		);
		
		this.addEventHandler(
			MouseEvent.MOUSE_DRAGGED,
			(event) -> {
				me.onMouseDragged(event);
			}
		);
		
		this.addEventHandler(
			MouseEvent.MOUSE_MOVED,
			(event) -> {
				me.onMouseMoved(event);
			}
		);
		
		this.addEventHandler(
			MouseEvent.MOUSE_RELEASED,
			(event) -> {
				me.onMouseReleased(event);
			}
		);
		
		this.addEventHandler(
			MouseEvent.MOUSE_CLICKED,
			(event) -> {
				me.onMouseClicked(event);
			}
		);	
	}
	
	protected void onMousePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();
        
        double width = this.getWidth();
        double height = this.getHeight();
        
		if (x >= this.margin.getLeft() && x <= width - this.margin.getRight() && y >= height - this.margin.getBottom()) {
			if(!this.xRanges.isEmpty()) {
				this.startMatrix = this.matrix;
			    Range range = new Range(this.xRanges.peek().getStart(), this.xRanges.peek().getEnd());
			    this.xRanges.push(range);
			}
		}
		else {
			this.startMatrix = null;
		}
        
        this.startPoint = new Point(x, y);
    }
	
	protected void onMouseDragged(MouseEvent event) {
		if(this.startPoint != null) {
			double x = event.getX();
			double y = event.getY();
			double width = this.getWidth();
			double height = this.getHeight();
			this.currentPoint = new Point(x, y);
			
			if (this.startPoint.getX() >= this.margin.getLeft()
					&& this.startPoint.getX() <= width - this.margin.getRight()
					&& this.startPoint.getY() >= height - this.margin.getBottom()) {
				if (this.startMatrix != null) {
					this.xRanges.pop();
					double center = this.xRanges.peek().getCenter();
					double length = this.xRanges.peek().getLength();
					
					RealMatrix inverse = MatrixUtils.inverse(this.startMatrix);
					
					double[] prevX = {this.startPoint.getX(), 0.0, 1.0};
					double prevDataX = inverse.operate(prevX)[0];
										
					double[] currentX = {x, 0.0, 1.0};
					double currentDataX = inverse.operate(currentX)[0];
					
					double dx = currentDataX - prevDataX;
					double start = center - length / 2.0 - dx;
					double end = center + length / 2.0 - dx;
					
					double minX = this.data.getPoints(0).get(0).getX();
					double maxX = this.data.getPoints(0).get(this.data.getPoints(0).size() - 1).getX();
					
					if (start < minX) {
						start = minX;
						end = start + length;
					}
					else if (end > maxX) {
						end = maxX;
						start = end - length;
					}
					
					Range range = new Range(start, end);
					this.xRanges.push(range);
				}
			}
			
			this.draw();
		}
	}
	
	protected void onMouseMoved(MouseEvent event) {
		double width = this.getWidth();
		double height = this.getHeight();
		double x = event.getX();
		double y = event.getY();
		
		if(this.margin == null) {
			this.setCursor(Cursor.DEFAULT);
		}
		else {
			if (x < this.margin.getLeft() || x > width - this.margin.getRight() || y < this.margin.getTop() ) {
				this.setCursor(Cursor.DEFAULT);
			}
			else if (y > height - this.margin.getBottom()) {
				if(this.xRanges.isEmpty()) {
					this.setCursor(Cursor.DEFAULT);
				}
				else {
					this.setCursor(Cursor.OPEN_HAND);
				}
			}
			else {
				this.setCursor(Cursor.H_RESIZE);
			}
		}
	}
	
	protected void onMouseClicked(MouseEvent event) {
		if(event.getClickCount() == 2) {
			this.startPoint = null;
			this.currentPoint = null;
			this.xRanges.clear();
			this.draw();
		}		
	}
	
	protected void onMouseReleased(MouseEvent event) {
		if(this.startPoint != null) {
			if(this.startPoint.getX() >= this.margin.getLeft() && this.startPoint.getX() <= this.getWidth() - this.margin.getRight() &&
                    this.startPoint.getY() >= this.margin.getTop() && this.startPoint.getY() <= this.getHeight() - this.margin.getBottom()) {
                double x = event.getX();
                x = Math.max(x, this.margin.getLeft());
                x = Math.min(x, this.getWidth() - this.margin.getRight());
                
                double startX = Math.min(this.startPoint.getX(), x);                
                double endX = Math.max(this.startPoint.getX(), x);
                
                RealMatrix inverse = MatrixUtils.inverse(this.matrix);
                double[] start = { startX, 0.0, 1.0 };
                double[] end = { endX, 0.0, 1.0 };
                double dataStart = inverse.operate(start)[0];
                double dataEnd = inverse.operate(end)[0];
                
                if(dataEnd > dataStart + 0.01) {                
                	Range range = new Range(dataStart, dataEnd);
                	this.xRanges.push(range);
                }
			}
		}
		this.startPoint = null;
		this.currentPoint = null;
		this.startMatrix = null;
		this.draw();
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
		this.points = points;
		this.xRanges.clear();
		this.yRanges.clear();		
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

	protected Range getXRange() {
		double start = 0.0;
		double end = 0.0;


		if (this.xRanges.isEmpty()) {
			List<DrawingPoint> points = this.data.getPoints(0);
			if (!points.isEmpty()) {
				start = points.get(0).getX();
				end = points.get(points.size() - 1).getX();
			}
		} 
		else {
			start = this.xRanges.peek().getStart();
			end = this.xRanges.peek().getEnd();
		}

		start = Math.max(0.0, start);
		end = Math.max(end, start + 0.01);
		return new Range(start, end);
	}
	
	protected Range getYRange() {
		double start = 0.0;
		double end = 0.0;

		if (this.yRanges.isEmpty()) {
			List<DrawingPoint> points = this.data.getPoints(0);
			Range xRange = this.getXRange();

			int startIndex = Collections.binarySearch(points, new DrawingPoint(xRange.getStart(), 0.0, 0.0, 0.0, 0.0));
			int endIndex = Collections.binarySearch(points, new DrawingPoint(xRange.getEnd(), 0.0, 0.0, 0.0, 0.0));

			if (startIndex < 0) {
				startIndex = Math.max(0, -startIndex - 2);
				if (startIndex < 0) {
					startIndex = 0;
				}
			}
			if (endIndex < 0) {
				endIndex = - endIndex - 1;
				if (endIndex >= points.size()) {
					endIndex = points.size() - 1;
				}
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

	protected double[] getTicks(Range range) {
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

	protected String[] getTickLabels(double[] ticks) {
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
	
	protected Bounds calculateMargin(String[] xLabels, String[] yLabels) {
		int martinTop = GRAPH_MARGIN;
		int marginBottom = GRAPH_MARGIN + TICK_LENGTH;
		int marginLeft = GRAPH_MARGIN + TICK_LENGTH;
		int marginRight = GRAPH_MARGIN;

		for (String xLabl : xLabels) {
			Text text = new Text(xLabl);
			text.setFont(this.font);
			marginBottom = Math.max(marginBottom, GRAPH_MARGIN + TICK_LENGTH + (int)text.getLayoutBounds().getHeight());
		}
		
		for (String yLabl : yLabels) {
			Text text = new Text(yLabl);
			text.setFont(this.font);
			marginLeft = Math.max(marginLeft, GRAPH_MARGIN + TICK_LENGTH + (int)text.getLayoutBounds().getWidth());
		}
		
		Bounds margin = new Bounds(martinTop, marginRight, marginBottom, marginLeft);
		return margin;		
	}
	
	protected RealMatrix calculateMatrix(double width, double height, Range xRange, Range yRange, Bounds margin) {
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
	
	protected void drawMouseBackground(GraphicsContext gc, RealMatrix matrix, double width, double height, Bounds margin,
			Point startPoint, Point currentPoint) {		
		if (startPoint != null && currentPoint != null) {
			if (startPoint.getY() >= margin.getTop() && startPoint.getX() >= margin.getLeft() && startPoint.getX() <= width - margin.getRight()) {
				gc.setFill(Color.LIGHTGRAY);
				double px1 = startPoint.getX();
				double px2 = currentPoint.getX();
				if(px1 != px2) {
					double startX = Math.min(px1, px2);
					startX = Math.max(startX, margin.getLeft());
					startX = Math.min(startX, width - margin.getRight());
				
					double endX = Math.max(px1, px2);
					endX = Math.max(endX, margin.getLeft());
					endX = Math.min(endX, width - margin.getRight());
				
					double endY = height - margin.getBottom();
					double startY = margin.getTop();
				
					if (startPoint.getY() <= height - margin.getBottom()) {
						gc.fillRect(startX,  startY,  endX - startX,  endY - startY);
					}
				}
			}
		}
	}
	
	public void refresh() {
		this.draw();
	}
	
	protected void drawForeground(GraphicsContext gc, double width, double height, Bounds margin, RealMatrix matrix, Range xRange, Range yRange) {
	}
	
	protected void drawBackground(GraphicsContext gc, double width, double height, Bounds margin, RealMatrix matrix, Range xRange, Range yRange) {
	}

	@Override
	protected void onDraw(GraphicsContext gc, double width, double height) {
		if(this.data != null) {
			drawData(gc, width, height);
		}
	}
	
	protected void drawTitles(GraphicsContext gc, double width, double height) {
		gc.setFont(this.font);

		Text text = new Text(this.xTitle);
		text.setFont(this.font);
		double textWidth = text.getLayoutBounds().getWidth();
		double textHeight = text.getLayoutBounds().getHeight();		
		gc.strokeText(this.xTitle, width - textWidth - TITLE_MARGIN, height - TITLE_MARGIN);
			
		text = new Text(this.yTitle);
		text.setFont(this.font);
		textWidth = text.getLayoutBounds().getWidth();
		textHeight = text.getLayoutBounds().getHeight();
		gc.strokeText(this.yTitle, TITLE_MARGIN, textHeight + TITLE_MARGIN);
	}
	
	protected void drawData(GraphicsContext gc, double width, double height) {
		gc.setFont(this.font);
		
		Range xRange = this.getXRange();
		Range yRange = this.getYRange();
		double[] xTicks = this.getTicks(xRange);
		double[] yTicks = this.getTicks(yRange);
		String[] xLabels = this.getTickLabels(xTicks);
		String[] yLabels = this.getTickLabels(yTicks);

		Bounds margin = this.calculateMargin(xLabels, yLabels);
		RealMatrix matrix = calculateMatrix(width, height, xRange, yRange, margin);
		this.matrix = matrix;
		this.margin = margin;

		int level = this.data.calculateLevel(width, xRange.getStart(), xRange.getEnd());
		List<DrawingPoint> points = this.data.getPoints(level);

		drawMouseBackground(gc, matrix, width, height, margin, this.startPoint, this.currentPoint);
		drawBackground(gc, width, height, margin, matrix, xRange, yRange);
		drawProfile(gc, matrix, width, height, margin, points);
		drawForeground(gc, width, height, margin, matrix, xRange, yRange);
		drawRect(gc, margin, width, height);
		drawXAxis(gc, xTicks, xLabels, matrix, margin, width, height);
		drawYAxis(gc, yTicks, yLabels, matrix, margin, width, height);
		drawTitles(gc, width, height);
	}
}
