package pl.janek49.iniektor.client.gui;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.api.reflection.Keys;
import pl.janek49.iniektor.client.IniektorClient;

public class KeyboardHandler {
    private boolean keyStates[] = new boolean[256];

    public boolean isKeyPressed(Keys keyCode) {
        //null = ekran wenątrz gry, keybindy nie mają działać poza grą
        if (Minecraft.currentScreen.get() != null)
            return false;

        //klawisz wciśnięty, zmień status
        if (Keyboard.isKeyDown(keyCode.gl2code) != keyStates[keyCode.gl2code])
            return keyStates[keyCode.gl2code] = !keyStates[keyCode.gl2code];

        return false;
    }

    public boolean isKeyDown(Keys keyCode){
        return Keyboard.isKeyDown(keyCode.gl2code);
    }

    public static class Mc114KeyBoardHandler extends KeyboardHandler{
        private boolean keyStates[] = new boolean[1000];

        @Override
        public boolean isKeyPressed(Keys keyCode) {
            if(keyCode == Keys.KEY_NONE)
                return false;

            //null = ekran wenątrz gry, keybindy nie mają działać poza grą
            if (Minecraft.currentScreen.get() != null)
                return false;

            //klawisz wciśnięty, zmień status
            if ((GLFW.glfwGetKey(IniektorClient.INSTANCE.windowId, keyCode.glfwcode) == 1) != keyStates[keyCode.glfwcode])
                return keyStates[keyCode.glfwcode] = !keyStates[keyCode.glfwcode];

            return false;
        }

        @Override
        public boolean isKeyDown(Keys keyCode) {
            return GLFW.glfwGetKey(IniektorClient.INSTANCE.windowId, keyCode.glfwcode) == 1;
        }
    }
}
