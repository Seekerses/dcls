package org.spt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.spt.compiler.Compiler;
import org.spt.compiler.CompilerImpl;
import org.spt.controller.exceptions.ChangesLoadingException;
import org.spt.loader.Loader;
import org.spt.loader.LoaderImpl;
import org.spt.redefiner.Redefiner;
import org.spt.redefiner.RedefinerImpl;
import org.spt.util.SpringUtil;
import org.spt.watch.FileWatcher;
import org.spt.watch.FileWatcherImpl;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Optional;

@Slf4j
public class AgentController implements Controller{
    private final FileWatcher fileWatcher;
    private final Compiler compiler;
    private final Redefiner redefiner;
    private final Loader loader;

    public AgentController(String sourceDir, Instrumentation instrumentation) throws IOException {
        this.compiler = new CompilerImpl(sourceDir);
        this.redefiner = new RedefinerImpl(instrumentation);
        this.loader = new LoaderImpl();
        this.fileWatcher = new FileWatcherImpl(sourceDir,this);
        log.info("Redefinition controller initialized...");
    }

    public AgentController(Compiler compiler, Redefiner redefiner, Loader loader, FileWatcher fileWatcher) {
        this.compiler = compiler;
        this.redefiner = redefiner;
        this.loader = loader;
        this.fileWatcher = fileWatcher;
        log.info("Redefinition controller initialized...");
    }

    public void start() throws InterruptedException {
        log.info("Sync started.");
        fileWatcher.watch();
    }

    @Override
    public void processChanges(File file, WatchEvent.Kind<?> eventKind){
        log.info("Start loading changes of {}", file.getAbsolutePath());
        try {
            File compiledFile = compileFile(file);
            if (StandardWatchEventKinds.ENTRY_MODIFY.equals(eventKind)) {
                redefineClass(compiledFile);
            } else if (StandardWatchEventKinds.ENTRY_CREATE.equals(eventKind)) {
                loadClass(compiledFile);
            } else {
                log.error("Not implemented event kind");
            }
        }catch (ChangesLoadingException ex){
            log.error("Ignore changes loading of {} due to {}", file.getName(), ex.getMessage());
        }
    }

    private void redefineClass(File compiledFile){
        try {
            String className = convertCompiledFileAbsolutePathToClassName(compiledFile);
            log.debug("Redefining class {} from path {}", className, compiledFile.getAbsolutePath());
            if(redefiner.redefineClass(getClassFromName(className), compiledFile))
                log.debug("Redefinition complete for class {}", className);
            else
                log.error("Redefinition failed for class {}", className);
        }catch (ClassNotFoundException ex){
            log.error("Class for redefinition was not found {}", compiledFile.getName().replace(".class", ""));
        }
    }

    private void loadClass(File compiledFile){
        log.debug("Loading class from {}", compiledFile.getAbsolutePath());
        if (loader.load(compiledFile))
            log.debug("Class {} successfully loaded.", compiledFile.getName());
        else
            log.error("Failed to load class {}", compiledFile.getName());
    }

    private File compileFile(File file) throws ChangesLoadingException {
        try {
            log.debug("Compiling source file {}", file.getAbsolutePath());
            return compiler.compile(file);
        } catch (Exception ex){
            log.error("Failed to compile class {} with error {}", file.getAbsolutePath(), ex.getStackTrace());
            throw new ChangesLoadingException("Failed to compile file with message: " + ex.getMessage());
        }
    }

    private String convertCompiledFileAbsolutePathToClassName(File file){
        return file.getAbsolutePath()
                .replace(Paths.get("./tmp").toAbsolutePath() + "/", "")
                .replace("/", ".")
                .replace(".class", "");
    }

    private Class<?> getClassFromName(String className) throws ClassNotFoundException {
        Optional<LaunchedURLClassLoader> springLoader = SpringUtil.getSpringClassLoader();
        if (springLoader.isPresent()){
            return Class.forName(className, false, springLoader.get());
        }else {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException ex) {
                return Class.forName(className, false, loader.getLoader());
            }
        }
    }
}
