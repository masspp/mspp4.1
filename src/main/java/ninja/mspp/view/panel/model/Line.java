package ninja.mspp.view.panel.model;

import java.util.List;

import ninja.mspp.core.model.ms.Point;

public class Line {
	private Point pt1;
	private Point pt2;
	
	public Line(Point pt1, Point pt2) {
		this.pt1 = pt1;
		this.pt2 = pt2;
	}
	
	public Point getPt1() {
		return pt1;
	}
	
	public Point getPt2() {
		return pt2;
	}
	
	private static 	Point calculateCrossedPoint(Line line1, Line line2) {
		Point result = null;
		
		double x1 = line1.getPt1().getX();
        double y1 = line1.getPt1().getY();
        double x2 = line1.getPt2().getX();
        double y2 = line1.getPt2().getY();
        double x3 = line2.getPt1().getX();
        double y3 = line2.getPt1().getY();
        double x4 = line2.getPt2().getX();
        double y4 = line2.getPt2().getY();;

        double denom = (x4 - x3) * (y2 - y1) - (x2 - x1) * (y4 - y3);
        if (Math.abs(denom) > 0.000001) {
        	double t = ((x3 - x1) * (y4 - y3) - (y3 - y1) * (x4 - x3)) / denom;
        	double u = ((x3 - x1) * (y2 - y1) - (y3 - y1) * (x2 - x1)) / denom;

        	if(t >= 0.0 && t <= 1.0 && u >= 0.0 && u <= 1.0) {
        		double interX = x1 + t * (x2 - x1);
        		double interY = y1 + t * (y2 - y1);
        		result = new Point(interX, interY);
        	}
        }

        return result;
	}
	
	public List<Point> calculateCrossedPoints(Bounds bounds) {
		List<Point> list = new java.util.ArrayList<Point>();

		boolean in1 = bounds.isInBound(pt1);
		boolean in2 = bounds.isInBound(pt2);

		if (in1) {
			list.add(pt1);
		}

		if (!in1 || !in2) {
			Point p1 = calculateCrossedPoint(this, new Line(new Point(bounds.getLeft(), bounds.getTop()), new Point(bounds.getLeft(), bounds.getBottom())));
			if (p1 != null) {
				list.add(p1);
			}
			Point p2 = calculateCrossedPoint(this, new Line(new Point(bounds.getLeft(), bounds.getBottom()), new Point(bounds.getRight(), bounds.getBottom())));
			if (p2 != null) {
				list.add(p2);
			}
			Point p3 = calculateCrossedPoint(this, new Line(new Point(bounds.getRight(), bounds.getBottom()), new Point(bounds.getRight(), bounds.getTop())));
			if (p3 != null) {
				list.add(p3);
			}
			Point p4 = calculateCrossedPoint(this, new Line(new Point(bounds.getRight(), bounds.getTop()), new Point(bounds.getLeft(), bounds.getTop())));
			if (p4 != null) {
				list.add(p4);
			}
		}

		if (in2) {
			list.add(pt2);
		}

		return list;
	}
}
