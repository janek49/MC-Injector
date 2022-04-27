package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;

@ClassImitator.ResolveClass(version = Version.DEFAULT, name = "net/minecraft/client/gui/Gui")
public class Gui extends ClassImitator {
    public static ClassInformation target;

    @ResolveConstructor(params = {})
    public static ConstructorDefinition constructor;

    @ResolveMethod(name = "drawTexturedModalRect", descriptor = "(IIIIII)V")
    public static MethodDefinition _drawTexturedModalRect;

    public void drawTexturedModalRect(int x, int y, int texX, int texY, int w, int h) {
        _drawTexturedModalRect.call(x, y, texX, texY, w, h);
    }
}
