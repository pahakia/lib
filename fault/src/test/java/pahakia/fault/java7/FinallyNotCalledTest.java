package pahakia.fault.java7;

import org.junit.Test;

import pahakia.fault.CodeBlock;
import pahakia.fault.Fault;
import pahakia.fault.KatchBlock;

public class FinallyNotCalledTest {
    @Test
    public void test1() {
        for (int i = 0; i < 1; i++) {
            string2Int("abc");
        string2Int("abc");
        string2Int("abc");
        string2Int("abc");
        string2Int("abc");
        string2Int("abc");
        }
        System.gc();
    }

    private int string2Int(final String str) {
        Fault.tri(new CodeBlock<Integer>() {

            @Override
            public Integer f() {
                str.length();
                return Integer.valueOf(str);
            }
        }).katch(NumberFormatException.class.getName(), new KatchBlock<Integer>() {

            @Override
            public Integer f(Fault ex) {
                System.out.println("processing NumberFormatException: " + ex);
                return 100;
            }
        }).katch(".*NullPointer.*", new KatchBlock<Integer>() {

            @Override
            public Integer f(Fault ex) {
                System.out.println("processing .*NullPointer.*: " + ex);
                return 500;
            }
        });
        return 1;
    }
}
