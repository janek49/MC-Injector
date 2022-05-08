package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.reflection.*;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/client/gui/GuiComponent")
@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/client/gui/Gui")
public class Gui extends ClassImitator {

    public Gui(Object instance){
        super(instance);
    }
    private Gui(){}

    public static ClassInformation target;

    @ResolveConstructor(params = {})
    public static ConstructorDefinition constructor;

    @ResolveMethod(version =  Version.MC1_14_4, andAbove = true, name = "blit", descriptor = "(IIIIII)V")
    @ResolveMethod(name = "drawTexturedModalRect", descriptor = "(IIIIII)V")
    public static MethodDefinition _drawTexturedModalRect;

    public void drawTexturedModalRect(int x, int y, int texX, int texY, int w, int h) {
        _drawTexturedModalRect.invoke(getInstanceBehind(), x, y, texX, texY, w, h);
    }
}
