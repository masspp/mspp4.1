package ninja.mspp.operation.peak_filter;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import ninja.mspp.MsppManager;
import ninja.mspp.core.annotation.clazz.Listener;
import ninja.mspp.core.annotation.method.MenuAction;
import ninja.mspp.core.annotation.parameter.ActiveSample;
import ninja.mspp.core.model.ms.Sample;
import ninja.mspp.core.view.ViewInfo;

@Listener("Peak Filter")
public class PeakFilterListener {
	@MenuAction("Tools > Peak Filter...")
	public void onMenu(@ActiveSample Sample sample) throws IOException {
		MsppManager manager = MsppManager.getInstance();
		
		if(sample == null) {
			ResourceBundle messages = manager.getMessages();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(messages.getString("sample.not_selected.title"));
			alert.setContentText(messages.getString("sample.not_selected.content"));
			alert.showAndWait();
		}
		else {
			ViewInfo<PeakFilterDialog> viewInfo
				= manager.showDialog(PeakFilterDialog.class, "PeakFilterDialog.fxml");
			viewInfo.getController().setSample(sample);
		}
	}
}
