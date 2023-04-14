package org.spt.controller;

import org.spt.compiler.Compiler;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CompilerStub implements Compiler {

    public List<File> mem = new ArrayList<>();

    @Override
    public File compile(File sourceFile) {
        mem.add(sourceFile);
        return Paths.get(sourceFile.getAbsolutePath().replace("./src/test/java", "./tmp")
                .replace(".java", ".class")).toFile();
    }
}
