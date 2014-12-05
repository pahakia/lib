package pahakia.fault;

import pahakia.i18n.MessageCode;

/**
 * This class overwrites the format method() of <code>MessageCode</code> to include the code at the beginning. <br>
 * <br>
 * Usage:
 * <ol>
 * <li>
 * <code>FaultCode fc = new FaultCode("hello.message", 1, "hello {0}");</code><br>
 * This can be used to populate a class of constants, e.g. <code>FaultCodes, JreFaultCodes</code></li>
 * </ol>
 * 
 * @see Fault
 * @see MessageCode
 */
public final class FaultCode extends MessageCode {

    public FaultCode(String code, int numArgs, String messageTemplate) {
        super(code, numArgs, messageTemplate);
    }

    static <T extends Throwable> FaultCode create(Class<T> clz) {
        return new FaultCode(clz.getName(), 1, "{0}");
    }

    public String format(String... args) {
        return getCode() + ": " + super.format(args);
    }
}
