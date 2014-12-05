package pahakia.settings;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import pahakia.fault.CodeBlock;
import pahakia.fault.Fault;
import pahakia.fault.FaultCodes;
import pahakia.fault.KatchBlock;

public class ValidValuesSettingTest {
    private static Logger logger = Logger.getLogger(RangeSettingTest.class.getName());

    @Test
    public void validValue() {
        new ValidValuesSetting("name", "desc", "sun", "earth", "sun", "moon");
        new ValidValuesSetting("name", "desc", "moon", "earth", "sun", "moon");
        new ValidValuesSetting("name", "desc", "earth", "sun", "moon", "earth");
        validValueHelper("hello");
        validValueHelper("moon1");
    }

    private void validValueHelper(final String preset) {
        Fault.tri(new CodeBlock<Integer>() {
            @Override
            public Integer f() {
                logger.info("preset=" + preset);
                new ValidValuesSetting("name", "desc", preset, "earth", "sun", "moon");
                fail("should raise ValueNotInValidValues Fault");
                return null;
            }
        }).katch(SettingsFaultCodes.ValueNotValidValue, new KatchBlock<Integer>() {
            @Override
            public Integer f(Fault fault) {
                assertEquals("first arg should be '" + preset + "'", preset, fault.getArgs()[1]);
                assertEquals("first arg should be '[earth, sun, moon]'", "[earth, sun, moon]", fault.getArgs()[2]);
                return null;
            }
        }).finale();
    }
    
    @Test
    public void nullValidValues() {
        nullValidValuesHelper(null);
        nullValidValuesHelper(new String[] {});
    }

    private void nullValidValuesHelper(final String[] validValues) {
        Fault.tri(new CodeBlock<Void>() {
            @Override
            public Void f() {
                logger.info("preset=" + ((validValues == null) ? null : Arrays.asList(validValues)));
                new ValidValuesSetting("name", "desc", "hello", validValues);
                fail("should raise ValueNotInValidValues Fault");
                return null;
            }
        }).katch(FaultCodes.Mandatory, new KatchBlock<Void>() {
            @Override
            public Void f(Fault fault) {
                assertEquals("first arg should be 'validValues'", "validValues", fault.getArgs()[0]);
                return null;
            }
        }).finale();
    }

    @Test
    public void nullValidValue() {
        nullValidValueHelper(new String[] { null }, "0");
        nullValidValueHelper(new String[] { "abc", null }, "1");
    }

    private void nullValidValueHelper(final String[] validValues, final String pos) {
        Fault.tri(new CodeBlock<Void>() {
            @Override
            public Void f() {
                logger.info("preset=" + ((validValues == null) ? null : Arrays.asList(validValues)));
                new ValidValuesSetting("name", "desc", "hello", validValues);
                fail("should raise NullValidValue Fault");
                return null;
            }
        }).katch(SettingsFaultCodes.NullValidValue, new KatchBlock<Void>() {
            @Override
            public Void f(Fault fault) {
                assertEquals("first arg should be '" + pos + "'", pos, fault.getArgs()[1]);
                return null;
            }
        }).finale();
    }

    @Test
    public void validate() {
        ValidValuesSetting def = new ValidValuesSetting("name", "desc", "sun", "earth", "sun", "moon");
        List<Fault> errors = new ArrayList<>();
        for (String str : new String[] { "earth", "sun", "moon" }) {
            def.validate(errors, str);
            assertTrue(errors.isEmpty());
        }
        validateHelper("hello");
        validateHelper("moon1");
    }

    private void validateHelper(final String value) {
        logger.info("value=" + value);
        ValidValuesSetting def = new ValidValuesSetting("name", "desc", "sun", "earth", "sun", "moon");
        List<Fault> errors = new ArrayList<>();
        def.validate(errors, value);
        assertFalse("There should be 1 error", errors.isEmpty());
        Fault fault = errors.get(0);
        assertEquals("Wrong fault code", SettingsFaultCodes.ValueNotValidValue, fault.getCode());
        assertEquals("Wrong args[0]", value, fault.getArgs()[1]);
        assertEquals("Wrong args[1]", "[earth, sun, moon]", fault.getArgs()[2]);
    }
}
