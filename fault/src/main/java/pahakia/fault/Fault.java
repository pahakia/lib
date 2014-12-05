package pahakia.fault;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * <code>Fault</code> is the only exception type. You no longer katch exception but FaultCode. A fault contains
 * 
 * <ol>
 * <li><code>FaultCode code</code>: which contains a code, a message template and the number of place holders in the
 * message template.</li>
 * <li><code>String... args</code>: the parameters used to fill the place holders in the message template in FaultCode.</li>
 * </ol>
 * 
 * <b>Usage</b>:
 * 
 * <ol>
 * <li>create Fault:<br>
 * <code>Fault.create(faultCode, "arg0", "arg1");</code></li>
 * <li>naturalize (convert) exception:<br>
 * <code>Fault.natualize(ex);</code></li>
 * <li>katch one code:<br>
 * <code>Fautl.tri(codeBlock).katch(fautlCode, katchBlock).finale([optionalFinaleBlock]);</code></li>
 * <li>katch multiple codes:<br>
 * <code>Fautl.tri(codeBlock).katch(katchBlock, faultCode0,
 * faultCode1).finale([optionalFinaleBlock]);</code></li>
 * <li>katch all exceptions:<br>
 * <code>Fautl.tri(codeBlock).katchAll(katchBlock).finale([optionalFinaleBlock]);</code></li>
 * <li>ignore:<br>
 * <code>Fautl.tri(codeBlock).ignore(fautlCode0, faultCode1).finale([optionalFinaleBlock]);</code></li>
 * </ol>
 * 
 * <b>Note</b>:
 * <ul>
 * <li><code>Fault.tri/katch/finale</code> is the replacement of the traditional <code>try/catch/finally</code> in java.
 * </li>
 * <li><code>Fault.tri/ignore/finale</code> can be used to ignore certain fault codes (YES, Exception swallowing made
 * legal!).</li>
 * <li>
 * <code>katch()/ignore()</code> can katch/ignore <code>FaultCode</code>, <code>String</code>, or String regular
 * expression.</li>
 * <li>
 * There can be multiple <code>katch</code>es/<code>ignore</code>s. <code>katch</code> and <code>ignore</code> can be
 * mixed.</li>
 * <li>
 * <code>finale</code> is mandatory. It's a programming error if it is forgotten because codeBlock/katchBlock will not
 * be executed.</li>
 * <li>
 * </ul>
 * 
 * @see FaultCode
 * @see JreFaultCodes
 */
public final class Fault extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private FaultCode code;
    private String[] args;

    private Fault(FaultCode code, String message, String... args) {
        super(message);
        this.code = code;
        this.args = args;
        if (args.length != code.getNumArgs()) {
            throw Fault.create(FaultCodes.NumArgsNotMatchCode, code.getCode(), code.getNumArgs() + "", args.length + "");
        }
    }

    private Fault(FaultCode code, String message, Throwable cause, String... args) {
        this(code, message, args);
        if (cause != null) {
            initCause(cause);
        }
    }

    public FaultCode getCode() {
        return code;
    }

    public String[] getArgs() {
        return args;
    }

    public static Fault create(FaultCode code, String... args) {
        return create(code, null, args);
    }

    static Fault create(FaultCode code, Throwable ex, String... args) {
        String message = code.format(args);
        return new Fault(code, message, ex, args);
    }

    /**
     * Convert a <code>Throwable (Error/Exception/RuntimeException)</code> other than <code>Fault</code> into a
     * <code>Fault</code>. This function unwraps the <code>Throwable</code> if it is an
     * <code>InvocationTargetException</code> or <code>UndeclaredThrowableException</code>.
     * 
     * It uses the full class name of the throwable as the fault code with a message template of <code>'{0}'</code>,
     * i.e. just one place holder. The original exception is the cause of the returned Fault. The message of the
     * original exception is used to populate the place holder. Please refer to <code>JreFaultCode</code> for the
     * FaultCodes of all JRE <code>Exceptions/Errors</code>.
     * 
     * @param ex
     *            the exception to be naturalized
     * @return Fault
     */
    public static Fault naturalize(Throwable ex) {
        while (ex instanceof InvocationTargetException || ex instanceof UndeclaredThrowableException) {
            ex = ex.getCause();
        }
        if (ex instanceof Fault) {
            return Fault.class.cast(ex);
        }
        return Fault.create(FaultCode.create(ex.getClass()), ex, ex.getMessage());
    }

    public static <T> CodeBlockWorker<T> tri(CodeBlock<T> code) {
        return new CodeBlockWorker<T>(code);
    }
}
