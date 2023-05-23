package org.spt.redefiner;

import java.io.File;
import java.nio.file.Path;

public interface Redefiner {

    boolean redefineClass(Class<?> clazz, Path file);
}
