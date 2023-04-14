package org.spt.redefiner;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.nio.file.Files;

@Slf4j
public class RedefinerImpl implements Redefiner {

    private final Instrumentation instrumentation;

    public RedefinerImpl(Instrumentation instrumentation){
        this.instrumentation = instrumentation;
    }
    @Override
    public boolean redefineClass(Class<?> clazz, File file) {
        byte[] bytecode;
        try {
            log.debug("Reading class definition from {}", file.getAbsolutePath());
            bytecode = Files.readAllBytes(file.toPath());
            ClassDefinition definition = new ClassDefinition(clazz, bytecode);
            instrumentation.redefineClasses(definition);
            log.debug("Deleting class-file of redefined class {}", file.getAbsolutePath());
            if (!file.delete()) log.error("Failed to delete used class-file {}", file.getAbsolutePath());
            return true;
        }catch (IOException ex){
            log.error("Error reading class definition from {}", file.getAbsolutePath());
        }
        catch (UnmodifiableClassException ex){
            log.error("Class redefinition failed: {} class unmodifiable", clazz.getSimpleName());
        }
        catch (ClassNotFoundException ex){
            log.error("Class redefinition failed: {} class not found", clazz.getSimpleName());
        }
        return false;
    }
}
