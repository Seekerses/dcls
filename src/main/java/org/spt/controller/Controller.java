package org.spt.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

public interface Controller {
    void processChanges(Path file, WatchEvent.Kind<?> eventKind);
}
