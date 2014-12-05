package pahakia.settings;

import pahakia.fault.FaultCode;

public class SettingsFaultCodes {
    private SettingsFaultCodes() {
    }

    public static final FaultCode ValueIsNotInteger = new FaultCode("pahakia.settings.ValueIsNotInteger", 2,
            "Setting: {0} value is not integer: {1}.");

    public static final FaultCode NullValidValue = new FaultCode("pahakia.settings.NullValidValue", 3,
            "Setting: {0} the {1}-th element is null in the valid value list: {2}.");

    public static final FaultCode MaxLessThanMin = new FaultCode("pahakia.settings.MaxLessThanMin", 3,
            "Setting: {0} max may not be less than min: min = {1}, max = {2}.");

    public static final FaultCode ValueNotInRange = new FaultCode("pahakia.settings.ValueNotInRange", 4,
            "Setting: {0} value {1} is not between min and max: min={2}, max={3}.");

    public static final FaultCode ValueNotValidValue = new FaultCode("pahakia.settings.ValueNotValidValue", 3,
            "Setting: {0} value: {1} is not in the valid values: {2}.");

    public static final FaultCode ValueNotMatchPattern = new FaultCode("pahakia.settings.ValueNotMatchPattern", 3,
            "Setting: {0} value: {1} does not match pattern: {2}.");

    public static final FaultCode NullSettingValue = new FaultCode("pahakia.settings.NullSettingValue", 1,
            "Setting: {0} value is null or empty.");

    public static final FaultCode ValueIsNotBoolean = new FaultCode("pahakia.settings.ValueIsNotBoolean", 2,
            "Setting: {0} value must be either \"true\" or \"false\", got: \"{1}\".");

    public static final FaultCode SettingsDirNotSpecified = new FaultCode("pahakia.settings.SettingsDirNotSpecified",
            1, "Please specify environment variable \"{0}\" or \"-D{0}=...\" on java command line.");

    public static final FaultCode SettingsDirNotExists = new FaultCode("pahakia.settings.SettingsDirNotExists", 2,
            "Settings dir specified through \"{0}\" does not exist: {1}");

}
