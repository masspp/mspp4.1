package ninja.mspp.view;

import java.io.InputStream;

import javafx.animation.AnimationTimer;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ninja.mspp.MsppManager;

public class GuiManager {
	private static GuiManager instance;
	
	private static final int CURSOR_SIZE = 48;
	private static final int CURSOR_MARGIN = 5;
	private static final int ROTATE_UNIT = 10;
	private static final int ANIMATION_DURATION = 60000000;
	
	private Image cyclingImage1;
	private Image cyclingImage2;
	private int cyclingAngle;
	private AnimationTimer cyclingTimer;
	private long lastCyclingTime;
	
	
	private GuiManager() {
		InputStream stream1 = this.getClass().getResourceAsStream("/ninja/mspp/images/animation/cycling-f1.png");
		InputStream stream2 = this.getClass().getResourceAsStream("/ninja/mspp/images/animation/cycling-f2.png");
		
		this.cyclingImage1 = new Image(stream1);
		this.cyclingImage2 = new Image(stream2);		
		this.cyclingAngle = 0;
		this.cyclingTimer = null;
	}
	
	private Image getRotatedCyclingImage(int angle) {
		int step = angle / ROTATE_UNIT;
		Image image = step % 2 == 0 ? this.cyclingImage1 : this.cyclingImage2;
		
		Canvas canvas = new Canvas(CURSOR_SIZE, CURSOR_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0, 0, CURSOR_SIZE, CURSOR_SIZE);

        gc.save();
        gc.translate(CURSOR_SIZE / 2, CURSOR_SIZE / 2);
        gc.rotate(angle);
        gc.drawImage(
        		image,
        		- CURSOR_SIZE / 2 + CURSOR_MARGIN,
        		- CURSOR_SIZE / 2 + CURSOR_MARGIN,
        		CURSOR_SIZE - 2 * CURSOR_MARGIN,
        		CURSOR_SIZE - 2 * CURSOR_MARGIN
        );
        gc.restore();

        WritableImage rotatedImage = new WritableImage(CURSOR_SIZE, CURSOR_SIZE);
        canvas.snapshot(null, rotatedImage);
        
        Image converted = this.convertImage(rotatedImage);

        return converted;
	}
	
	
	private Image convertImage(Image image) {
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		
		WritableImage converted = new WritableImage(width, height);
		
		PixelReader reader = image.getPixelReader();
		PixelWriter writer = converted.getPixelWriter();
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				Color color = reader.getColor(x, y);
				if(color.getRed() > 0.95 && color.getGreen() > 0.95 && color.getBlue() > 0.95) {
					writer.setColor(x,  y,  Color.TRANSPARENT);
				}
				else {
					writer.setColor(x,  y,  color);
				}
			}
		}
		
		return converted;
	}
	
	
	private void updateCursor() {
		MsppManager manager = MsppManager.getInstance();
		Stage stage = manager.getMainStage();
		Scene scene = stage.getScene();
		
		Image image = this.getRotatedCyclingImage(this.cyclingAngle);
		this.cyclingAngle += ROTATE_UNIT;
		if(this.cyclingAngle > 360) {
			this.cyclingAngle -= 360;
		}
		if(this.cyclingAngle < -360) {
			this.cyclingAngle  += 360;
		}
		Cursor cursor = new ImageCursor(image, CURSOR_SIZE / 2, CURSOR_SIZE / 2);
		scene.setCursor(cursor);
	}
	
	public void startWaitingCursor() {
		System.out.println("fugafuga");
		this.cyclingAngle = 0;
		this.lastCyclingTime = 0;
		GuiManager me = this;
		this.cyclingTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				long diff = now - me.lastCyclingTime;
				if(diff > ANIMATION_DURATION) {
					me.lastCyclingTime = now;
					updateCursor();
				}
			}
		};
		this.cyclingTimer.start();
	}
	
	
	public void endWaitingCursor() {
		if(this.cyclingTimer != null) {
			this.cyclingTimer.stop();
			this.cyclingTimer = null;
			
			MsppManager manager = MsppManager.getInstance();
			Stage stage = manager.getMainStage();
			Scene scene = stage.getScene();			
			scene.setCursor(Cursor.DEFAULT);
		}
	}
	
	
	public static GuiManager getInstance() {
		if(instance == null) {
			instance = new GuiManager();
		}
		return instance;
	}
}
