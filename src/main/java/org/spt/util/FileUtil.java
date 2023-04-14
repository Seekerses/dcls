package org.spt.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static void initDir(String pathToDir) throws IOException {
        Path path = Paths.get(pathToDir);
        if (!Files.exists(path)){
            Files.createDirectory(path);
        }
        new File(pathToDir);
    }
}
