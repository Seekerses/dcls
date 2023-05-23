package org.spt.watch;

import org.spt.controller.Controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.ArrayList;
import java.util.List;

public class AgentControllerStub implements Controller {

    public List<FileEvent> modifiedFiles = new ArrayList<>();

    @Override
    public void processChanges(Path file, WatchEvent.Kind<?> eventKind){
        modifiedFiles.add(new FileEvent(file.toFile(), eventKind));
    }

    public static class FileEvent{
        public File file;
        public WatchEvent.Kind<?> kind;

        public FileEvent(File file, WatchEvent.Kind<?> kind){
            this.file = file;
            this.kind = kind;
        }

        @Override
        public String toString(){
            return file.getAbsolutePath() + ", " + kind;
        }
    }
}
