package pl.janek49.iniektor.client.gui;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.input.Mouse;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.client.IniektorClient;

public class MouseHelper {
    public static boolean isButtonDown(int btn){
        if(Reflector.isOnOrAbvVersion(Version.MC1_14_4)){
            return GLFW.glfwGetMouseButton(IniektorClient.INSTANCE.windowId, btn) == 1;
        }else{
            return Mouse.isButtonDown(btn);
        }
    }
}
