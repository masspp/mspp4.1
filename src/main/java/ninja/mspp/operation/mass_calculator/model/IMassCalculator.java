package ninja.mspp.operation.mass_calculator.model;

public interface IMassCalculator {

	/**
	 * Validate the input.
	 * @param name input
	 * @return error message
	 */
	public String validate(String name);

	public double computeMass(String name);

	public double computeMz(String name, String ion, String neutralLoss);
}
