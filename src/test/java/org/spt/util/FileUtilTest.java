package org.spt.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtilTest {

    @Test
    public void createDirTest() throws IOException {
        Path tmp = Paths.get("./tmp");
        FileUtil.initDir(String.valueOf(tmp));
        Assertions.assertTrue(Files.exists(tmp));
        Files.deleteIfExists(tmp);
    }

    @Test
    public void checkDirExists() throws IOException {
        Path tmp = Paths.get("./tmp");
        Files.createDirectory(tmp);
        FileUtil.initDir(String.valueOf(tmp));
        Assertions.assertTrue(Files.exists(tmp));
        Files.deleteIfExists(tmp);
    }
}
