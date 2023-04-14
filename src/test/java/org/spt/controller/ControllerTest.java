package org.spt.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.spt.util.MockClass;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class ControllerTest {

    AgentController controller;
    Path path;

    CompilerStub compilerStub;
    RedefinerStub redefinerStub;
    LoaderStub loaderStub;
    FileWatcherStub fileWatcherStub;

    @BeforeEach
    public void init(){
        compilerStub = new CompilerStub();
        redefinerStub = new RedefinerStub();
        loaderStub = new LoaderStub();
        fileWatcherStub = new FileWatcherStub();
        controller = new AgentController(compilerStub,
                redefinerStub,
                loaderStub,
                fileWatcherStub);
        path = Paths.get("./src/test/java/org/spt/util/MockClass.java");
    }

    @Test
    public void testFileModified(){
        controller.processChanges(path.toFile(), StandardWatchEventKinds.ENTRY_MODIFY);
        assertEquals(1, compilerStub.mem.size());
        assertEquals(1, redefinerStub.map.size());
        assertEquals(path.toAbsolutePath().toString(), compilerStub.mem.get(0).getAbsolutePath());
        assertEquals(Paths.get("./tmp/org/spt/util/MockClass.class").toAbsolutePath().toString(),
                redefinerStub.map.get(MockClass.class).toString());
    }

    @Test
    public void testFileCreated(){
        controller.processChanges(path.toFile(), StandardWatchEventKinds.ENTRY_CREATE);
        assertEquals(1, compilerStub.mem.size());
        assertEquals(1, loaderStub.mem.size());
        assertEquals(path.toAbsolutePath().toString(), compilerStub.mem.get(0).getAbsolutePath());
        assertEquals(Paths.get("./tmp/org/spt/util/MockClass.class").toAbsolutePath().toString(),
                loaderStub.mem.get(0).toString());
    }
}
