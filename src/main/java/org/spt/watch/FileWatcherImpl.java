package org.spt.watch;

import com.sun.nio.file.ExtendedWatchEventModifier;
import lombok.extern.slf4j.Slf4j;
import org.spt.controller.RedefinitionController;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Slf4j
public class FileWatcherImpl implements FileWatcher{

    private WatchService watchService;
    private WatchKey watchKey;
    private String sourceDir;
    private Map<WatchKey, Path> keys = new HashMap<>();
    private final RedefinitionController redefinitionController;

    public FileWatcherImpl(String sourceDir, RedefinitionController redefinitionController) throws IOException {
        this.sourceDir = sourceDir;
        this.redefinitionController = redefinitionController;
        log.info("Registering new File Watcher to source directory: {}", sourceDir);
        watchService = FileSystems.getDefault().newWatchService();
        Path root = Paths.get(sourceDir);
        registerRecursive(root);
    }

    @Override
    public void watch() throws InterruptedException {
        while ((watchKey = watchService.take()) != null) {
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                if(event.context().toString().endsWith(".java")) {
                    log.debug("Detected modification: type {}, file {}", event.kind(), event.context());
                    redefinitionController.processChanges(new File(keys.get(watchKey) + "/" + event.context()), event.kind());
                }
            }
            watchKey.reset();
        }
    }

    private void registerRecursive(final Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.info("Registered path: {}", dir.toAbsolutePath());
                keys.put(dir.register(watchService, ENTRY_MODIFY), dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
