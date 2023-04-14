package org.spt.controller;

import java.io.File;
import java.nio.file.WatchEvent;

public interface Controller {
    void processChanges(File file, WatchEvent.Kind<?> eventKind);
}
