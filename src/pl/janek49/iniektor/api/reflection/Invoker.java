package pl.janek49.iniektor.api.reflection;

public class Invoker {

    public FieldDefinition fieldDefinition;
    public MethodDefinition methodDefinition;

    public Object instance;

    public Invoker(Object value) {
        this.instance = value;
    }

    public static Invoker fromObj(Object value) {
        return new Invoker(value);
    }

    public Invoker field(FieldDefinition fd) {
        fieldDefinition = fd;
        return this;
    }

    public Invoker method(MethodDefinition md) {
        methodDefinition = md;
        return this;
    }

    public Invoker exec(Object... params) {
        instance = methodDefinition.invoke(instance, params);
        return this;
    }

    public Invoker get() {
        instance = fieldDefinition.get(instance);
        return this;
    }

    public <T> T getType(){
        get();
        return (T)getValue();
    }

    public Invoker set(Object newVal) {
        fieldDefinition.set(instance, newVal);
        return this;
    }

    public Object getValue() {
        return instance;
    }
}
