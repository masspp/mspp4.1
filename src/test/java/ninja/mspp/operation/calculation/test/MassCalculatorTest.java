package ninja.mspp.operation.calculation.test;

import static org.junit.Assert.*;

import org.junit.Test;

import ninja.mspp.operation.mass_calculator.model.IMassCalculator;
import ninja.mspp.operation.mass_calculator.model.common.ChemicalCompositionMassCalculator;
import ninja.mspp.operation.mass_calculator.model.glyco.GlycanCompositionMassCalculator;
import ninja.mspp.operation.mass_calculator.model.proteo.PeptideMassCalculator;

public class MassCalculatorTest {

	@Test
	public void testGlycanComposition() {
		IMassCalculator calc = new GlycanCompositionMassCalculator();
		assertEquals(180.063388, calc.computeMass("Hex"), 0.000001);
		assertEquals(221.089937, calc.computeMass("HexNAc"), 0.000001);
		assertEquals(910.327780, calc.computeMass("Hex(3)HexNAc(2)"), 0.000001);

		assertEquals(933.317001, calc.computeMz("Hex(3)HexNAc(2)", "Na", null), 0.000001);
		assertEquals(478.153111, calc.computeMz("Hex(3)HexNAc(2)", "2Na", null), 0.000001);
	}

	@Test
	public void testPeptide() {
		IMassCalculator calc = new PeptideMassCalculator();
		assertEquals(965.529429, calc.computeMass("PROTEIN"), 0.000001);
		assertEquals(799.359964, calc.computeMass("PEPTIDE"), 0.000001);

		assertEquals(800.367240, calc.computeMz("PEPTIDE", "H", null), 0.000001);
//		assertEquals(478.154208, calc.computeMz("Hex(3)HexNAc(2)", "2Na"), 0.000001);
	}

	@Test
	public void testChemicalComposition() {
		IMassCalculator calc = new ChemicalCompositionMassCalculator();
		assertEquals(180.063388, calc.computeMass("C6H12O6"), 0.000001);
	}

}
