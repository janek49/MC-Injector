package pl.janek49.iniektor.agent.annotation;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(SetSuperClassBase.class)
public @interface SetSuperClass {
    public Version version() default Version.DEFAULT;
    public boolean andAbove() default false;
    public String value();
}

