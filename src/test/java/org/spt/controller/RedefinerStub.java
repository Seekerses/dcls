package org.spt.controller;

import org.spt.redefiner.Redefiner;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RedefinerStub implements Redefiner {

    public Map<Class<?>, File> map = new HashMap<>();

    @Override
    public boolean redefineClass(Class<?> clazz, File file) {
        map.put(clazz, file);
        return true;
    }
}
