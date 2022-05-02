package net.minecraft.world.entity.boss;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class EnderDragonPart extends Entity {
   public final EnderDragon parentMob;
   public final String name;
   private final EntityDimensions size;

   public EnderDragonPart(EnderDragon parentMob, String name, float var3, float var4) {
      super(parentMob.getType(), parentMob.level);
      this.size = EntityDimensions.scalable(var3, var4);
      this.refreshDimensions();
      this.parentMob = parentMob;
      this.name = name;
   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      return this.isInvulnerableTo(damageSource)?false:this.parentMob.hurt(this, damageSource, var2);
   }

   public boolean is(Entity entity) {
      return this == entity || this.parentMob == entity;
   }

   public Packet getAddEntityPacket() {
      throw new UnsupportedOperationException();
   }

   public EntityDimensions getDimensions(Pose pose) {
      return this.size;
   }
}
