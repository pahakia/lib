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
public class AllExceptionsFinderTest {

    @Test
    public void findAllExceptions() throws IOException, ClassNotFoundException {
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
        List<String> errors = getExceptionClasses(list, Error.class);
        printExceptions("classes extends Error:", errors);
        List<String> exceptions = getExceptionClasses(list, Exception.class);
        printExceptions("classes extends Exception:", exceptions);
        List<String> runtimeExceptions = getExceptionClasses(list, RuntimeException.class);
        printExceptions("classes extends RuntimeException:", runtimeExceptions);
    }

    private void printExceptions(String msg, List<String> exceptions) {
        System.out.println(msg + " total=" + exceptions.size());
        for (String name : exceptions) {
            int pos = name.lastIndexOf(".");
            String simpleName = name.substring(pos + 1);
            String varName = simpleName + "_" + name.substring(0, pos).replaceAll("[.]", "_");
            System.out.println("public static final FaultCode " + varName + " = new FaultCode(\"" + name
                    + "\", 1, \"{0}\");");
        }
    }
    private List<String> getExceptionClasses(List<File> list, Class<? extends Throwable> class1)
            throws ClassNotFoundException, IOException {
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
                            if (class1.isAssignableFrom(clz)) {
                                // System.out.println("    " + clz.getName());
                                Class<?> superclass = clz.getSuperclass();
                                if (superclass.equals(class1)) {
                                    classes.add(clz.getName());
                                } else {
                                    classes.add(clz.getName());
                                }
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
// @formatter:off
/*
 * awt.toolkit=sun.lwawt.macosx.LWCToolkit file.encoding=UTF-8 file.encoding.pkg=sun.io file.separator=/
 * gopherProxySet=false java.awt.graphicsenv=sun.awt.CGraphicsEnvironment
 * java.awt.printerjob=sun.lwawt.macosx.CPrinterJob
 * java.class.path=/Users/bozou/git/lib/exception/target/test-classes:/Users
 * /bozou/git/lib/exception/target/classes:/Users
 * /bozou/.m2/repository/junit/junit/4.11/junit-4.11.jar:/Users/bozou/.m2/repository
 * /org/hamcrest/hamcrest-core/1.3/hamcrest
 * -core-1.3.jar:/Users/bozou/Downloads/eclipseluna/configuration/org.eclipse.osgi
 * /203/0/.cp/:/Users/bozou/Downloads/eclipseluna/configuration/org.eclipse.osgi/202/0/.cp/ java.class.version=51.0
 * java.endorsed.dirs=/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/endorsed
 * java.ext.dirs=/Users
 * /bozou/Library/Java/Extensions:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre
 * /lib/ext:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java
 * java.home=/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre
 * java.io.tmpdir=/var/folders/sj/0vvwcqqs4y10m3k34fpfqxrc0000gn/T/
 * java.library.path=/Users/bozou/Library/Java/Extensions
 * :/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.
 * java.runtime.name=Java(TM) SE Runtime Environment java.runtime.version=1.7.0_51-b13 java.specification.name=Java
 * Platform API Specification java.specification.vendor=Oracle Corporation java.specification.version=1.7
 * java.vendor=Oracle Corporation java.vendor.url=http://java.oracle.com/
 * java.vendor.url.bug=http://bugreport.sun.com/bugreport/ java.version=1.7.0_51 java.vm.info=mixed mode
 * java.vm.name=Java HotSpot(TM) 64-Bit Server VM java.vm.specification.name=Java Virtual Machine Specification
 * java.vm.specification.vendor=Oracle Corporation java.vm.specification.version=1.7 java.vm.vendor=Oracle Corporation
 * java.vm.version=24.51-b03 line.separator=
 * 
 * os.arch=x86_64 os.name=Mac OS X os.version=10.9.5 path.separator=: sun.arch.data.model=64
 * sun.boot.class.path=/Library
 * /Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java
 * /JavaVirtualMachines/jdk1.7
 * .0_51.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents
 * /Home/jre/lib/sunrsasign
 * .jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/jsse.jar:/Library
 * /Java/JavaVirtualMachines
 * /jdk1.7.0_51.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.
 * jdk/Contents/Home/jre/lib
 * /charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib/jfr.jar
 * :/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/classes
 * sun.boot.library.path=/Library/Java/JavaVirtualMachines/jdk1.7.0_51.jdk/Contents/Home/jre/lib sun.cpu.endian=little
 * sun.cpu.isalist= sun.io.unicode.encoding=UnicodeBig
 * sun.java.command=org.eclipse.jdt.internal.junit.runner.RemoteTestRunner -version 3 -port 62816 -testLoaderClass
 * org.eclipse.jdt.internal.junit4.runner.JUnit4TestLoader -loaderpluginname org.eclipse.jdt.junit4.runtime -classNames
 * pahakia.fault.AllExceptionsFinder sun.java.launcher=SUN_STANDARD sun.jnu.encoding=UTF-8
 * sun.management.compiler=HotSpot 64-Bit Tiered Compilers sun.os.patch.level=unknown user.country=US
 * user.dir=/Users/bozou/git/lib/exception user.home=/Users/bozou user.language=en user.name=bozou user.timezone=
 */

// @formatter:on

