package pl.janek49.iniektor.agent.annotation;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RenameMethod {
    public Version version();
    public String name();
    public String descriptor();
}