package pahakia.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import pahakia.fault.Fault;
import pahakia.settings.annotation.RuntimeSettings;

import com.pahakia.annotation.registry.AnnotationRegistry;
import com.pahakia.annotation.registry.Pair;

public class Settings {
    private static final String SETTINGS_DIR = "SETTINGS_DIR";

    private static Map<String, String> settings = new HashMap<>();
    private static Map<String, PlainSetting> metadata = new HashMap<>();

    static {
        registerSetting(Settings.class.getClassLoader());
    }

    public static void registerSetting(ClassLoader classLoader) {
        AnnotationRegistry.register(classLoader);
        Set<Pair> pairs = AnnotationRegistry.getAnnotatedClasses(RuntimeSettings.class.getName());
        for (Pair pair : pairs) {
            Class<?> clazz = pair.loadClass();
            for (Field field : clazz.getDeclaredFields()) {
                if (PlainSetting.class.isAssignableFrom(field.getType()) && Modifier.isStatic(field.getModifiers())) {
                    // TODO: need to make sure
                    // 1 field is defined on a class
                    // 2 field is static
                    try {
                        PlainSetting setting = (PlainSetting) field.get(null);
                        metadata.put(setting.name(), setting);
                    } catch (IllegalArgumentException | IllegalAccessException ex) {
                        throw Fault.naturalize(ex);
                    }
                }
            }
        }
    }

    // load the files from -Dcom.pahakia.settings.dir=directory
    public static final void init() {
        String settingDir = System.getProperty(SETTINGS_DIR);
        if (settingDir == null) {
            settingDir = System.getenv(SETTINGS_DIR);
        }
        if (settingDir == null) {
            throw Fault.create(SettingsFaultCodes.SettingsDirNotSpecified, SETTINGS_DIR);
        }
        init(settingDir);
    }

    static final void init(String settingDir) {
        File file = new File(settingDir);
        if (!file.exists() || !file.isDirectory()) {
            throw Fault.create(SettingsFaultCodes.SettingsDirNotExists, SETTINGS_DIR, settingDir);
        }
        for (File f : file.listFiles()) {
            if (f.isFile() && f.getPath().endsWith(".properties")) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(f)) {
                    props.load(fis);
                    // System.out.println(props);
                } catch (IOException ex) {
                    throw Fault.naturalize(ex);
                }
                for (Object key : props.keySet()) {
                    String value = (String)props.get(key);
                    PlainSetting base = metadata.get(key);
                    if (base != null) {
                        List<Fault> errors = new ArrayList<>();
                        base.validate(errors, value);
                        if (!errors.isEmpty()) {
                            throw errors.get(0);
                        }
                    }
                    settings.put((String) key, value);
                }
            }
        }
    }

    public static final String get(PlainSetting base) {
        return get(base.name());
    }

    public static final int getInt(RangeSetting rs) {
        return getInt(rs.name());
    }

    public static final boolean getBoolean(PlainSetting base) {
        return getBoolean(base.name());
    }

    public static final String get(String key) {
        String v = settings.get(key);
        if (v == null) {
            PlainSetting base = metadata.get(key);
            if (base != null) {
                v = base.preset();
            }
        }
        return v;
    }

    public static void validate(List<Fault> errors, String key, String value) {
        PlainSetting base = metadata.get(key);
        if (base == null) {
            return;
        }
        base.validate(errors, value);
    }

    public static final int getInt(String key) {
        String v = get(key);
        return Integer.valueOf(v);
    }

    public static final boolean getBoolean(String key) {
        String v = get(key);
        return "true".equals(v);
    }
}
