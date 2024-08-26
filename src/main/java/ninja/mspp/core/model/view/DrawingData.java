package ninja.mspp.core.model.view;

import java.util.ArrayList;
import java.util.List;

import ninja.mspp.core.model.ms.DataPoints;
import ninja.mspp.core.model.ms.Point;

public class DrawingData {
	private static final double FIRST_RANGE = 0.01;
	private static final int MAXIMUM_LEVEL = 10;
	
	List<List<DrawingPoint>> pointList;
	
	public DrawingData(DataPoints points) {
		this.pointList = new ArrayList<List<DrawingPoint>>();
		this.createData(points);
	}
	
	private void createData(DataPoints points) {
		double range = FIRST_RANGE;
		
		List<DrawingPoint> list = new ArrayList<DrawingPoint>();		
		for(Point point : points) {
			double x = point.getX();	
			double y = point.getY();
			DrawingPoint pt = new DrawingPoint(x, y, y, y, y);
			list.add(pt);
		}
		this.pointList.add(list);

		List<DrawingPoint> prevPoint = list;
		for (int i = 1; i <= MAXIMUM_LEVEL; i++) {
			list = createNextLevel(prevPoint, range);
			range *= 2.0;
			this.pointList.add(list);
		}
	}
	
	private List<DrawingPoint> createNextLevel(List<DrawingPoint> prevPoint, double range) {
		List<DrawingPoint> list = new ArrayList<DrawingPoint>();
		DrawingPoint lastPoint = null;
		int lastIndex = -1;
		
		for(DrawingPoint point : prevPoint) {
			int index = (int)Math.round(point.getX() / range);
			if(index == lastIndex) {
				lastPoint.setMaxY(Math.max(lastPoint.getMaxY(), point.getMaxY()));
				lastPoint.setMinY(Math.min(lastPoint.getMinY(), point.getMinY()));
				lastPoint.setRightY(point.getRightY());
			}
			else {
				lastIndex = index;
				lastPoint = new DrawingPoint(index * range, point.getMinY(), point.getMaxY(), point.getLeftY(), point.getRightY());
				list.add(lastPoint);
			}
		}
		
		return list;
	}
	
	public int calculateLevel(double width, double minX, double maxX) {
		double unit = (maxX - minX) / width / FIRST_RANGE;
		double level = Math.log(unit) / Math.log(2.0);
		int finalLevel = Math.min(MAXIMUM_LEVEL,  Math.max(0, (int)Math.floor(level)));
		return finalLevel;
	}
	
	public List<DrawingPoint> getPoints(int level) {
		return this.pointList.get(level);
	}
}
