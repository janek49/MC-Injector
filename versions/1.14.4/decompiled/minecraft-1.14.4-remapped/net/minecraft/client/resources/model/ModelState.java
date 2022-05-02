package net.minecraft.client.resources.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.resources.model.BlockModelRotation;

@ClientJarOnly
public interface ModelState {
   default BlockModelRotation getRotation() {
      return BlockModelRotation.X0_Y0;
   }

   default boolean isUvLocked() {
      return false;
   }
}
