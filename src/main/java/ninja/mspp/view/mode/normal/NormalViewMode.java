package ninja.mspp.view.mode.normal;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import ninja.mspp.core.model.ms.Chromatogram;
import ninja.mspp.core.model.ms.Sample;
import ninja.mspp.core.model.ms.Spectrum;
import ninja.mspp.view.part.table.chromatogram.ChromatogramTableManager;
import ninja.mspp.view.part.table.sample.SampleTableManager;
import ninja.mspp.view.part.table.spectrum.SpectrumTableManager;

public class NormalViewMode implements Initializable {
	@FXML
	private GridPane tableGridPane;
	
	@FXML
	private BorderPane spectrumPane;
	
	@FXML
	private BorderPane chromatogramPane;
	
	@FXML
	private BorderPane heatmapPane;
	
	@FXML
	private BorderPane threeDPane;
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		TableView<Sample> sampleTable = SampleTableManager.getInstance().createTableView();
		this.tableGridPane.add(sampleTable, 0, 0);
		
		TableView<Chromatogram> chromatogramTable = ChromatogramTableManager.getInstance().createTableView();
		this.tableGridPane.add(chromatogramTable, 0, 1);
		
		TableView<Spectrum> spectrumTable = SpectrumTableManager.getInstance().createTableView();
		this.tableGridPane.add(spectrumTable, 0, 2);
	}
}
