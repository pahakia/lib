package pahakia.settings;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static pahakia.fault.Fault.tri;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import pahakia.fault.CodeBlock;
import pahakia.fault.Fault;
import pahakia.settings.SettingsFaultCodes;
import pahakia.settings.RangeSetting;

public class RangeSettingTest {
    private static Logger logger = Logger.getLogger(RangeSettingTest.class.getName());

    @Test
    public void valueNotInRange() {
        valueNotInRangeHelper(3, 5, 6);
        valueNotInRangeHelper(3, 5, 2);
        new RangeSetting("abc", "desc", 3, 5, 3);
        new RangeSetting("abc", "desc", 3, 5, 5);
        new RangeSetting("abc", "desc", 3, 5, 4);
    }

    private void valueNotInRangeHelper(final int min, final int max, final int val) {
        tri(new CodeBlock<Integer>() {
            @Override
            public Integer f() {
                logger.info("min=" + min + ", max=" + max + ", val=" + val);
                new RangeSetting("abc", "def", min, max, val);
                fail("should raise ValueNotInRange Fault");
                return null;
            }
        }).ignore(SettingsFaultCodes.ValueNotInRange).finale();
    }
    
    @Test
    public void maxLessThanMin() {
        tri(new CodeBlock<Integer>() {
            @Override
            public Integer f() {
                new RangeSetting("abc", "desc", 3, 2, 5);
                fail("should raise MaxLessThanMin Fault");
                return null;
            }
        }).ignore(SettingsFaultCodes.MaxLessThanMin).finale();
    }
    
    @Test
    public void stringIsNotInteger() {
        stringIsNotIntegerHelper("hello");
        stringIsNotIntegerHelper("123a");
        stringIsNotIntegerHelper("a123");
        stringIsNotIntegerHelper("123 a");
        stringIsNotIntegerHelper("123.455");
    }

    public void stringIsNotIntegerHelper(final String value) {
        RangeSetting rc = new RangeSetting("abc", "desc", 2, 5, 3);
        List<Fault> errors = new ArrayList<>();
        rc.validate(errors, value);
        assertEquals(1, errors.size());
        Fault f = errors.get(0);
        assertEquals(SettingsFaultCodes.ValueIsNotInteger, f.getCode());
    }

    @Test
    public void valueNotInRange_validate() {
        valueNotInRangeHelper("1");
        valueNotInRangeHelper("6");
    }

    public void valueNotInRangeHelper(final String value) {
        RangeSetting rc = new RangeSetting("abc", "desc", 2, 5, 3);
        List<Fault> errors = new ArrayList<>();
        rc.validate(errors, value);
        assertEquals(1, errors.size());
        Fault f = errors.get(0);
        assertEquals(SettingsFaultCodes.ValueNotInRange, f.getCode());
    }

    @Test
    public void valueNotInRange_validate_ok() {
        valueNotInRangeOKHelper("2");
        valueNotInRangeOKHelper("5");
        valueNotInRangeOKHelper("4");
    }

    public void valueNotInRangeOKHelper(final String value) {
        RangeSetting rc = new RangeSetting("abc", "desc", 2, 5, 3);
        List<Fault> errors = new ArrayList<>();
        rc.validate(errors, value);
        assertEquals(0, errors.size());
    }
}
