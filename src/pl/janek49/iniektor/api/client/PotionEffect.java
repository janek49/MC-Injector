package pl.janek49.iniektor.api.client;

public enum PotionEffect {

    NIGHT_VISION(16, "night_vision"),
    WATER_BREATHING(13, "water_breathing"),
    NAUSEA(9, "nausea");

    public int oldId;
    public String newId;

    private PotionEffect(int oldId, String newId) {
        this.oldId = oldId;
        this.newId = newId;
    }
}
