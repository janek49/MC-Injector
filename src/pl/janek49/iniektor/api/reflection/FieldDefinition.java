package pl.janek49.iniektor.api.reflection;

import pl.janek49.iniektor.agent.Logger;

import java.lang.reflect.Field;

public class FieldDefinition {


    private IWrapper parent;
    private Field fieldBehind;

    public FieldDefinition(IWrapper parent, Field field) {
        this.fieldBehind = field;
        this.parent = parent;
    }

    public <T> T get(){
        return get(parent.getInstanceBehind());
    }

    public <T> T get(Object instance) {
        try {
            return (T) fieldBehind.get(instance);
        } catch (Exception ex) {
            Logger.ex(ex);
            return null;
        }
    }

    public int getInt(Object instance){
        return get(instance);
    }

    public boolean getBoolean(Object instance){
        return get(instance);
    }

    public String getString(Object instance){
        return get(instance);
    }

    public float getFloat(Object instance){
        return get(instance);
    }

    public double getDouble(Object instance){
        return get(instance);
    }

    public void set(Object instance, Object value) {
        try {
            fieldBehind.set(instance, value);
        } catch (Exception ex) {
            Logger.ex(ex);
        }
    }

    public void set(Object value) {
        try {
            fieldBehind.set(parent.getInstanceBehind(), value);
        } catch (Exception ex) {
            Logger.ex(ex);
        }
    }
}
