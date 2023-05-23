package org.spt.watch;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileWatcherTest {

    @Test
    public void testFileModified() throws IOException, InterruptedException {
        Path srcDir = Files.createDirectory(Paths.get("./tmp"));
        Path file = Files.createFile(Paths.get(srcDir + "/file.java"));
        AgentControllerStub stub = new AgentControllerStub();
        FileWatcherImpl fileWatcher = new FileWatcherImpl(stub);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(500);
                Files.write(file, new byte[] {11, 11, 11});
                Thread.sleep(500);
                fileWatcher.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            fileWatcher.stop();
        });
        thread.start();
        fileWatcher.watch();
        assertEquals(1, stub.modifiedFiles.size());
        assertEquals(file.getFileName().toString(), stub.modifiedFiles.get(0).file.getName());
        assertEquals(StandardWatchEventKinds.ENTRY_MODIFY, stub.modifiedFiles.get(0).kind);
        Files.deleteIfExists(file);
        Files.deleteIfExists(srcDir);
    }

    @Test
    public void testDirCreated() throws IOException, InterruptedException {
        Path srcDir = Files.createDirectory(Paths.get("./tmp"));
        AgentControllerStub stub = new AgentControllerStub();
        FileWatcherImpl fileWatcher = new FileWatcherImpl(stub);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
                Files.createDirectory(Paths.get(srcDir.toAbsolutePath() + "/test"));
                Thread.sleep(100);
                Files.createFile(Paths.get(srcDir + "/test/file.java"));
                Thread.sleep(100);
                fileWatcher.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            fileWatcher.stop();
        });
        thread.start();
        fileWatcher.watch();
        assertEquals(1, stub.modifiedFiles.size());
        assertEquals("file.java", stub.modifiedFiles.get(0).file.getName());
        assertEquals(StandardWatchEventKinds.ENTRY_CREATE, stub.modifiedFiles.get(0).kind);
        Files.deleteIfExists(Paths.get(srcDir + "/test/file.java"));
        Files.deleteIfExists(Paths.get(srcDir + "/test"));
        Files.deleteIfExists(srcDir);
    }

    @Test
    public void testFileCreated() throws IOException, InterruptedException {
        Path srcDir = Files.createDirectory(Paths.get("./tmp"));
        AgentControllerStub stub = new AgentControllerStub();
        FileWatcherImpl fileWatcher = new FileWatcherImpl(stub);
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
                fileWatcher.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            fileWatcher.stop();
        });
        Path file = Files.createFile(Paths.get(srcDir + "/file.java"));
        thread.start();
        fileWatcher.watch();
        assertEquals(1, stub.modifiedFiles.size());
        assertEquals(file.getFileName().toString(), stub.modifiedFiles.get(0).file.getName());
        assertEquals(StandardWatchEventKinds.ENTRY_CREATE, stub.modifiedFiles.get(0).kind);
        Files.deleteIfExists(file);
        Files.deleteIfExists(srcDir);
    }
}
