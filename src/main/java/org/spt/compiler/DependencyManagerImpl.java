package org.spt.compiler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DependencyManagerImpl implements DependencyManager {

    private final List<Path> dependencies = new ArrayList<>();

    @Override
    public boolean isDependencyPresent(Path dependency) {
        return dependencies.contains(dependency);
    }

    @Override
    public String getDependencies() {
        return dependencies.stream().map(Path::toString).collect(Collectors.joining(";"));
    }

    @Override
    public void addDependency(Path dependency) {
        if (!isDependencyPresent(dependency)) dependencies.add(dependency);
    }
}
