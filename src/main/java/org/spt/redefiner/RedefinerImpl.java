package org.spt.redefiner;

import javassist.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class RedefinerImpl implements Redefiner {

    private final Instrumentation instrumentation;
    private final String CLINIT_METHOD_NAME = "redefineInit";

    public RedefinerImpl(Instrumentation instrumentation){
        this.instrumentation = instrumentation;
    }
    @Override
    public boolean redefineClass(Class<?> clazz, Path file) {
        byte[] bytecode;
        try {
            log.debug("Reading class definition from {}", file);
            bytecode = updateStaticFields(file.toFile(), clazz);
            ClassDefinition definition = new ClassDefinition(clazz, bytecode);
            instrumentation.redefineClasses(definition);
            try {
                Method m = clazz.getClassLoader().loadClass(clazz.getName()).getDeclaredMethod(CLINIT_METHOD_NAME);
                m.setAccessible(true);
                m.invoke(null);
            } catch (NoSuchMethodException ignore) {}
            log.debug("Deleting class-file of redefined class {}", file);
            //if (!file.delete()) log.error("Failed to delete used class-file {}", file.getAbsolutePath());
            return true;
        }catch (IOException ex){
            log.error("Error reading class definition from {}", file);
        }
        catch (UnmodifiableClassException ex){
            log.error("Class redefinition failed: {} class unmodifiable", clazz.getSimpleName());
        }
        catch (ClassNotFoundException ex){
            log.error("Class redefinition failed: {} class not found", clazz.getSimpleName());
        } catch (NotFoundException | InvocationTargetException | IllegalAccessException |
                 CannotCompileException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private byte[] updateStaticFields(File file, Class<?> origClazz) throws CannotCompileException, NotFoundException, IOException {
        CtClass clazz = ClassPool.getDefault().getOrNull(origClazz.getName());
        if (clazz != null) if (clazz.isFrozen()) clazz.defrost();
        clazz = ClassPool.getDefault().makeClass(Files.newInputStream(file.toPath()));
        if (clazz.getClassInitializer() != null) {
            CtConstructor initMethod = new CtConstructor(clazz.getClassInitializer(), clazz, null);
            initMethod.setModifiers(Modifier.STATIC | Modifier.PUBLIC);
            initMethod.getMethodInfo().setName(CLINIT_METHOD_NAME);
            clazz.addConstructor(initMethod);
        }
        return clazz.toBytecode();
    }
}
