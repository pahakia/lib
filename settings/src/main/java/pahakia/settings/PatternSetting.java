package pahakia.settings;

import java.util.List;

import pahakia.fault.Fault;
import pahakia.fault.FaultCodes;

public class PatternSetting extends PlainSetting {
    private String pattern;

    public PatternSetting(String name, String description, String pattern) {
        super(name, description);
        this.pattern = pattern;
        if (pattern == null || pattern.trim().isEmpty()) {
            throw Fault.create(FaultCodes.Mandatory, "pattern", this.getClass().getName());
        }
    }

    public PatternSetting(String name, String description, String pattern, String preset) {
        this(name, description, pattern);
        this.preset = preset;
        if (!preset.matches(pattern)) {
            throw Fault.create(SettingsFaultCodes.ValueNotMatchPattern, name, preset, pattern);
        }
    }

    @Override
    protected void subClassValidate(List<Fault> errors, String value) {
        if (!value.matches(pattern)) {
            errors.add(Fault.create(SettingsFaultCodes.ValueNotMatchPattern, name, value, pattern));
        }
    }

    public String pattern() {
        return pattern;
    }

    @Override
    public String toString() {
        return super.toString() + ", pattern=" + pattern;
    }
}
