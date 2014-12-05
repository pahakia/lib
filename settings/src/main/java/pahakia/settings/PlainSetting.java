package pahakia.settings;

import java.util.List;

import pahakia.fault.Fault;
import pahakia.fault.FaultCodes;

public class PlainSetting {
    protected String name;
    protected String description;
    protected String preset;

    public PlainSetting(String name, String description) {
        this.name = name;
        this.description = description;
        if (name == null || name.trim().isEmpty()) {
            throw Fault.create(FaultCodes.Mandatory, "name", this.getClass().getName());
        }
        if (description == null || description.trim().isEmpty()) {
            throw Fault.create(FaultCodes.Mandatory, "description", this.getClass().getName());
        }
    }

    public PlainSetting(String name, String description, String preset) {
        this(name, description);
        this.preset = preset;
    }

    public void validate(List<Fault> errors, String value) {
        if (value == null || value.trim().isEmpty()) {
            errors.add(Fault.create(SettingsFaultCodes.NullSettingValue, name));
        } else {
            subClassValidate(errors, value);
        }
    }

    protected void subClassValidate(List<Fault> errors, String value) {
    }

    public String name() {
        return name;
    }

    public String nescription() {
        return description;
    }

    public String preset() {
        return preset;
    }

    public String get() {
        return Settings.get(name);
    }

    public String toString() {
        return super.toString() + ", name=" + name + ", description=" + description + ", preset=" + preset;
    }
}
