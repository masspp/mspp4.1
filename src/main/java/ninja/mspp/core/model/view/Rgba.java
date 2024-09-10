package ninja.mspp.core.model.view;

public class Rgba {
	private int red;
	private int green;
	private int blue;
	private int alpha;
	
	public Rgba(int red, int green, int blue, int alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public Rgba(int red, int green, int blue) {
		this(red, green, blue, 255);
	}

	public int getRed() {
		return red;
	}

	public void setRed(int red) {
		this.red = red;
	}

	public int getGreen() {
		return green;
	}

	public void setGreen(int green) {
		this.green = green;
	}

	public int getBlue() {
		return blue;
	}

	public void setBlue(int blue) {
		this.blue = blue;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
	
	public int getPixel() {
		int pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
		return pixel;
	}
}
