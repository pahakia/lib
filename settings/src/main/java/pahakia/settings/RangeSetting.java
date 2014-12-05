package pahakia.settings;

import java.util.List;

import pahakia.fault.Fault;

public class RangeSetting extends PlainSetting {
    private int min;
    private int max;

    public RangeSetting(String name, String description, int min, int max, int preset) {
        super(name, description, "" + preset);
        this.min = min;
        this.max = max;
        if (min > max) {
            throw Fault.create(SettingsFaultCodes.MaxLessThanMin, name, min + "", max + "");
        }
        if (min > preset || max < preset) {
            throw Fault.create(SettingsFaultCodes.ValueNotInRange, name, preset + "", min + "", max + "");
        }
    }

    @Override
    protected void subClassValidate(List<Fault> errors, String value) {
        try {
            int val = Integer.valueOf(value);
            if (val < min || val > max) {
                errors.add(Fault.create(SettingsFaultCodes.ValueNotInRange, name, value, min + "", max + ""));
            }
        } catch (NumberFormatException ex) {
            errors.add(Fault.create(SettingsFaultCodes.ValueIsNotInteger, name, value));
        }
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    public int getInt() {
        return Settings.getInt(name);
    }

    @Override
    public String toString() {
        return super.toString() + ", min=" + min + ", max=" + max;
    }
}
