package pahakia.settings;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import pahakia.fault.CodeBlock;
import pahakia.fault.Fault;
import pahakia.fault.KatchBlock;

public class SettingsTest {

    @BeforeClass
    public static void setup() {
        Settings.registerSetting(SettingsTest.class.getClassLoader());
    }

    @Test
    public void test() {
        Settings.init("src/test/resources/valid");

        String str = TestSettings.ADMIN_EMAIL.get();
        assertEquals("hello.world@some.com", str);

        str = TestSettings.DEFAULT_EMAIL.get();
        assertEquals(TestSettings.DEFAULT_EMAIL.preset(), str);

        int iv = TestSettings.RELOAD_INTERVAL.getInt();
        assertEquals(3, iv);

        boolean relax = TestSettings.RELAX.getBoolean();
        assertTrue("relax expecting true", relax);
    }

    @Test
    public void testInvalidBoolean() {
        Fault.tri(new CodeBlock<Void>() {

            @Override
            public Void f() {
                Settings.init("src/test/resources/invalid-boolean");
                fail("must throw " + SettingsFaultCodes.ValueIsNotBoolean);
                return null;
            }
        }).katch(SettingsFaultCodes.ValueIsNotBoolean, new KatchBlock<Void>() {

            @Override
            public Void f(Fault fault) {
                assertEquals(TestSettings.RELAX.name, fault.getArgs()[0]);
                assertEquals("some invalid boolean", fault.getArgs()[1]);
                return null;
            }
        }).finale();
    }

    @Test
    public void testInvalidInt() {
        Fault.tri(new CodeBlock<Void>() {

            @Override
            public Void f() {
                Settings.init("src/test/resources/invalid-int");
                fail("must throw " + SettingsFaultCodes.ValueIsNotInteger);
                return null;
            }
        }).katch(SettingsFaultCodes.ValueIsNotInteger, new KatchBlock<Void>() {

            @Override
            public Void f(Fault fault) {
                assertEquals(TestSettings.RELOAD_INTERVAL.name(), fault.getArgs()[0]);
                assertEquals("invalid integer", fault.getArgs()[1]);
                return null;
            }
        }).finale();
    }

    @Test
    public void testInvalidRange() {
        Fault.tri(new CodeBlock<Void>() {

            @Override
            public Void f() {
                Settings.init("src/test/resources/invalid-range");
                fail("must throw " + SettingsFaultCodes.ValueNotInRange);
                return null;
            }
        }).katch(SettingsFaultCodes.ValueNotInRange, new KatchBlock<Void>() {

            @Override
            public Void f(Fault fault) {
                assertEquals(TestSettings.RELOAD_INTERVAL.name(), fault.getArgs()[0]);
                assertEquals("10", fault.getArgs()[1]);
                assertEquals(TestSettings.RELOAD_INTERVAL.min() + "", fault.getArgs()[2]);
                assertEquals(TestSettings.RELOAD_INTERVAL.max() + "", fault.getArgs()[3]);
                return null;
            }
        }).finale();
    }

    @Test
    public void testNotMatchPattern() {
        Fault.tri(new CodeBlock<Void>() {

            @Override
            public Void f() {
                Settings.init("src/test/resources/not-match-pattern");
                fail("must throw " + SettingsFaultCodes.ValueNotMatchPattern);
                return null;
            }
        }).katch(SettingsFaultCodes.ValueNotMatchPattern, new KatchBlock<Void>() {

            @Override
            public Void f(Fault fault) {
                assertEquals(TestSettings.DEFAULT_EMAIL.name(), fault.getArgs()[0]);
                assertEquals("hello.world.some.com", fault.getArgs()[1]);
                assertEquals(TestSettings.DEFAULT_EMAIL.pattern(), fault.getArgs()[2]);
                return null;
            }
        }).finale();
    }

    @Test
    public void testNotValidValue() {
        Fault.tri(new CodeBlock<Void>() {
            @Override
            public Void f() {
                Settings.init("src/test/resources/not-valid-value");
                fail("must throw " + SettingsFaultCodes.ValueNotValidValue);
                return null;
            }
        }).katch(SettingsFaultCodes.ValueNotValidValue, new KatchBlock<Void>() {
            @Override
            public Void f(Fault fault) {
                assertEquals(TestSettings.WEEK_DAY.name(), fault.getArgs()[0]);
                assertEquals("friday", fault.getArgs()[1]);
                assertEquals(TestSettings.WEEK_DAY.validValues().toString(), fault.getArgs()[2]);
                return null;
            }
        }).finale();
    }
}
