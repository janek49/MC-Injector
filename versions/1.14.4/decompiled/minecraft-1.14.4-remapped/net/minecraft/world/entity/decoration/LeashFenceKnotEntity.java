package net.minecraft.world.entity.decoration;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class LeashFenceKnotEntity extends HangingEntity {
   public LeashFenceKnotEntity(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public LeashFenceKnotEntity(Level level, BlockPos blockPos) {
      super(EntityType.LEASH_KNOT, level, blockPos);
      this.setPos((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D);
      float var3 = 0.125F;
      float var4 = 0.1875F;
      float var5 = 0.25F;
      this.setBoundingBox(new AABB(this.x - 0.1875D, this.y - 0.25D + 0.125D, this.z - 0.1875D, this.x + 0.1875D, this.y + 0.25D + 0.125D, this.z + 0.1875D));
      this.forcedLoading = true;
   }

   public void setPos(double var1, double var3, double var5) {
      super.setPos((double)Mth.floor(var1) + 0.5D, (double)Mth.floor(var3) + 0.5D, (double)Mth.floor(var5) + 0.5D);
   }

   protected void recalculateBoundingBox() {
      this.x = (double)this.pos.getX() + 0.5D;
      this.y = (double)this.pos.getY() + 0.5D;
      this.z = (double)this.pos.getZ() + 0.5D;
   }

   public void setDirection(Direction direction) {
   }

   public int getWidth() {
      return 9;
   }

   public int getHeight() {
      return 9;
   }

   protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return -0.0625F;
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      return d < 1024.0D;
   }

   public void dropItem(@Nullable Entity entity) {
      this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
   }

   public boolean interact(Player player, InteractionHand interactionHand) {
      if(this.level.isClientSide) {
         return true;
      } else {
         boolean var3 = false;
         double var4 = 7.0D;
         List<Mob> var6 = this.level.getEntitiesOfClass(Mob.class, new AABB(this.x - 7.0D, this.y - 7.0D, this.z - 7.0D, this.x + 7.0D, this.y + 7.0D, this.z + 7.0D));

         for(Mob var8 : var6) {
            if(var8.getLeashHolder() == player) {
               var8.setLeashedTo(this, true);
               var3 = true;
            }
         }

         if(!var3) {
            this.remove();
            if(player.abilities.instabuild) {
               for(Mob var8 : var6) {
                  if(var8.isLeashed() && var8.getLeashHolder() == this) {
                     var8.dropLeash(true, false);
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean survives() {
      return this.level.getBlockState(this.pos).getBlock().is(BlockTags.FENCES);
   }

   public static LeashFenceKnotEntity getOrCreateKnot(Level level, BlockPos blockPos) {
      int var2 = blockPos.getX();
      int var3 = blockPos.getY();
      int var4 = blockPos.getZ();

      for(LeashFenceKnotEntity var7 : level.getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB((double)var2 - 1.0D, (double)var3 - 1.0D, (double)var4 - 1.0D, (double)var2 + 1.0D, (double)var3 + 1.0D, (double)var4 + 1.0D))) {
         if(var7.getPos().equals(blockPos)) {
            return var7;
         }
      }

      LeashFenceKnotEntity var6 = new LeashFenceKnotEntity(level, blockPos);
      level.addFreshEntity(var6);
      var6.playPlacementSound();
      return var6;
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, this.getType(), 0, this.getPos());
   }
}
