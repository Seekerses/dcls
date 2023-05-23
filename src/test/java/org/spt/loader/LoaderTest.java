package org.spt.loader;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class LoaderTest {

    @Test
    public void loadClass() throws IOException {
//        Files.createDirectories(Paths.get("./tmp/org/spt/util"));
//        Path path = Paths.get("./src/test/java/org/spt/util/MockClass.class");
//        Path path1 = Paths.get("./tmp/org/spt/util/MockClass.class");
//        Files.copy(path, path1);
//        LoaderImpl loader = new LoaderImpl();
//        loader.load(path1.toFile());
//        assertDoesNotThrow(() -> Class.forName("org.spt.util.MockClass", false, loader.getLoader()));
//        Files.deleteIfExists(path1);
//        Files.deleteIfExists(path1.getParent());
//        Files.deleteIfExists(path1.getParent().getParent());
//        Files.deleteIfExists(path1.getParent().getParent().getParent());
//        Files.deleteIfExists(path1.getParent().getParent().getParent().getParent());
    }
}
