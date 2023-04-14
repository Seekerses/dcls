package org.spt.redefiner;

import java.io.File;

public interface Redefiner {

    boolean redefineClass(Class<?> clazz, File file);
}
