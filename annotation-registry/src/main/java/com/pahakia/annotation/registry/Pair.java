package com.pahakia.annotation.registry;

import pahakia.fault.Fault;

public class Pair {
    public String className;
    public ClassLoader classLoader;

    public Pair(String className, ClassLoader classLoader) {
        super();
        this.className = className;
        this.classLoader = classLoader;
    }

    public Class<?> loadClass() {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException ex) {
            throw Fault.naturalize(ex);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Pair)) {
            return false;
        }
        Pair pair = (Pair) obj;
        return className.equals(pair.className) && classLoader.equals(pair.classLoader);
    }

    @Override
    public int hashCode() {
        return className.hashCode() + classLoader.hashCode();
    }

    @Override
    public String toString() {
        return "class: " + className + ", class loader: " + classLoader;
    }

}
