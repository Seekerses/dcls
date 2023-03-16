package org.spt.controller;

import lombok.extern.slf4j.Slf4j;
import org.spt.compiler.Compiler;
import org.spt.compiler.CompilerImpl;
import org.spt.loader.Loader;
import org.spt.loader.LoaderImpl;
import org.spt.redefiner.Redefiner;
import org.spt.redefiner.RedefinerImpl;
import org.spt.watch.FileWatcher;
import org.spt.watch.FileWatcherImpl;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Arrays;

@Slf4j
public class RedefinitionController {
    private final FileWatcher fileWatcher;
    private final Compiler compiler;
    private final Redefiner redefiner;
    private final Loader loader;
    private final String sourceDir;

    public RedefinitionController(String sourceDir, Instrumentation instrumentation) throws IOException {
        this.sourceDir = sourceDir;
        this.compiler = new CompilerImpl(sourceDir);
        this.redefiner = new RedefinerImpl(instrumentation);
        this.loader = new LoaderImpl();
        this.fileWatcher = new FileWatcherImpl(sourceDir,this);
        log.info("Redefinition controller initialized...");
    }

    public void processChanges(File file, WatchEvent.Kind<?> eventKind){
        log.info("Start loading changes of {}", file.getAbsolutePath());
        File compiledFile;
        try {
            log.debug("Compiling source file {}", file.getAbsolutePath());
            compiledFile = compiler.compile(file);
        } catch (Exception ex){
            log.error("Failed to compile class {} with error {}", file.getAbsolutePath(), ex.getStackTrace());
            return;
        }
        if (StandardWatchEventKinds.ENTRY_MODIFY.equals(eventKind)) {
            try {
                String className = file.getAbsolutePath()
                        .replace(Paths.get(sourceDir).toAbsolutePath() + "/", "")
                        .replace("/", ".")
                        .replace(".java", "");
                log.debug("Redefining class {} from path {}", className, compiledFile.getAbsolutePath());
                if(redefiner.redefineClass(Class.forName(className), compiledFile))
                    log.debug("Redefinition complete for class {}", className);
                else
                    log.error("Redefinition failed for class {}", className);
            }catch (ClassNotFoundException ex){
                log.error("Class for redefinition was not found {}", compiledFile.getName().replace(".class", ""));
            }
        } else if (StandardWatchEventKinds.ENTRY_CREATE.equals(eventKind)) {
            log.debug("Loading class from {}", compiledFile.getAbsolutePath());
            loader.load(compiledFile);
        } else{
            log.error("Not implemented event kind");
        }
    }

    public void start() throws InterruptedException {
        log.info("Sync started.");
        fileWatcher.watch();
    }

}
