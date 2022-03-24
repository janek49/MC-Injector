package pl.janek49.iniektor.client.config;

public class Property<T> {

    public String propertyName;
    public String description;

    public Property(String propertyName, T defaultValue, String description){
        this.value = defaultValue;
        this.propertyName = propertyName;
        this.description = description;
    }

    private T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
