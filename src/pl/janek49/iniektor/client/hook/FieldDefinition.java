package pl.janek49.iniektor.client.hook;

import java.lang.reflect.Field;

public class FieldDefinition {
    private Field fieldBehind;

    public FieldDefinition(Field field) {
        this.fieldBehind = field;
    }

    public <T> T get(Object instance) {
        try {
            return (T) fieldBehind.get(instance);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
