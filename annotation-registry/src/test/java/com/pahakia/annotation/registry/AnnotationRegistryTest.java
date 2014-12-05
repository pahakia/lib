package com.pahakia.annotation.registry;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import pahakia.fault.CodeBlock;
import pahakia.fault.Fault;
import pahakia.fault.KatchBlock;

/**
 * Register all classes from META-INF/annotated-classes.
 */
public class AnnotationRegistryTest {

    @Before
    public void setup() {
        AnnotationRegistry.testClear();
    }

    @Test
    public void test() {
        AnnotationRegistry.register(getClass().getClassLoader());
        Set<Pair> set = AnnotationRegistry.getAnnotatedClasses("com.pahakia.annotation.processor.test.annotation.Sport");
        assertNotNull(set);
        assertEquals("@Sport should have 2 entries", 2, set.size());

        Set<Pair> set2 = AnnotationRegistry.getAnnotatedClasses("com.pahakia.annotation.processor.test.annotation.Region");
        assertNotNull(set2);
        assertEquals("@Region should have 1 entries", 1, set2.size());

        Set<Pair> set3 = AnnotationRegistry.getAnnotatedClasses("java.lang.annotation.Target");
        assertNotNull(set3);
        assertEquals("@Target should have 2 entries", 2, set3.size());

        Set<Pair> set4 = AnnotationRegistry.getAnnotatedClasses("java.lang.annotation.Retention");
        assertNotNull(set4);
        assertEquals("@Retention should have 2 entries", 2, set4.size());
    }

    @Test
    public void negative_test() {
        Fault.tri(new CodeBlock<Void>() {
            @Override
            public Void f() {
                AnnotationRegistry.register(getClass().getClassLoader(), "META-INF/invalid-annotated-classes");
                fail("should fail invalid-annotate-classes");
                return null;
            }
        }).ignore(AnnotationRegistryFaultCodes.InvalidEntry).finale();
    }

    @Test
    public void negative_test2() {
        negative_test_helper("META-INF/invalid-annotated-classes", "com.pahakia.annotation.processor.test.Soccer: ");
    }

    @Test
    public void negative_test3() {
        negative_test_helper("META-INF/invalid-annotated-classes2", "com.pahakia.annotation.processor.test.Soccer");
    }

    private void negative_test_helper(final String fileName, final String arg2) {
        Fault.tri(new CodeBlock<Void>() {
            @Override
            public Void f() {
                AnnotationRegistry.register(getClass().getClassLoader(), fileName);
                fail("should fail invalid-annotate-classes");
                return null;
            }
        }).katch(AnnotationRegistryFaultCodes.InvalidEntry, new KatchBlock<Void>() {
            @Override
            public Void f(Fault ex) {
                assertEquals(AnnotationRegistryFaultCodes.InvalidEntry.getNumArgs(), ex.getArgs().length);
                assertTrue(ex.getArgs()[0] + " should end with " + fileName, ex.getArgs()[0].endsWith(fileName));
                assertEquals(arg2, ex.getArgs()[1]);
                return null;
            }
        }).finale();
    }
}
