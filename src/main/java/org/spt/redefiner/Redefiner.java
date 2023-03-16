package org.spt.redefiner;

import java.io.File;

public interface Redefiner {

    public boolean redefineClass(Class clazz, File file);
}
