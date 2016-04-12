package automata;

public class RuleParsingException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;

	RuleParsingException() {
		super("Unable to parse expression");
	}

	RuleParsingException(String expression) {
		super("Unable to parse expression: " + expression);
	}
}