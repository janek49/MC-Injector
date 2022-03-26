package pl.janek49.iniektor.agent.asm;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TransformMethodName {
    public Version version();
    public String name();
    public String descriptor();
}
