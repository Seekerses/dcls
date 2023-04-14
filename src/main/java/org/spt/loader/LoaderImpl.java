package org.spt.loader;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

@Slf4j
public class LoaderImpl implements Loader {

    private final String TMP_DIR_PATH = "./tmp";

    private final URLClassLoader urlClassLoader;

    public LoaderImpl() throws MalformedURLException {
        this.urlClassLoader = new URLClassLoader(new URL[] {Paths.get(TMP_DIR_PATH).toAbsolutePath().toUri().toURL()}, getClass().getClassLoader());
    }

    @Override
    public boolean load(File file) {
        try {
            String className = convertFileToClassName(file);
            log.debug("Loading class from {} with name {}", file.getAbsolutePath(), className);
            urlClassLoader.loadClass(className);
            return true;
        }catch (ClassNotFoundException ex){
            log.error("Failed to load class from {}", file.getAbsolutePath());
            return false;
        }
    }

    @Override
    public ClassLoader getLoader() {
        return urlClassLoader;
    }

    private String convertFileToClassName(File file){
        return file.getAbsolutePath()
                .replace(Paths.get(TMP_DIR_PATH).toAbsolutePath() + "/", "")
                .replace("/", ".")
                .replace(".class", "");
    }

}
