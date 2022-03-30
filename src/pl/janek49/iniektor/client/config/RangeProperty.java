package pl.janek49.iniektor.client.config;

public class RangeProperty extends Property<Float> {

    public String propertyName;
    public String description;

    public float min, max;

    public RangeProperty(String propertyName, float defaultValue, float min, float max, String description){
        super(propertyName, defaultValue, description);
        this.value = defaultValue;
        this.propertyName = propertyName;
        this.description = description;
        this.min = min;
        this.max = max;
    }

    private float value;

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
