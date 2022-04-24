package pl.janek49.iniektor.api;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.*;

public class ClassImitator implements IWrapper {

    public static <T extends ClassImitator> T fromObj(Class<? extends ClassImitator> type, Object obj) {
        try {
            Object typeInst = type.newInstance();
            return (T) typeInst;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static class ClassInformation {
        public Class javaClass;
        public String deobfClassName;
        public String obfClassName;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Repeatable(ResolveClassBase.class)
    @interface ResolveClass {
        public Version[] version();

        public boolean andAbove() default false;

        public String name();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface ResolveClassBase {
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



    private Object instance;

    protected ClassImitator(Object instance) {
        this.instance = instance;
    }

    public ClassImitator(){}

    @Override
    public void initWrapper() {

    }

    @Override
    public Object getInstance() {
        return instance;
    }
}
