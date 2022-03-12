package pl.janek49.iniektor.client;

public class Hooks {

    public static String GetFullClassNameForJA() {
        return Hooks.class.getName().replace("/", ".");
    }

    public static void HookRenderInGameOverlay() {

    }
}
