package net.minecraft.world.entity.boss.enderdragon;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.dimension.end.TheEndDimension;

public class EndCrystal extends Entity {
   private static final EntityDataAccessor DATA_BEAM_TARGET = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
   private static final EntityDataAccessor DATA_SHOW_BOTTOM = SynchedEntityData.defineId(EndCrystal.class, EntityDataSerializers.BOOLEAN);
   public int time;

   public EndCrystal(EntityType entityType, Level level) {
      super(entityType, level);
      this.blocksBuilding = true;
      this.time = this.random.nextInt(100000);
   }

   public EndCrystal(Level level, double var2, double var4, double var6) {
      this(EntityType.END_CRYSTAL, level);
      this.setPos(var2, var4, var6);
   }

   protected boolean makeStepSound() {
      return false;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_BEAM_TARGET, Optional.empty());
      this.getEntityData().define(DATA_SHOW_BOTTOM, Boolean.valueOf(true));
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      ++this.time;
      if(!this.level.isClientSide) {
         BlockPos var1 = new BlockPos(this);
         if(this.level.dimension instanceof TheEndDimension && this.level.getBlockState(var1).isAir()) {
            this.level.setBlockAndUpdate(var1, Blocks.FIRE.defaultBlockState());
         }
      }

   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      if(this.getBeamTarget() != null) {
         compoundTag.put("BeamTarget", NbtUtils.writeBlockPos(this.getBeamTarget()));
      }

      compoundTag.putBoolean("ShowBottom", this.showsBottom());
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      if(compoundTag.contains("BeamTarget", 10)) {
         this.setBeamTarget(NbtUtils.readBlockPos(compoundTag.getCompound("BeamTarget")));
      }

      if(compoundTag.contains("ShowBottom", 1)) {
         this.setShowBottom(compoundTag.getBoolean("ShowBottom"));
      }

   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else if(damageSource.getEntity() instanceof EnderDragon) {
         return false;
      } else {
         if(!this.removed && !this.level.isClientSide) {
            this.remove();
            if(!damageSource.isExplosion()) {
               this.level.explode((Entity)null, this.x, this.y, this.z, 6.0F, Explosion.BlockInteraction.DESTROY);
            }

            this.onDestroyedBy(damageSource);
         }

         return true;
      }
   }

   public void kill() {
      this.onDestroyedBy(DamageSource.GENERIC);
      super.kill();
   }

   private void onDestroyedBy(DamageSource damageSource) {
      if(this.level.dimension instanceof TheEndDimension) {
         TheEndDimension var2 = (TheEndDimension)this.level.dimension;
         EndDragonFight var3 = var2.getDragonFight();
         if(var3 != null) {
            var3.onCrystalDestroyed(this, damageSource);
         }
      }

   }

   public void setBeamTarget(@Nullable BlockPos beamTarget) {
      this.getEntityData().set(DATA_BEAM_TARGET, Optional.ofNullable(beamTarget));
   }

   @Nullable
   public BlockPos getBeamTarget() {
      return (BlockPos)((Optional)this.getEntityData().get(DATA_BEAM_TARGET)).orElse((Object)null);
   }

   public void setShowBottom(boolean showBottom) {
      this.getEntityData().set(DATA_SHOW_BOTTOM, Boolean.valueOf(showBottom));
   }

   public boolean showsBottom() {
      return ((Boolean)this.getEntityData().get(DATA_SHOW_BOTTOM)).booleanValue();
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      return super.shouldRenderAtSqrDistance(d) || this.getBeamTarget() != null;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
