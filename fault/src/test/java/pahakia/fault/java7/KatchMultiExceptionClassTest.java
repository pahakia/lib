package pahakia.fault.java7;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

import pahakia.fault.CodeBlock;
import pahakia.fault.Fault;
import pahakia.fault.FinaleBlock;
import pahakia.fault.KatchBlock;

public class KatchMultiExceptionClassTest {
    @Test
    public void test1() {
        int num = string2Int("abc");
        assertEquals(100, num);
    }

    @Test
    public void test2() {
        int num = string2Int(null);
        assertEquals(200, num);
    }

    @Test
    public void test3() {
        int num = string2Int("301");
        assertEquals(301, num);
    }

    private int string2Int(final String str) {
        @SuppressWarnings("unchecked")
        int tmp = Fault.tri(new CodeBlock<Integer>() {

            @Override
            public Integer f() {
                str.length();
                return Integer.valueOf(str);
            }
        }).katch(new KatchBlock<Integer>() {

            @Override
            public Integer f(Fault ex) {
                System.out.println("processing NumberFormatException/NullPointerException: " + ex);
                if (ex.getCause() instanceof NullPointerException) {
                    return 200;
                }
                return 100;
            }
        }, NumberFormatException.class, NullPointerException.class).finale(new FinaleBlock() {

            @Override
            public void f() {
                System.out.println("hello");
            }
        });
        System.out.println(tmp);
        return tmp;
    }
}
