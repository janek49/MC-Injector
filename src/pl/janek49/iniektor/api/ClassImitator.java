package pl.janek49.iniektor.api;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.*;

public class ClassImitator implements IWrapper {

    public static <T extends ClassImitator> T fromObj(Class<? extends ClassImitator> type, Object obj) {
        try {
            Object typeInst = type.newInstance();
            ((ClassImitator)typeInst).instance = obj;
            return (T) typeInst;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    //shall be in static field "target"
    public static class ClassInformation {
        public Class javaClass;
        public String deobfClassName;
        public String obfClassName;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Repeatable(ResolveClassBase.class)
    public @interface ResolveClass {
        public Version[] version() default Version.DEFAULT;

        public boolean andAbove() default false;

        public String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ResolveClassBase {
        public ResolveClass[] value();
    }


    public ClassInformation getTarget() {
        try {
            return (ClassInformation) this.getClass().getField("target").get(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Class getTargetClass() {
        try {
            return ((ClassInformation) this.getClass().getField("target").get(this)).javaClass;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private Object instance;

    public ClassImitator(Object instance) {
        this.instance = instance;
    }

    public ClassImitator() {
    }

    @Override
    public void initWrapper() {

    }

    @Override
    public Object getInstanceBehind() {
        return instance;
    }
}
