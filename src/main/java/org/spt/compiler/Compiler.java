package org.spt.compiler;

import java.io.File;
import java.nio.file.Path;

public interface Compiler {
    Path compile(Path sourceFile) throws Exception;
}
