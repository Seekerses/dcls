package org.spt.util;

import org.spt.controller.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    public static void initDir(Path pathToDir) throws IOException {
        if (!Files.exists(pathToDir)){
            Files.createDirectory(pathToDir);
        }
    }

    public static String sourceFileToClassName(Path sourceFile) {
        return sourceFile.toString()
                .replace(Configuration.getInstance().getSourceDir() + "/", "")
                .replace("/", ".")
                .replace(".class", "");
    }

    public static String compiledFileToClassName(Path compiledFile) throws IOException {
        return compiledFile.toString()
                .replace(Configuration.getInstance().getTempDir() + "/classes/", "")
                .replace("/", ".")
                .replace(".class", "");
    }

    public static Path sourceFileToCompiledFile(Path sourceFile){
        String compiledFileName = sourceFile.toString()
                .replace(Configuration.getInstance().getSourceDir() + "/", "")
                .replace(".java", ".class");
        return Paths.get(Configuration.getInstance().getTempDir() + "/classes/" + compiledFileName);
    }
}
