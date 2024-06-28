package ninja.mspp.operation.mass_calculator.model;

import ninja.mspp.operation.mass_calculator.model.common.ChemicalCompositionMassCalculator;
import ninja.mspp.operation.mass_calculator.model.glyco.GlycanCompositionMassCalculator;
import ninja.mspp.operation.mass_calculator.model.proteo.PeptideMassCalculator;

public class MassCalculatorFactory {
	
	public static IMassCalculator createMassCalculator(MassCalculatorType type) {
		switch (type) {
		case CHEMICAL_COMPOSITION:
			return new ChemicalCompositionMassCalculator();
		case PEPTIDE:
			return new PeptideMassCalculator();
		case GLYCAN_COMPOSITION:
			return new GlycanCompositionMassCalculator();
		}
		return null;
	}
}
