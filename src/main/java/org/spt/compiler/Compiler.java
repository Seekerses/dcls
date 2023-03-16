package org.spt.compiler;

import java.io.File;
import java.io.IOException;

public interface Compiler {

    File compile(File sourceFile) throws Exception;
}
