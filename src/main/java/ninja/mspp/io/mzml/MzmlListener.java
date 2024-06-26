package ninja.mspp.io.mzml;

import java.io.File;

import io.github.msdk.MSDKException;
import javafx.stage.FileChooser;
import ninja.mspp.MsppManager;
import ninja.mspp.core.annotation.clazz.Listener;
import ninja.mspp.core.annotation.method.MenuAction;
import ninja.mspp.core.annotation.method.OnOpenSample;
import ninja.mspp.core.model.ms.Sample;

@Listener("mzML Input Listener")
public class MzmlListener {
	@MenuAction(value = "File > Open > mzML...", order = 0)
	public void onMzml() throws MSDKException {
		MsppManager manager = MsppManager.getInstance();
		
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("mzML Files", "*.mzML"));
		chooser.setTitle("Open mzML File");
		File file = chooser.showOpenDialog(manager.getMainStage());
		
		if (file != null) {
			MzmlReader reader = new MzmlReader();
			Sample sample = reader.read(file.getAbsolutePath());
			manager.invoke(OnOpenSample.class, sample);
		}
	}
}
