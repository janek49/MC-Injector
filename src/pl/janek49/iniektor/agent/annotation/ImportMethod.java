package pl.janek49.iniektor.agent.annotation;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ImportMethod {
    public Version[] version() default Version.DEFAULT;

    public Version.Compare vcomp() default Version.Compare.EQUAL;

    public String name();

    public String descriptor();
}
