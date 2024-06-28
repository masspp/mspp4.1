package ninja.mspp.operation.mass_calculator.model.common;

import org.glycoinfo.ms.GlycanMassUtility.om.IonCloud;
import org.glycoinfo.ms.GlycanMassUtility.om.Molecule;
import org.glycoinfo.ms.GlycanMassUtility.utils.MassUtils;

import ninja.mspp.operation.mass_calculator.model.IMassCalculator;

public class ChemicalCompositionMassCalculator implements IMassCalculator {

	@Override
	public String validate(String name) {
		Molecule mol = Molecule.parse(name);
		if (mol == null)
			return "Invalid chemical composition.";
		return null;
	}

	@Override
	public double computeMass(String name) {
		// Read string as chemical composition, e.g. "C6H12O6"
		Molecule mol = Molecule.parse(name);
		return mol.getMass().doubleValue();
	}

	@Override
	public double computeMz(String name, String ion, String neutralLoss) {
		// Read string as chemical composition
		Molecule mol = Molecule.parse(name);
		IonCloud cloud = IonCloud.parse(ion);
		Molecule neutralLossMol = new Molecule();
		if (neutralLoss != null && !neutralLoss.isEmpty())
			neutralLossMol = neutralLossMol.removeMolecules(Molecule.parse(neutralLoss), 1);
		return MassUtils.computeMz(mol, cloud, neutralLossMol).doubleValue();
	}

}
