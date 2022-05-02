package net.minecraft.world;

import net.minecraft.world.InteractionResult;

public class InteractionResultHolder {
   private final InteractionResult result;
   private final Object object;

   public InteractionResultHolder(InteractionResult result, Object object) {
      this.result = result;
      this.object = object;
   }

   public InteractionResult getResult() {
      return this.result;
   }

   public Object getObject() {
      return this.object;
   }
}
