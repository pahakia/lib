package pahakia.settings;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static pahakia.fault.Fault.tri;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.junit.Test;

import pahakia.fault.CodeBlock;
import pahakia.fault.Fault;
import pahakia.fault.FaultCodes;

public class PatternSettingTest {
    private static Logger logger = Logger.getLogger(PatternSettingTest.class.getName());

    @Test
    public void mandatory() {
        mandatoryHelper(null);
        mandatoryHelper("");
        new PatternSetting("name", "desc", "hello");
        new PatternSetting("name", "desc", "hello.*");
        new PatternSetting("name", "desc", "\\d+");
    }

    private void mandatoryHelper(final String pattern) {
        tri(new CodeBlock<Integer>() {
            @Override
            public Integer f() {
                logger.info("pattern=" + pattern);
                new PatternSetting("name", "desc", pattern);
                fail("should raise Mandatory Fault");
                return null;
            }
        }).ignore(FaultCodes.Mandatory).finale();
    }
    
    @Test
    public void valueNotMatchPattern() {
        valueNotMatchPatternHelper("\\d+", "abc");
        valueNotMatchPatternHelper("\\d+", "5c");
        valueNotMatchPatternHelper("\\d+", "c5");
        valueNotMatchPatternHelper("\\d+", "5 c");
        valueNotMatchPatternHelper("(.+=.+)+", "a=");
        valueNotMatchPatternHelper("(.+=.+)+", "=b");
        valueNotMatchPatternHelper("(.+=.+)+", "=");
        valueNotMatchPatternHelper("(.+=.+)+", "cd");
        valueNotMatchPatternHelper("([^=]+=[^=]+)+", "cd=abc,ab=");
    }

    private void valueNotMatchPatternHelper(final String pattern, final String value) {
        PatternSetting pat = new PatternSetting("name", "desc", pattern);
        ArrayList<Fault> errors = new ArrayList<Fault>();
        pat.validate(errors, value);
        assertEquals(1, errors.size());
        Fault f = errors.get(0);
        assertEquals(SettingsFaultCodes.ValueNotMatchPattern, f.getCode());
        assertEquals(value, f.getArgs()[1]);
        assertEquals(pattern, f.getArgs()[2]);
    }

    @Test
    public void valueMatchPattern() {
        valueMatchPatternHelper("\\d+", "5");
        valueMatchPatternHelper("\\d+", "55");
        valueMatchPatternHelper("\\d+", "123");
        valueMatchPatternHelper("\\d+", "0");
        valueMatchPatternHelper("(.+=.+)+", "a=b");
        valueMatchPatternHelper("(.+=.+)+", "aa=bbb");
        valueMatchPatternHelper("(.+=.+)+", "ccc = ddd");
        valueMatchPatternHelper("(.+=.+)+", "cd= dddd ");
        valueMatchPatternHelper("(.+=.+)+", "ccc = ddd,aa=bb");
        valueMatchPatternHelper("(.+=.+)+", "cd= dddd ,aa = bb ");
    }

    private void valueMatchPatternHelper(final String pattern, final String value) {
        PatternSetting pat = new PatternSetting("name", "desc", pattern);
        ArrayList<Fault> errors = new ArrayList<Fault>();
        pat.validate(errors, value);
        assertEquals(0, errors.size());
    }
    
    @Test
    public void nullSettingValue() {
        nullSettingValueHelper(null);
        nullSettingValueHelper("");
    }

    private void nullSettingValueHelper(final String value) {
        PatternSetting pat = new PatternSetting("name", "desc", "some pattern");
        ArrayList<Fault> errors = new ArrayList<Fault>();
        pat.validate(errors, value);
        assertEquals(1, errors.size());
        Fault f = errors.get(0);
        assertEquals(SettingsFaultCodes.NullSettingValue, f.getCode());
        assertEquals("name", f.getArgs()[0]);
    }
}
