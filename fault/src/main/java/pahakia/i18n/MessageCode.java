package pahakia.i18n;

import java.text.MessageFormat;

import pahakia.fault.Fault;
import pahakia.fault.FaultCode;
import pahakia.fault.FaultCodes;

/**
 * This class formalizes the message codes for internationalization. <br>
 * <br>
 * Usage:
 * <ol>
 * <li><code>MessageCode mc1 = new MessageCode("hello.message", 1, "hello {0}");</code><br>
 * This can be used to populate a class of constants.</li>
 * </ol>
 * 
 * @see FaultCode
 */
public class MessageCode {
    private String code;
    private int numArgs = 0;
    private String messageTemplate;

    public MessageCode(String code, int numArgs, String messageTemplate) {
        if (code == null || code.trim().length() == 0) {
            throw Fault.create(FaultCodes.Mandatory, "code", this.getClass().getName());
        }
        if (numArgs < 0) {
            throw Fault.create(FaultCodes.NumArgsNegative, numArgs + "", this.getClass().getName());
        }
        if (messageTemplate == null || messageTemplate.trim().length() == 0) {
            throw Fault.create(FaultCodes.EmptyMessageTemplate);
        }
        // validate numArgs match message
        for (int i = 0; i < numArgs; i++) {
            if (messageTemplate.indexOf("{" + i + "}") < 0) {
                throw Fault.create(FaultCodes.InsufficientArgs, code, numArgs + "", messageTemplate);
            }
        }

        this.code = code;
        this.numArgs = numArgs;
        this.messageTemplate = messageTemplate;
    }

    public String getCode() {
        return code;
    }

    public int getNumArgs() {
        return numArgs;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public int hashCode() {
        return code.hashCode();
    }

    public boolean equals(Object other) {
        if (!(other instanceof MessageCode))
            return false;
        if (other == this)
            return true;

        MessageCode rhs = (MessageCode) other;
        return code.equals(rhs.code);
    }

    public String format(String... args) {
        return MessageFormat.format(messageTemplate, (Object[]) args);
    }

    public String toString() {
        return "code=" + code + ", num args=" + numArgs + ", message template=" + messageTemplate;
    }
}
