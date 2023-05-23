package org.spt.compiler;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompilerTest {

    @Test
    public void compileClass() throws Exception {
        CompilerImpl compiler = new CompilerImpl();
        assertEquals("MockClass.class",
                compiler.compile(Paths.get("./src/test/java/org/spt/util/MockClass.java").toFile()).getName());
        Path bottom = Paths.get("./tmp/org/spt/util/MockClass.class");
        Files.deleteIfExists(bottom);
        Files.deleteIfExists(bottom.getParent());
        Files.deleteIfExists(bottom.getParent().getParent());
        Files.deleteIfExists(bottom.getParent().getParent().getParent());
        Files.deleteIfExists(bottom.getParent().getParent().getParent().getParent());
    }
}
