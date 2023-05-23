package org.spt.loader;

import java.io.File;
import java.nio.file.Path;

public interface Loader {
    boolean load(Path file);
}
