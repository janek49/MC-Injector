package net.minecraft.world.entity.animal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;

public abstract class ShoulderRidingEntity extends TamableAnimal {
   private int rideCooldownCounter;

   protected ShoulderRidingEntity(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public boolean setEntityOnShoulder(ServerPlayer entityOnShoulder) {
      CompoundTag var2 = new CompoundTag();
      var2.putString("id", this.getEncodeId());
      this.saveWithoutId(var2);
      if(entityOnShoulder.setEntityOnShoulder(var2)) {
         this.remove();
         return true;
      } else {
         return false;
      }
   }

   public void tick() {
      ++this.rideCooldownCounter;
      super.tick();
   }

   public boolean canSitOnShoulder() {
      return this.rideCooldownCounter > 100;
   }
}
