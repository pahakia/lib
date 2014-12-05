package pahakia.settings;

import java.util.Arrays;
import java.util.List;

import pahakia.fault.Fault;
import pahakia.fault.FaultCodes;

public class ValidValuesSetting extends PlainSetting {
    private List<String> validValues;

    public ValidValuesSetting(String name, String description, String preset, String... validValues) {
        super(name, description, preset);
        if (validValues == null || validValues.length == 0) {
            throw Fault.create(FaultCodes.Mandatory, "validValues", this.getClass().getName());
        }
        this.validValues = Arrays.asList(validValues);
        for (int i = 0; i < validValues.length; i++) {
            if (validValues[i] == null || validValues[i].trim().isEmpty()) {
                throw Fault.create(SettingsFaultCodes.NullValidValue, name, i + "", this.validValues.toString());
            }
        }
        if (!this.validValues.contains(preset)) {
            throw Fault.create(SettingsFaultCodes.ValueNotValidValue, name, preset,
                    this.validValues.toString());
        }
    }

    @Override
    protected void subClassValidate(List<Fault> errors, String value) {
        if (!validValues.contains(value)) {
            errors.add(Fault.create(SettingsFaultCodes.ValueNotValidValue, name, value, validValues.toString()));
        }
    }

    public List<String> validValues() {
        return validValues;
    }

    @Override
    public String toString() {
        return super.toString() + ", valid values=" + validValues;
    }
}
