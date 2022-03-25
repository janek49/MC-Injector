package pl.janek49.iniektor.client.hook;

import pl.janek49.iniektor.agent.Version;

import java.lang.annotation.*;

@Repeatable(ResolveConstructorBase.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResolveConstructor {
    public Version[] version();
    public boolean andAbove() default false;
    public String name();
    public String[] params();
}
