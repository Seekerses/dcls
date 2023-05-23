package org.spt.watch;

import lombok.extern.slf4j.Slf4j;
import org.spt.controller.Configuration;
import org.spt.controller.Controller;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Slf4j
public class FileWatcherImpl implements FileWatcher{

    private final WatchService watchService;
    private final Map<WatchKey, Path> keys = new HashMap<>();
    private final Controller redefinitionController;
    private boolean running = false;
    private final Map<Path, FileTime> lastModifiedMap = new HashMap<>();

    public FileWatcherImpl(Controller redefinitionController) throws IOException {
        this.redefinitionController = redefinitionController;
        log.info("Registering new File Watcher to source directory: {}", Configuration.getInstance().getSourceDir());
        watchService = FileSystems.getDefault().newWatchService();
        Path root = Configuration.getInstance().getSourceDir();
        registerRecursive(root);
    }

    @Override
    public void watch() {
        running = true;
        while (running) {
            WatchKey watchKey = watchService.poll();
            if (watchKey == null) continue;
            for (WatchEvent<?> event : watchKey.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path targetPath = getEventTargetFile(watchKey, event);

                // prevent temp files
                if(targetPath.getFileName().toString().endsWith("~") ||
                targetPath.getFileName().toString().endsWith(".swp")){
                    watchKey.reset();
                    continue;
                }

                // prevent doubling events
                try {
                    FileTime lastTime = Files.getLastModifiedTime(targetPath);
                    FileTime prevMod = lastModifiedMap.get(targetPath);
                    if (prevMod != null && lastTime.toMillis() - prevMod.toMillis() < 50) {
                            watchKey.reset();
                            continue;
                    }
                    lastModifiedMap.put(targetPath, lastTime);
                } catch (IOException e) {
                    log.error("I/O exception occurred when reading last modified time for {}",
                            targetPath);
                }

                // handle event based on type and file
                if (ENTRY_CREATE.equals(kind)) {
                    if (targetPath.getFileName().toString().endsWith(".java")){
                        log.debug("Detected new Java-file: file {}", event.context());
                        redefinitionController.processChanges(targetPath, kind);
                    } else if (Files.isDirectory(targetPath)){
                        try {
                            log.info("Register new path: {}", targetPath);
                            keys.put(targetPath.register(watchService, ENTRY_CREATE, ENTRY_MODIFY), targetPath);
                        }catch (IOException ex){
                            log.error("Failed to register watch service on {}", targetPath);
                        }
                    }

                } else if (ENTRY_MODIFY.equals(kind)) {

                    if (targetPath.getFileName().toString().endsWith(".java")) {
                        log.debug("Detected modification: type {}, file {}", kind, event.context());
                        redefinitionController.processChanges(targetPath, kind);
                    }

                }
            }
            watchKey.reset();
        }
    }

    public void stop(){
        running = false;
    }

    private Path getEventTargetFile(WatchKey key, WatchEvent<?> event){
        if (event.context() == null || event.context().toString().equals("")){
            return keys.get(key);
        }
        return Paths.get(keys.get(key) + "/" + event.context().toString());
    }

    private void registerRecursive(final Path root) throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                log.info("Registered path: {}", dir.toAbsolutePath());
                keys.put(dir.register(watchService, ENTRY_CREATE,  ENTRY_MODIFY), dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
