package pl.janek49.iniektor.api.reflection;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.*;

@Repeatable(ResolveMethodBase.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResolveMethod {
    public Version[] version() default Version.DEFAULT;

    public boolean andAbove() default false;

    public String name();

    public String descriptor();

    public Class<? extends ClassImitator> parent() default ClassImitator.EmptyImitator.class;
}