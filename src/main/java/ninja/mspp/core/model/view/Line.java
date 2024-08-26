package ninja.mspp.core.model.view;

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
				
		double d1x = line1.getPt2().getX() - line1.getPt1().getX();
		double d1y = line1.getPt2().getY() - line1.getPt1().getY();
		double p1x = line1.getPt1().getX();
		double p1y = line1.getPt1().getY();
		double d2x = line2.getPt2().getX() - line2.getPt1().getX();
		double d2y = line2.getPt2().getY() - line2.getPt1().getY();
		double p2x = line2.getPt1().getX();
		double p2y = line2.getPt1().getY();

		double detA = - d1x * d2y + d2x * d1y;
		if(Math.abs(detA) > 0.0000001) {
			double t = (- d2y * (p2x - p1x) + d2x * (p2y - p1y)) / detA;
			double u = (- d1y * (p2x - p1x) + d1x * (p2y - p1y)) / detA;
			if (t >= 0.0 && t <= 1.0 && u >= 0.0 && u <= 1.0) {
				double x = p1x + t * d1x;
				double y = p1y + t * d1y;
				result = new Point(x, y);
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
			Point p1 = calculateCrossedPoint(this, new Line(new Point(bounds.getLeft(), bounds.getTop()), new Point(bounds.getRight(), bounds.getTop())));
			if (p1 != null) {
				list.add(p1);
			}
			Point p2 = calculateCrossedPoint(this, new Line(new Point(bounds.getRight(), bounds.getTop()), new Point(bounds.getRight(), bounds.getBottom())));
			if (p2 != null) {
				list.add(p2);
			}
			Point p3 = calculateCrossedPoint(this, new Line(new Point(bounds.getLeft(), bounds.getBottom()), new Point(bounds.getRight(), bounds.getBottom())));
			if (p3 != null) {
				list.add(p3);
			}
			Point p4 = calculateCrossedPoint(this, new Line(new Point(bounds.getLeft(), bounds.getTop()), new Point(bounds.getLeft(), bounds.getBottom())));
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
