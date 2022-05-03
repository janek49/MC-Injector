package test;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.patcher.ApplyPatchTransformer;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.jar.JarFile;

public class FakeInstrumentation implements Instrumentation {


    public ApplyPatchTransformer transformer;

    @Override
    public void addTransformer(ClassFileTransformer transformer, boolean canRetransform) {

    }

    @Override
    public void addTransformer(ClassFileTransformer transformer) {

    }

    @Override
    public boolean removeTransformer(ClassFileTransformer transformer) {
        return false;
    }

    @Override
    public boolean isRetransformClassesSupported() {
        return false;
    }

    @Override
    public void retransformClasses(Class<?>... classes) throws UnmodifiableClassException {
        try {
            String cName = classes[0].getName();
            ClassPath cp = new LoaderClassPath(this.getClass().getClassLoader());
            ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(cp);
            byte[] bytes = pool.get(cName).toBytecode();
            transformer.transform(null, cName, classes[0], null, bytes);
            pool.removeClassPath(cp);
        } catch (Exception ex) {
           throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isRedefineClassesSupported() {
        return false;
    }

    @Override
    public void redefineClasses(ClassDefinition... definitions) throws ClassNotFoundException, UnmodifiableClassException {

    }

    @Override
    public boolean isModifiableClass(Class<?> theClass) {
        return false;
    }

    @Override
    public Class[] getAllLoadedClasses() {
        return new Class[0];
    }

    @Override
    public Class[] getInitiatedClasses(ClassLoader loader) {
        return new Class[0];
    }

    @Override
    public long getObjectSize(Object objectToSize) {
        return 0;
    }

    @Override
    public void appendToBootstrapClassLoaderSearch(JarFile jarfile) {

    }

    @Override
    public void appendToSystemClassLoaderSearch(JarFile jarfile) {

    }

    @Override
    public boolean isNativeMethodPrefixSupported() {
        return false;
    }

    @Override
    public void setNativeMethodPrefix(ClassFileTransformer transformer, String prefix) {

    }
}
