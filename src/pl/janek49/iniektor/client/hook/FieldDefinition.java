package pl.janek49.iniektor.client.hook;

import net.minecraft.client.renderer.EntityRenderer;

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

    public void set(Object instance, Object value) {
        try {
            fieldBehind.set(instance, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
