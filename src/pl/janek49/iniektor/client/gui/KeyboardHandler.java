package pl.janek49.iniektor.client.gui;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;

public class KeyboardHandler {
    private boolean keyStates[] = new boolean[256];

    public boolean isKeyPressed(int keyCode) {
        //null = ekran wenątrz gry, keybindy nie mają działać poza grą
        if (Minecraft.getMinecraft().currentScreen != null)
            return false;

        //klawisz wciśnięty, zmień status
        if (Keyboard.isKeyDown(keyCode) != keyStates[keyCode])
            return keyStates[keyCode] = !keyStates[keyCode];

        return false;
    }
}
