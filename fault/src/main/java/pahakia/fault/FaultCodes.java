package pahakia.fault;

/**
 * This class contains the <code>FaultCode</code>s of the pahakia-fault package.
 */
public final class FaultCodes {
    private FaultCodes() {
    }

    public static final FaultCode Mandatory = new FaultCode("pahakia.fault.Mandatory", 2,
            "{0} is mandatory when creating {1}.");
    public static final FaultCode NumArgsNegative = new FaultCode("pahakia.fault.NumArgsNegative", 2,
            "Number of arguments {0} may not be negative when creating {1}.");
    public static final FaultCode InsufficientArgs = new FaultCode("pahakia.fault.InsufficientArgs", 3,
            "Message template does not have enough \"'{'n'}'\"s for code: {0}, expected: {1}, message template: \"{2}\".");
    public static final FaultCode EmptyMessageTemplate = new FaultCode("pahakia.fault.EmptyMessageTemplate", 0,
            "Message template may not be null or empty.");
    public static final FaultCode NumArgsNotMatchCode = new FaultCode("pahakia.fault.CodeNumArgsNoMatch", 3,
            "The number of String parameters must match the number of arguments specified in code: {0}, expected: {1}, got: {2}.");
}
