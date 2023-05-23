package org.spt.compiler;

import java.nio.file.Path;

public interface DependencyManager {
    boolean isDependencyPresent(Path dependency);
    String getDependencies();
    void addDependency(Path dependency);
}
