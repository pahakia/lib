package pahakia.fault.java7;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class AllAnnotationsFinderTest {

    @Test
    public void findAllAnnotations() throws IOException, ClassNotFoundException {
        Properties properties = System.getProperties();
        Set<String> keys = properties.stringPropertyNames();
        List<String> names = new ArrayList<>(keys);
        Collections.sort(names);
        String extDirsString = properties.getProperty("java.ext.dirs");
        String bootJarsString = properties.getProperty("sun.boot.class.path");
        String javaHome = properties.getProperty("java.home");
        String[] extDirs = extDirsString.split(":");
        String[] bootJars = bootJarsString.split(":");
        List<File> list = new ArrayList<>();
        for (String path : bootJars) {
            list.add(new File(path));
        }
        for (String path : extDirs) {
            System.out.println("find jars in dir: " + path);
            list.addAll(findJars(path));
        }
        List<File> dirs = findDirs(javaHome);
        for (File path : dirs) {
            list.addAll(findJars(path.getAbsolutePath()));
        }
        Collections.sort(list, new Comparator<File>() {

            @Override
            public int compare(File o1, File o2) {
                return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
            }
        });
        List<String> annotations = getAnnotationClasses(list);
        printExceptions("annotation classes:", annotations);
    }

    private void printExceptions(String msg, List<String> annotations) {
        System.out.println(msg + " total=" + annotations.size());
        for (String name : annotations) {
            System.out.println("    " + name);
        }
    }

    private List<String> getAnnotationClasses(List<File> list) throws ClassNotFoundException, IOException {
        List<String> classes = new ArrayList<>();
        for (File file : list) {
            if (!file.isFile()) {
                // System.out.println("skip: " + file.getAbsolutePath());
                continue;
            }
            // System.out.println(file.getAbsolutePath());
            try (JarFile jf = new JarFile(file);) {

                Enumeration<JarEntry> en = jf.entries();
                while (en.hasMoreElements()) {
                    JarEntry je = en.nextElement();
                    // System.out.println("    " + je.getName());
                    String name = je.getName();
                    if (name.endsWith(".class") && !name.contains("$")) {
                        name = name.substring(0, name.length() - 6).replace('/', '.');
                        try {
                            Class<?> clz = this.getClass().getClassLoader().loadClass(name);
                            if (clz.isAnnotation()) {
                                // System.out.println("    " + clz.getName());
                                classes.add(clz.getName());
                            }
                        } catch (NoClassDefFoundError NCDFE) {
                            // System.out.println("       NoClassDefFoundError: " + name);
                        } catch (ClassNotFoundException CNFE) {
                            // System.out.println("       ClassNotFoundException: " + name);
                        } catch (UnsatisfiedLinkError ule) {
                            // System.out.println("       UnsatisfiedLinkError: " + name);
                        }
                    }
                }
            }
        }
        Collections.sort(classes);
        return classes;
    }

    private List<File> findJars(String path) {
        File dir = new File(path);

        File[] matches = dir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.matches(".*\\.(jar|zip)");
            }
        });
        if (matches != null) {
            return Arrays.<File> asList(matches);
        }
        return new ArrayList<File>();
    }

    private List<File> findDirs(String path) {
        File dir = new File(path);

        File[] matches = dir.listFiles(new FileFilter() {
            public boolean accept(File dir) {
                String name = dir.getName();
                return dir.isDirectory() && !name.equals(".") && name.equals("..") && name.endsWith("/.")
                        && name.endsWith("/..");
            }
        });
        return Arrays.<File> asList(matches);
    }
}
