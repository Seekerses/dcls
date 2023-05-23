package org.spt.controller;

import org.spt.loader.Loader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class LoaderStub implements Loader {

    private final URLClassLoader loader = new URLClassLoader(new URL[] {});
    public List<File> mem = new ArrayList<>();

    @Override
    public boolean load(File file) {
        mem.add(file);
        return true;
    }

}
