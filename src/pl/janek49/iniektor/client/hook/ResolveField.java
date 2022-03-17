package pl.janek49.iniektor.client.hook;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.*;

@Repeatable(ResolveFieldBase.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface ResolveField {
    public Version[] version();
    public String name();
}

