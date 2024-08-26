package ninja.mspp.core.model.view.color;

import ninja.mspp.core.model.view.ColorTheme;
import ninja.mspp.core.model.view.Rgba;

public class GrayScaleTheme extends ColorTheme {
	@Override
	public String getName() {
		return "GrayScale";
	}

	@Override
	public Rgba getColor(double value) {
		double degree = Math.pow(value, 0.5);
		int rgb = 255 - (int)Math.round(255.0 * degree);
		int alpha = 255;
		Rgba rgba = new Rgba(rgb, rgb, rgb, alpha);
		return rgba;
	}
}
