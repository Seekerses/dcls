package org.spt.util;

import org.springframework.boot.loader.LaunchedURLClassLoader;

import java.util.Optional;
import java.util.Set;

public class SpringUtil {

    public static Optional<LaunchedURLClassLoader> getSpringClassLoader(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Optional<Thread> springClassLoadedThread = threadSet.stream()
                .filter(v -> v.getContextClassLoader() instanceof LaunchedURLClassLoader).findFirst();
        return springClassLoadedThread.map(thread -> (LaunchedURLClassLoader) thread.getContextClassLoader());
    }
}
