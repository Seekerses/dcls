package org.spt.loader;

import lombok.extern.slf4j.Slf4j;
import org.spt.util.FileUtil;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.jar.JarFile;

@Slf4j
public class LoaderImpl implements Loader {

    public LoaderImpl(Instrumentation instrumentation, JarFile jarFile) {
        instrumentation.appendToSystemClassLoaderSearch(jarFile);
    }

    @Override
    public boolean load(Path file) {
        try {
            String className = FileUtil.compiledFileToClassName(file);
            log.debug("Loading class from {} with name {}", file, className);
            getClass().getClassLoader().loadClass(className);
            return true;
        }catch (ClassNotFoundException ex){
            log.error("Failed to load class from {}, {}", file, ex.getStackTrace());
            return false;
        } catch (IOException e) {
            log.error("Failed to find convert compiled file name to class name.");
            return false;
        }
    }
}
