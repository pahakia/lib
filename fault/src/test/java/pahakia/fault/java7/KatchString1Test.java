package pahakia.fault.java7;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

import pahakia.fault.CodeBlock;
import pahakia.fault.Fault;
import pahakia.fault.FaultCode;
import pahakia.fault.FinaleBlock;
import pahakia.fault.KatchBlock;

public class KatchString1Test {
    @Test
    public void test1() {
        int num = string2Int("abc");
        assertEquals(100, num);
    }

    @Test(expected = Fault.class)
    public void test2() {
        string2Int(null);
    }

    @Test
    public void test3() {
        int num = string2Int("301");
        assertEquals(301, num);
    }

    @Test
    public void test4() {
        int num = string2Int("throw");
        assertEquals(700, num);
    }

    private int string2Int(final String str) {
        int tmp = Fault.tri(new CodeBlock<Integer>() {

            @Override
            public Integer f() {
                if (str.equals("throw")) {
                    throw Fault.create(new FaultCode("my.test.code", 0, "my test code"));
                }
                return Integer.valueOf(str);
            }
        }).katch(NumberFormatException.class.getName(), new KatchBlock<Integer>() {

            @Override
            public Integer f(Fault ex) {
                System.out.println("processing NumberFormatException: " + ex);
                return 100;
            }
        }).katch("my.test.code", new KatchBlock<Integer>() {

            @Override
            public Integer f(Fault ex) {
                System.out.println("processing NumberFormatException: " + ex);
                return 700;
            }
        }).finale(new FinaleBlock() {

            @Override
            public void f() {
                System.out.println("hello");
            }
        });
        System.out.println(tmp);
        return tmp;
    }
}
