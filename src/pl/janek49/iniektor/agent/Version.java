package pl.janek49.iniektor.agent;

public enum Version {
    DEFAULT, MC1_6_4, MC1_7_10, MC1_8_8, MC1_9_4, MC1_10, MC1_11_2, MC1_12, MC1_12_2, MC1_14_4;

    public static boolean matches(Version versionOn, Version versionTarget, Compare check) {
        if (check == Compare.EQUAL) {
            return versionOn == versionTarget || versionTarget == DEFAULT;
        } else if (check == Compare.OR_LOWER) {
            return versionOn.ordinal() <= versionTarget.ordinal();
        } else if (check == Compare.OR_HIGHER) {
            return versionOn.ordinal() >= versionTarget.ordinal();
        }
        return false;
    }

    public static boolean inRange(Version versionOn, Version upperLimit, Version lowerLimit){
        return versionOn.ordinal() >= lowerLimit.ordinal() && versionOn.ordinal() <= upperLimit.ordinal();
    }

    public enum Compare {
        OR_LOWER, EQUAL, OR_HIGHER
    }
}
