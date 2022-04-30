package pl.janek49.iniektor.api;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.client.Minecraft;

public class WrapperChat implements IWrapper {

    @ResolveMethod(version = Version.MC1_7_10, name = "net/minecraft/client/gui/GuiNewChat/func_146239_a", descriptor = "(Ljava/lang/String;)V")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/client/gui/GuiNewChat/addToSentMessages", descriptor = "(Ljava/lang/String;)V")
    public static MethodDefinition addToSentMessages;

    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/client/gui/GuiIngame/getChatGUI", descriptor = "()Lnet/minecraft/client/gui/GuiNewChat;")
    public static MethodDefinition getChatGUI;

    @ResolveConstructor(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/util/text/TextComponentString", params = {"java/lang/String"})
    @ResolveConstructor(version = Version.MC1_7_10, andAbove = true, name = "net/minecraft/util/ChatComponentText", params = {"java/lang/String"})
    public static ConstructorDefinition TextComponentString;

    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/client/entity/EntityPlayerSP/addChatMessage", descriptor = "(Lnet/minecraft/util/text/ITextComponent;)V")
    @ResolveMethod(version = Version.MC1_7_10, andAbove = true, name = "net/minecraft/client/entity/EntityPlayerSP/addChatMessage", descriptor = "(Lnet/minecraft/util/IChatComponent;)V")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/client/entity/EntityPlayerSP/addChatMessage", descriptor = "(Ljava/lang/String;)V")
    public static MethodDefinition addChatMessage;

    @Override
    public void initWrapper() {

    }

    @Override
    public Object getInstanceBehind() {
        return null;
    }

    public static void showChatMessage(String text) {
        try {
            String val = "§7[§cIniektor§7] §r" + text;
            if (Reflector.isOnOrAbvVersion(Version.MC1_7_10)) {
                WrapperChat.addChatMessage.invoke(Minecraft.thePlayer.get(), WrapperChat.TextComponentString.newInstance(val));
            } else {
                WrapperChat.addChatMessage.invoke(Minecraft.thePlayer.get(), val);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
