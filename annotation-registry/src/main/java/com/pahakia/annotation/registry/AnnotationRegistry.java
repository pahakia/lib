package com.pahakia.annotation.registry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import pahakia.fault.Fault;

/**
 * Register all classes from META-INF/annotated-classes.
 */
public class AnnotationRegistry {

    private static final String DEST_FILE_NAME = "META-INF/annotated-classes";

    // annotation --> list of classes
    private static ConcurrentMap<String, Set<Pair>> map = new ConcurrentHashMap<>();

    public static void register(ClassLoader classLoader) {
        register(classLoader, DEST_FILE_NAME);
    }

    static void register(ClassLoader classLoader, String fileName) {
        try {
            Enumeration<URL> en = classLoader.getResources(fileName);
            while (en.hasMoreElements()) {
                URL url = en.nextElement();
                try (InputStream uis = url.openStream()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(uis));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        if (!line.trim().isEmpty()) {
                            String[] parts = line.split(":\\s*");
                            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                                throw Fault.create(AnnotationRegistryFaultCodes.InvalidEntry, url.toString(), line);
                            }
                            for (String ann : parts[1].split("\\s+")) {
                                if (!ann.trim().isEmpty()) {
                                    Set<Pair> classes = map.get(ann);
                                    if (classes == null) {
                                        classes = new HashSet<>();
                                        map.put(ann, classes);
                                    }
                                    classes.add(new Pair(parts[0], classLoader));
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Fault.naturalize(ex);
        }
    }

    public static Set<Pair> getAnnotatedClasses(String annotation) {
        return map.get(annotation);
    }

    static void testClear() {
        map.clear();
    }
}
