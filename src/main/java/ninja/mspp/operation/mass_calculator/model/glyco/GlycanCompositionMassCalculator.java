package ninja.mspp.operation.mass_calculator.model.glyco;

import org.glycoinfo.ms.GlycanMassUtility.om.Composition;
import org.glycoinfo.ms.GlycanMassUtility.om.IonCloud;
import org.glycoinfo.ms.GlycanMassUtility.om.Molecule;
import org.glycoinfo.ms.GlycanMassUtility.utils.MassUtils;

import ninja.mspp.operation.mass_calculator.model.IMassCalculator;

public class GlycanCompositionMassCalculator implements IMassCalculator {

	@Override
	public String validate(String name) {
		Composition compo = Composition.parse(name);
		if (compo == null) {
			return Composition.getErrorMessage();
		}
		return null;
	}

	@Override
	public double computeMass(String name) {
		// Read string as glycan composition with Byonic format, e.g. HexNAc(2)Hex(5)dHex(1)
		Composition compo = Composition.parse(name);
		return MassUtils.computeMass(compo).doubleValue();
	}

	@Override
	public double computeMz(String name, String ion, String neutralLoss) {
		Composition compo = Composition.parse(name);
		IonCloud cloud = IonCloud.parse(ion);
		Molecule neutralLossMol = new Molecule();
		if (neutralLoss != null && !neutralLoss.isEmpty())
			neutralLossMol = neutralLossMol.removeMolecules(Molecule.parse(neutralLoss), 1);
		return MassUtils.computeMz(compo, cloud, neutralLossMol).doubleValue();
	}

}
