package org.spt.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.spt.compiler.JavaVersion;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class Configuration {

    private static Configuration singleton;

    @Getter
    @Setter
    private boolean springPresent = false;
    @Getter
    @Setter
    private JavaVersion sourceVersion = JavaVersion.JAVA_8;
    @Getter
    @Setter
    private JavaVersion targetVersion = JavaVersion.JAVA_8;
    @Getter
    private Path sourceDir;
    @Getter
    private Path tempDir;
    @Getter
    private Path libDir;

    private Configuration(){
        try {
            this.tempDir = Paths.get("./tmp").toRealPath();
        } catch (IOException e) {
            log.error("Failed to find tmp directory.");
            throw new RuntimeException(e);
        }
    }

    protected static void init(String args){
        singleton = new Configuration();
        setupConfiguration(args);
    }

    public static Configuration getInstance(){
        if(singleton == null){
            throw new IllegalStateException("Instance call before initialization");
        }
        return singleton;
    }

    public void setSourceDir(String pathToSrc){
        try {
            sourceDir = Paths.get(pathToSrc).toRealPath();
        } catch (IOException e) {
            log.error("Failed to set src directory.");
            throw new RuntimeException(e);
        }
    }

    public void setLibDir(String pathToLib){
        try {
            libDir = Paths.get(pathToLib).toRealPath();
        } catch (IOException e) {
            log.error("Failed to set lib directory.");
            throw new RuntimeException(e);
        }
    }

    public void setTempDir(String pathToTmp){
        try {
            tempDir = Paths.get(pathToTmp).toRealPath();
        } catch (IOException e) {
            log.error("Failed to set tmp directory.");
            throw new RuntimeException(e);
        }
    }

    private static void setupConfiguration(String args){
        Map<String, String> params = splitArgs(args);
        String sourceDir = params.get("src");
        String libDir = params.get("lib");
        String sourceVersion = params.get("sv");
        String targetVersion = params.get("tv");
        String tmpDir = params.get("tmp");
        String springPresent = params.get("spring");
        if (sourceDir == null) throw new IllegalArgumentException("Source directory not specified");
        Configuration.getInstance().setSourceDir(sourceDir);
        if (libDir != null) Configuration.getInstance().setLibDir(libDir);
        if (sourceVersion != null) Configuration.getInstance().setSourceVersion(JavaVersion.valueOf(sourceVersion));
        if (targetVersion != null) Configuration.getInstance().setSourceVersion(JavaVersion.valueOf(targetVersion));
        if (tmpDir != null) Configuration.getInstance().setTempDir(tmpDir);
        if (springPresent != null) Configuration.getInstance().setSpringPresent(Boolean.parseBoolean(springPresent));
    }

    private static Map<String, String> splitArgs(String args){
        return Arrays.stream(args.split(":"))
                .collect(Collectors.toMap((v -> v.split("=")[0]), (v -> v.split("=")[1])));
    }
}
