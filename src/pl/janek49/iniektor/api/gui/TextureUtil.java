package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.AgentMain;

import java.io.InputStream;

public class TextureUtil {
    public static InputStream getImageStream(String path) {
        try {
            return(Class.forName(AgentMain.class.getName(), true, ClassLoader.getSystemClassLoader()).getResourceAsStream("/img/" + path));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
