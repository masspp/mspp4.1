package ninja.mspp.operation.mass_calculator.model.proteo;

import org.expasy.mzjava.proteomics.mol.Peptide;
import org.glycoinfo.ms.GlycanMassUtility.dict.residue.substituent.SubstituentDictionary;
import org.glycoinfo.ms.GlycanMassUtility.dict.residue.substituent.SubstituentType;
import org.glycoinfo.ms.GlycanMassUtility.om.IonCloud;
import org.glycoinfo.ms.GlycanMassUtility.om.Molecule;
import org.glycoinfo.ms.GlycanMassUtility.utils.MassUtils;

import ninja.mspp.operation.mass_calculator.model.IMassCalculator;

public class PeptideMassCalculator implements IMassCalculator {

	@Override
	public String validate(String name) {
		try {
			Peptide pep = Peptide.parse(name);
			pep.getMolecularMass();
		} catch (Exception e) {
			return e.getMessage();
		}
		return null;
	}

	@Override
	public double computeMass(String name) {
		// Read string as peptide sequence
		Peptide pep = Peptide.parse(name);
		return pep.getMolecularMass();
	}

	@Override
	public double computeMz(String name, String ion, String neutralLoss) {
		double pepMass = this.computeMass(name);
		// Change to custom substituent
		SubstituentDictionary.addCustumSubstituent(name, String.valueOf(pepMass), false);
		SubstituentType type = SubstituentDictionary.getSubstituent(name);

		// Create molecule for neutral loss
		Molecule neutralLossMol = new Molecule();
		if (neutralLoss != null && !neutralLoss.isEmpty())
			neutralLossMol = neutralLossMol.removeMolecules(Molecule.parse(neutralLoss), 1);

		// Calculate mz
		double mz = MassUtils.computeMz(type, IonCloud.parse(ion), neutralLossMol).doubleValue();
		// Remove custom substituent
		SubstituentDictionary.removeCustumSubstituent(name);
		return mz;
	}

}
