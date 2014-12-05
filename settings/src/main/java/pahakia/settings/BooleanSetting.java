package pahakia.settings;

import java.util.List;

import pahakia.fault.Fault;

public class BooleanSetting extends PlainSetting {
    public BooleanSetting(String name, String description, boolean preset) {
        super(name, description, preset + "");
    }

    @Override
    protected void subClassValidate(List<Fault> errors, String value) {
        if (!"true".equals(value) && !"false".equals(value)) {
            errors.add(Fault.create(SettingsFaultCodes.ValueIsNotBoolean, name, value));
        }
    }

    public boolean getBoolean() {
        return Settings.getBoolean(name);
    }
}
