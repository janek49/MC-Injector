package pl.janek49.iniektor.api.client;

public enum PotionEffect {

    MOVEMENT_SPEED(1, "speed"),
    MOVEMENT_SLOWDOWN(2, "slowness"),
    DIG_SPEED(3, "haste"),
    DIG_SLOWDOWN(4, "mining_fatigue"),
    DAMAGE_BOOST(5, "strength"),
    HEAL(6, "instant_health"),
    HARM(7, "instant_damage"),
    JUMP(8, "jump_boost"),
    NAUSEA(9, "nausea"),
    REGENERATION(10, "regeneration"),
    DAMAGE_RESISTANCE(11, "resistance"),
    FIRE_RESISTANCE(12, "fire_resistance"),
    WATER_BREATHING(13, "water_breathing"),
    INVISIBILITY(14, "invisibility"),
    BLINDNESS(15, "blindness"),
    NIGHT_VISION(16, "night_vision"),
    HUNGER(17, "hunger"),
    WEAKNESS(18, "weakness"),
    POISON(19, "poison"),
    WITHER(20, "wither"),
    HEALTH_BOOST(21, "health_boost"),
    ABSORPTION(22, "absorption"),
    SATURATION(23, "saturation"),
    GLOWING(24, "glowing"),
    LEVITATION(25, "levitation"),
    LUCK(26, "luck"),
    UNLUCK(27, "unluck"),
    SLOW_FALLING(28, "slow_falling"),
    CONDUIT_POWER(29, "conduit_power"),
    DOLPHINS_GRACE(30, "dolphins_grace"),
    BAD_OMEN(31, "bad_omen"),
    HERO_OF_THE_VILLAGE(32, "hero_of_the_village");

    public int oldId;
    public String newId;

    private PotionEffect(int oldId, String newId) {
        this.oldId = oldId;
        this.newId = newId;
    }
}

