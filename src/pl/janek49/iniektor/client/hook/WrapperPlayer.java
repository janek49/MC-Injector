package pl.janek49.iniektor.client.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.PlayerCapabilities;
import pl.janek49.iniektor.agent.Version;

public class WrapperPlayer implements IWrapper {


    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/thePlayer")
    public String THEPLAYER_FIELD;

    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/entity/player/EntityPlayer/capabilities")
    public String PLAYER_CAPS_FIELD;

    @Override
    public void initWrapper() {
    }

    public EntityPlayerSP getPlayerObj() {
        try {
            return (EntityPlayerSP) Minecraft.class.getDeclaredField(THEPLAYER_FIELD).get(Minecraft.getMinecraft());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public PlayerCapabilities getPlayerCapabilities() {
        try {
            return (PlayerCapabilities) getPlayerObj().getClass().getField(PLAYER_CAPS_FIELD).get(getPlayerObj());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
