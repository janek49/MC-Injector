package pl.janek49.iniektor.api.reflection;

import pl.janek49.iniektor.agent.Logger;

import java.lang.reflect.Constructor;

public class ConstructorDefinition {
    private Constructor constructorBehind;
    public Class javaClass;

    public ConstructorDefinition(Constructor constructorBehind) {
        this.constructorBehind = constructorBehind;
    }

    public <T> T newType(Object... params) {
        try {
            return (T) constructorBehind.newInstance(params);
        } catch (Exception ex) {
            Logger.ex(ex);
            return null;
        }
    }

    public Object newInstance(Object... params) {
        try {
            return constructorBehind.newInstance(params);
        } catch (Exception ex) {
            Logger.ex(ex);
            return null;
        }
    }
}
