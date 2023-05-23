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
import org.spt.util.FileUtil;
import org.spt.util.SpringUtil;
import org.spt.watch.FileWatcher;
import org.spt.watch.FileWatcherImpl;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Optional;
import java.util.jar.JarFile;

@Slf4j
public class AgentController implements Controller{
    private final FileWatcher fileWatcher;
    private final Compiler compiler;
    private final Redefiner redefiner;
    private final Loader loader;

    public AgentController(String args, Instrumentation instrumentation) throws IOException {
        Configuration.init(args);
        this.compiler = new CompilerImpl();
        this.redefiner = new RedefinerImpl(instrumentation);
        this.loader = new LoaderImpl(instrumentation, new JarFile(
                Configuration.getInstance().getTempDir() + "/tmp.jar"));
        this.fileWatcher = new FileWatcherImpl(this);
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
    public void processChanges(Path file, WatchEvent.Kind<?> eventKind){
        log.info("Start loading changes of {}", file);
        try {
            Path compiledFile = compileFile(file);
            if (StandardWatchEventKinds.ENTRY_MODIFY.equals(eventKind)) {
                redefineClass(compiledFile);
            } else if (StandardWatchEventKinds.ENTRY_CREATE.equals(eventKind)) {
                loadClass(compiledFile);
            } else {
                log.error("Not implemented event kind");
            }
        }catch (ChangesLoadingException ex){
            log.error("Ignore changes loading of {} due to {}", file.getFileName(), ex.getMessage());
        }
    }

    private void redefineClass(Path compiledFile){
        try {
            String className = FileUtil.compiledFileToClassName(compiledFile);
            log.debug("Redefining class {} from path {}", className, compiledFile);
            if(redefiner.redefineClass(getClassFromName(className), compiledFile))
                log.debug("Redefinition complete for class {}", className);
            else
                log.error("Redefinition failed for class {}", className);
        }catch (ClassNotFoundException | IOException ex){
            log.error("Class for redefinition was not found {}", compiledFile);
        }
    }

    private void loadClass(Path compiledFile){
        log.debug("Loading class from {}", compiledFile);
        if (loader.load(compiledFile))
            log.debug("Class {} successfully loaded.", compiledFile.getFileName());
        else
            log.error("Failed to load class {}", compiledFile.getFileName());
    }

    private Path compileFile(Path file) throws ChangesLoadingException {
        try {
            log.debug("Compiling source file {}", file);
            return compiler.compile(file);
        } catch (Exception ex){
            log.error("Failed to compile class {} with error {}", file, ex.getStackTrace());
            throw new ChangesLoadingException("Failed to compile file with message: " + ex.getMessage());
        }
    }

    private Class<?> getClassFromName(String className) throws ClassNotFoundException {
        Optional<LaunchedURLClassLoader> springLoader = SpringUtil.getSpringClassLoader();
        if (springLoader.isPresent()){
            return Class.forName(className, false, springLoader.get());
        }else {
            return Class.forName(className);
        }
    }
}
