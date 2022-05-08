package pl.janek49.iniektor.api.reflection;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MinimumVersion {
    public Version version();
}
