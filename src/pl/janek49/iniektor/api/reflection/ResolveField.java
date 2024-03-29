package pl.janek49.iniektor.api.reflection;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.*;

@Repeatable(ResolveFieldBase.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface ResolveField {
    public Version[] version() default Version.DEFAULT;
    public boolean andAbove() default false;
    public String value();
}

