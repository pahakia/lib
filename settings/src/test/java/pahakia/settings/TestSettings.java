package pahakia.settings;

import pahakia.settings.annotation.RuntimeSettings;

@RuntimeSettings
public class TestSettings {
    public static final RangeSetting RELOAD_INTERVAL = new RangeSetting("reload.interval", "range from 1 to 5", 1, 5, 3);
    public static final PatternSetting ADMIN_EMAIL = new PatternSetting("admin.email", "admin email", ".+@.+");
    public static final PatternSetting DEFAULT_EMAIL = new PatternSetting("default.email", "admin email", ".+@.+",
            "admin@pahakia.com");
    public static final ValidValuesSetting WEEK_DAY = new ValidValuesSetting("week.day", "Week day", "sun", "sun",
            "mon", "tue", "wed", "thu", "fri", "sat");
    public static final BooleanSetting RELAX = new BooleanSetting("relax", "should we relax today", false);

}
