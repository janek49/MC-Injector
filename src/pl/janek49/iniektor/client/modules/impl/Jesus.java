package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class Jesus extends Module {
    public Jesus() {
        super("Jezus", Keyboard.KEY_J, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        if (getPlayer().isInWater()) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                getPlayer().motionY = -0.3;
            else
                getPlayer().motionY = 0.1;

        }
    }
}
