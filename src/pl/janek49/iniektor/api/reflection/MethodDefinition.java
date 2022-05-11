package pl.janek49.iniektor.api.reflection;

import pl.janek49.iniektor.agent.Logger;

import java.lang.reflect.Method;

public class MethodDefinition {
    private Method mdBehind;
    private IWrapper parent;

    public Object currentInstance;

    public MethodDefinition(IWrapper parent, Method md) {
        this.parent = parent;
        this.mdBehind = md;
    }

    public Object invokeSilent(Object instance, Object... params) {
        try {
            return mdBehind.invoke(instance, params);
        } catch (Exception ex) {
            return null;
        }
    }

    public <T> T invokeType(Object instance, Object... params) {
        try {
            return (T) mdBehind.invoke(instance, params);
        } catch (Exception ex) {
            Logger.ex(ex);
            return null;
        }
    }

    public Object invoke(Object instance, Object... params) {
        try {
            return mdBehind.invoke(instance, params);
        } catch (Exception ex) {
            Logger.ex(ex);
            return null;
        }
    }

    public Object call(Object... params) {
        try {
            return mdBehind.invoke(parent.getInstanceBehind(), params);
        } catch (Exception ex) {
            Logger.ex(ex);
            return null;
        }
    }

    public Object invokeSt(Object... params) {
        try {
            return mdBehind.invoke(null, params);
        } catch (Exception ex) {
            Logger.ex(ex);
            return null;
        }
    }
}
