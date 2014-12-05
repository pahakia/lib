package pahakia.settings;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;

import java.util.logging.Logger;

import org.junit.Test;

import pahakia.fault.CodeBlock;
import pahakia.fault.Fault;
import pahakia.fault.FaultCodes;
import pahakia.fault.KatchBlock;
import pahakia.settings.RangeSetting;

public class PropertyNameDescriptionTest {
    private static Logger logger = Logger.getLogger(RangeSettingTest.class.getName());

    @Test
    public void mandatory() {
        nameDescHelper(null, "hello", true);
        nameDescHelper("", "hello", true);
        nameDescHelper("hello", null, false);
        nameDescHelper("hello", "", false);
    }

    private void nameDescHelper(final String name, final String desc, final boolean testName) {
        Fault.tri(new CodeBlock<Integer>() {
            @Override
            public Integer f() {
                logger.info("name="+name + ", desc="+desc + ", testName="+ testName);
                new RangeSetting(name, desc, 2,5,3);
                fail("should raise Mandatory Fault");
                return null;
            }
        }).katch(FaultCodes.Mandatory, new KatchBlock<Integer>() {
            @Override
            public Integer f(Fault fault) {
                if (testName) {
                    assertEquals("first arg should be 'name'", "name", fault.getArgs()[0]);
                } else {
                    assertEquals("first arg should be 'description'", "description", fault.getArgs()[0]);
                }
                return null;
            }
        }).finale();
    }
    
}
