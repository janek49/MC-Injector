package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.geom.ModelPart;

@ClientJarOnly
public interface HeadedModel {
   ModelPart getHead();

   default void translateToHead(float f) {
      this.getHead().translateTo(f);
   }
}
