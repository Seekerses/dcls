package org.spt.compiler;

import java.io.File;

public interface Compiler {
    File compile(File sourceFile) throws Exception;
}
