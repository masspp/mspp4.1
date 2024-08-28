package ninja.mspp.core.model.ms;

import java.util.ArrayList;
import java.util.Collections;

public class DataPoints extends ArrayList<Point> {
	public double findMaxY(double startX, double endX) {
		double maxY = 0.0;
		
		int startIndex = 0;
		int endIndex = this.size() - 1;
		
		if(startX > 0.0) {
			startIndex = Collections.binarySearch(this, new Point(startX, 0.0));
			if(startIndex < 0) {
				startIndex = - startIndex - 1;
			}
		}
		
		if (endX > 0.0) {
			endIndex = Collections.binarySearch(this, new Point(endX, 0.0));
			if (endIndex < 0) {
				endIndex = - endIndex - 2;
			}
		}
		
		for(int i = startIndex; i <= endIndex; i++) {
			double y = this.get(i).getY();
			if(y > maxY) {
				maxY = y;
			}
		}
		return maxY;
	}
}
