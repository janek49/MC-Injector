package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.world.entity.HumanoidArm;

@ClientJarOnly
public interface ArmedModel {
   void translateToHand(float var1, HumanoidArm var2);
}
