package pl.janek49.iniektor.agent.annotation;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ImportMethodBase.class)
public @interface ImportMethod {
    public Version[] version() default Version.DEFAULT;

    public Version.Compare vcomp() default Version.Compare.EQUAL;

    public String name();

    public String descriptor();
}
