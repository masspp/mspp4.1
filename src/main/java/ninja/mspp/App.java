package ninja.mspp;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ninja.mspp.core.view.ViewInfo;
import ninja.mspp.view.MainFrame;


public class App extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {		
		MsppManager manager = MsppManager.getInstance();
		manager.setMainStage(primaryStage);
		
		ViewInfo<MainFrame> viewInfo = manager.createWindow(MainFrame.class, "MainFrame.fxml");
		Parent root = viewInfo.getWindow();
		MainFrame mainFrame = viewInfo.getController();
		manager.setMainFrame(mainFrame);
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		
		String[] icons = {
			"MS_icon_16.png", "MS_icon_24.png", "MS_icon_32.png", "MS_icon_48.png", "MS_icon_50.png", "MS_icon_128.png",
			"MS_icon_150.png", "MS_icon_256.png"
		};
		
		for (String icon : icons) {
			Image iconImage = new Image(getClass().getResourceAsStream("/ninja/mspp/images/icon/" + icon));
			primaryStage.getIcons().add(iconImage);
		}
		primaryStage.setTitle("Mass++ ver. 4.1");

		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
