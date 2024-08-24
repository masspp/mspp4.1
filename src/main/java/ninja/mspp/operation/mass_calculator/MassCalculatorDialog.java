package ninja.mspp.operation.mass_calculator;

import java.io.IOException;
import java.util.ResourceBundle;

import org.glycoinfo.ms.GlycanMassUtility.dict.molecule.NeutralType;
import org.glycoinfo.ms.GlycanMassUtility.om.IMassElement;
import org.glycoinfo.ms.GlycanMassUtility.om.IonCloud;
import org.glycoinfo.ms.GlycanMassUtility.om.Molecule;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import ninja.mspp.MsppManager;
import ninja.mspp.core.model.ms.Sample;
import ninja.mspp.operation.mass_calculator.model.mass.CompoundCreator;
import ninja.mspp.operation.mass_calculator.model.mass.CompoundType;
import ninja.mspp.operation.mass_calculator.model.mass.MassCalculator;
import ninja.mspp.operation.peak_filter.PeakFilterDialog;

public class MassCalculatorDialog {

	@FXML
	private TextField nameField;

	@FXML
	private ChoiceBox<String> typeChoiceBox;

	@FXML
	private Label egLabel;

	@FXML
	private TextField massField;

	@FXML
	private Label warningLabel;

	@FXML
	private RadioButton mhRadioButton;

	@FXML
	private RadioButton mNaRadioButton;

	@FXML
	private RadioButton m2NaRadioButton;

	@FXML
	private CheckBox watLossCheckBox;

	@FXML
	private TextField mzField;

	@FXML
	private Button searchButton;

	@FXML
	private Button resetButton;

	private ToggleGroup adductGroup;

	private Stage currentStageForPeakFilter;
	private PeakFilterDialog peakFilterDialog;

	@FXML
	private void initialize() {
		// Add items to ChoiceBox
		typeChoiceBox.getItems().addAll(
				CompoundType.CHEMICAL_COMPOSITION.getName(),
				CompoundType.PEPTIDE.getName(),
				CompoundType.GLYCAN_COMPOSITION.getName()
			);
		typeChoiceBox.getSelectionModel().selectFirst();
		updateEgLabel();

		// Create and set up the ToggleGroup for the RadioButtons
		adductGroup = new ToggleGroup();
		mhRadioButton.setToggleGroup(adductGroup);
		mNaRadioButton.setToggleGroup(adductGroup);
		m2NaRadioButton.setToggleGroup(adductGroup);

		// Initial settings and other initialization tasks
		warningLabel.setText("");  // Clear initial warning message

		// Add listeners to handle changes in nameField and typeChoiceBox
		nameField.textProperty().addListener((observable, oldValue, newValue) -> handleInputChange());
		typeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> handleInputChange());
		adductGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> handleInputChange());
		watLossCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> handleInputChange());

		currentStageForPeakFilter = null;
	}

	private void updateEgLabel() {
		egLabel.setText("");
		if (getSelectedType() != null)
			egLabel.setText("e.g. " + getSelectedType().getEg());
	}

	private void reset() {
		nameField.clear();
		typeChoiceBox.getSelectionModel().selectFirst();
		updateEgLabel();

		warningLabel.setText("");
		massField.setText("");
		mzField.setText("");
	}

	// Method to handle changes in input fields
	private void handleInputChange() {
		// Clear warning message
		warningLabel.setText("");

		updateEgLabel();

		CompoundType type = getSelectedType();
		String name = nameField.getText();
		if (name.isEmpty()) {
			massField.clear();
			mzField.clear();
			return;
		}

		// Calculate mass and mz
		MassCalculator calc = new MassCalculator();
		try {
			IMassElement compound = CompoundCreator.create(name, type);
			calc.addCompound(compound);

			IonCloud ionCloud = IonCloud.parse( getSelectedIon() );

			Molecule waterLoss = null;
			if ( watLossCheckBox.isSelected() )
				waterLoss = NeutralType.Water.getMolecule();

			// Display mass and mz
			massField.setText(String.valueOf(calc.computeMass()));
			mzField.setText(String.valueOf(calc.computeMz(ionCloud, waterLoss)));
		} catch (IllegalArgumentException e) {
			// handle invalid input
			warningLabel.setText(e.getMessage());
			massField.clear();
			mzField.clear();
			return;
		}
	}

	@FXML
	private void onAddToPeakFilter() throws IOException {
		// Call the dialog for adding the current mass to the peak filter
		MsppManager manager = MsppManager.getInstance();
		
		Sample sample = manager.getActiveSample();

		// Check if a sample is selected
		if(sample == null) {
			ResourceBundle messages = manager.getMessages();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(messages.getString("sample.not_selected.title"));
			alert.setContentText(messages.getString("sample.not_selected.content"));
			alert.showAndWait();
			return;
		}

		if (currentStageForPeakFilter == null)
			setUpPeakFilterDialog(manager);

		// Set the mass and mz values
		String name = nameField.getText();
		if (watLossCheckBox.isSelected())
			name += " - H2O";
		name += " + "+getSelectedIon();
		peakFilterDialog.setInformation(name, mzField.getText(), null, false);

		currentStageForPeakFilter.show();
	}

	private void setUpPeakFilterDialog(MsppManager manager) throws IOException {
		FXMLLoader loader = new FXMLLoader(PeakFilterDialog.class.getResource("PeakFilterDialog.fxml"));
		Parent root = loader.load();
		peakFilterDialog = loader.getController();
		peakFilterDialog.setSample(manager.getActiveSample());

		Stage stage = new Stage();
		stage.initOwner(manager.getMainStage());
		Scene scene = new Scene(root);
		stage.setScene(scene);
		currentStageForPeakFilter = stage;
	}

	@FXML
	private void onReset() {
		reset();
	}

	private CompoundType getSelectedType() {
		return CompoundType.fromName(typeChoiceBox.getValue());
	}

	private String getSelectedIon() {
		if (mhRadioButton.isSelected())
			return "H";
		else if (mNaRadioButton.isSelected())
			return "Na";
		else if (m2NaRadioButton.isSelected())
			return "2Na";
		return null;
	}

}
