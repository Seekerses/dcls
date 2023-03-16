package org.spt.compiler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.spt.util.FileUtil;

import java.io.*;
import java.nio.file.Paths;

@Slf4j
public class CompilerImpl implements Compiler{

    private final String COMPILER_COMMAND = "javac -d %s -cp %s -target %s -source %s %s";
    private final String TMP_DIR_PATH = "./tmp";
    @Getter
    @Setter
    private String MAVEN_DEPENDENCIES_REPOSITORY_DIR = "~/.m2/repository";
    @Getter
    @Setter
    private JavaVersion sourceVersion = JavaVersion.JAVA_8;
    @Getter
    @Setter
    private JavaVersion targetVersion = JavaVersion.JAVA_8;
    private String sourceDir;

    public CompilerImpl(String sourceDir) throws IOException {
        FileUtil.initDir(TMP_DIR_PATH);
        this.sourceDir = sourceDir;
    }

    @Override
    public File compile(File sourceFile) throws Exception {
        log.debug("Starting compilation: {}", sourceFile.getAbsoluteFile());
        String command = String.format(COMPILER_COMMAND,
                TMP_DIR_PATH,
                MAVEN_DEPENDENCIES_REPOSITORY_DIR,
                targetVersion.getVersion(),
                sourceVersion.getVersion(),
                sourceFile.getAbsoluteFile());
        log.debug("Command: {}", command);
        Process compileProcess = Runtime.getRuntime().exec(command);
        printJavacInfo("stdout:", compileProcess.getInputStream());
        printJavacInfo("stderr:", compileProcess.getErrorStream());
        compileProcess.waitFor();
        log.debug("Compilation complete for {} with exit code {}", sourceFile.getAbsoluteFile(), compileProcess.exitValue());
        return getCompiledFile(sourceFile);
    }

    private File getCompiledFile(File sourceFile) throws FileNotFoundException {
        String compiledFileName = sourceFile.getAbsolutePath()
                .replace(Paths.get(sourceDir).toAbsolutePath() + "/", "")
                .replace(".java", ".class");
        File compiledFile =  new File(TMP_DIR_PATH + "/" + compiledFileName);
        if (!compiledFile.exists()){
            log.error("Compiled file {} not found", compiledFileName);
            throw new FileNotFoundException("Compiled file not found");
        }
        return compiledFile;
    }

    private void printJavacInfo(String cmd, InputStream ins) throws Exception {
        String line;
        BufferedReader in = new BufferedReader(
                new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            log.debug(cmd + " " + line);
        }
    }
}
