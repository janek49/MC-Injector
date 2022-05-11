package pl.janek49.iniektor.api.util;

import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;

import java.io.InputStream;

public class TextureUtil {
    public static InputStream getImageStream(String path) {
        try {
            return(Class.forName(AgentMain.class.getName(), true, ClassLoader.getSystemClassLoader()).getResourceAsStream("/img/" + path));
        } catch (Exception ex) {
            Logger.ex(ex);
        }
        return null;
    }

}
