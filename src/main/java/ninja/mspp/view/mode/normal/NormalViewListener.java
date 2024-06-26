package ninja.mspp.view.mode.normal;

import java.io.IOException;

import javafx.scene.Parent;
import ninja.mspp.MsppManager;
import ninja.mspp.core.annotation.clazz.Listener;
import ninja.mspp.core.annotation.method.ViewMode;
import ninja.mspp.core.view.ViewInfo;

@Listener("Normal View Mode")
public class NormalViewListener {
	@ViewMode(value = "Normal", order = 0)
	public Parent createNormalView() throws IOException {
		MsppManager manager = MsppManager.getInstance();
		ViewInfo<NormalViewMode> info = manager.createWindow(NormalViewMode.class, "NormalViewMode.fxml");
		return info.getWindow();
	}
}
