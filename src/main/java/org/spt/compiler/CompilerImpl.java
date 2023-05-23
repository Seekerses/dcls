package org.spt.compiler;

import lombok.extern.slf4j.Slf4j;
import org.spt.controller.Configuration;
import org.spt.util.FileUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class CompilerImpl implements Compiler{

    private final String COMPILER_COMMAND = "javac -d %s/classes -cp %s -target %s -source %s %s";
    private final String CREATE_JAR_COMMAND = "jar -cf %s/tmp.jar -C %s/classes .";
    private final String UPDATE_JAR_COMMAND = "jar -uf %s/tmp.jar -C %s/classes .";
    private final DependencyManager dependencyManager;

    public CompilerImpl() throws IOException {
        FileUtil.initDir(Configuration.getInstance().getTempDir());
        FileUtil.initDir(Paths.get( Configuration.getInstance().getTempDir() + "/classes"));
        dependencyManager = new DependencyManagerImpl();
        if (Configuration.getInstance().getLibDir() != null) dependencyManager
                .addDependency(Configuration.getInstance().getLibDir());
        dependencyManager.addDependency(Paths.get(Configuration.getInstance().getTempDir() + "/tmp.jar"));
        try {
            createTempJar();
        } catch (Exception e) {
            log.error("Failed to create temp jar file. Agent will fail to load new classes.");
        }
    }

    @Override
    public Path compile(Path sourceFile) throws Exception {
        log.debug("Starting compilation: {}", sourceFile);
        compileFile(sourceFile);
        log.debug("Updating temp jar.");
        updateTempJar();
        Path compiledFile = FileUtil.sourceFileToCompiledFile(sourceFile);
        if (!Files.exists(compiledFile)){
            log.error("Compiled file {} not found", compiledFile);
            throw new FileNotFoundException("Compiled file not found");
        }
        return compiledFile;
    }

    private void createTempJar() throws Exception {
        String createJarTemp = String.format(CREATE_JAR_COMMAND,
                Configuration.getInstance().getTempDir(),
                Configuration.getInstance().getTempDir());
        execProcess(createJarTemp);
    }

    private void compileFile(Path sourceFile) throws Exception {
        String compilingCommand = String.format(COMPILER_COMMAND,
                Configuration.getInstance().getTempDir(),
                dependencyManager.getDependencies(),
                Configuration.getInstance().getTargetVersion().getVersion(),
                Configuration.getInstance().getSourceVersion().getVersion(),
                sourceFile);
        execProcess(compilingCommand);
    }

    private void updateTempJar() throws Exception {
        String packagingCommand = String.format(UPDATE_JAR_COMMAND,
                Configuration.getInstance().getTempDir(),
                Configuration.getInstance().getTempDir());
        execProcess(packagingCommand);
    }

    private void execProcess(String command) throws Exception {
        log.debug("Command: {}", command);
        Process process = Runtime.getRuntime().exec(command);
        printProcessOut("stdout:", process.getInputStream());
        printProcessOut("stderr:", process.getErrorStream());
        process.waitFor();
        log.debug("Process completed with exit code {}", process.exitValue());
    }

    private void printProcessOut(String cmd, InputStream ins) throws Exception {
        String line;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            log.info(cmd + " " + line);
        }
    }
}
