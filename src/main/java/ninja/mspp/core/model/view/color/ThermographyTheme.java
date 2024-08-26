package ninja.mspp.core.model.view.color;

import ninja.mspp.core.model.view.ColorTheme;
import ninja.mspp.core.model.view.Rgba;

public class ThermographyTheme extends ColorTheme {

	@Override
	public String getName() {
        return "Thermography";	
	}

	@Override
	public Rgba getColor(double value) {
		int red = 0;
		int green = 0;
		int blue = 0;
		int alpha = 255;
		
		if(value < 0.1) {
			double degree = value / 0.1;
			blue = (int)Math.round(255.0 * degree);
		}
		else if(value < 0.25) {
			double degree = (value - 0.1) / 0.15;
			blue = 255;
			green = (int)Math.round(255.0 * degree);
		}
		else if(value < 0.45) {
			double degree = (value - 0.25) / 0.2;
			green = 255;
			blue = (int)Math.round(255.0 * (1.0 - degree));
		}
		else if(value < 0.7) {
			double degree = (value - 0.45) / 0.25;
			green = 255;
			red = (int)Math.round(255.0 * degree);
		}
		else {
			double degree = (value - 0.7) / 0.3;
			red = 255;
			green = (int)Math.round(255.0 * (1.0 - degree));
		}

		Rgba rgba = new Rgba(red, green, blue, alpha);
		return rgba;
	}
}
