package pl.janek49.iniektor.api;

import java.lang.reflect.Field;

public class FieldDefinition {


    private IWrapper parent;
    private Field fieldBehind;

    public FieldDefinition(IWrapper parent, Field field) {
        this.fieldBehind = field;
        this.parent = parent;
    }

    public <T> T get(){
        return get(parent.getDefaultInstance());
    }

    public <T> T get(Object instance) {
        try {
            return (T) fieldBehind.get(instance);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void set(Object instance, Object value) {
        try {
            fieldBehind.set(instance, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
