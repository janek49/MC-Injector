package pl.janek49.iniektor.api.reflection;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.*;

@Repeatable(ResolveConstructorBase.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResolveConstructor {
    public Version[] version() default Version.DEFAULT;
    public boolean andAbove() default false;
    public String name() default "";
    public String[] params();
}
