package ninja.mspp.core.model.view;

import java.util.ArrayList;
import java.util.List;

import ninja.mspp.core.model.view.color.GrayScaleTheme;
import ninja.mspp.core.model.view.color.ThermographyTheme;

public abstract class ColorTheme {
	public abstract String getName();
	public abstract Rgba getColor(double value);
	
	private static List<ColorTheme> themes = null;
	
	@Override
	public String toString() {
		return this.getName();	
	}
	
	public static List<ColorTheme> getThemes() {
		if(themes == null) {
			themes = new ArrayList<ColorTheme>();
			
			themes.add(new ThermographyTheme());
			themes.add(new GrayScaleTheme());
		}
		
		return themes;
	}
}
