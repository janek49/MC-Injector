package net.minecraft.world.level.block.entity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.TickableBlockEntity;

public class EnderChestBlockEntity extends BlockEntity implements LidBlockEntity, TickableBlockEntity {
   public float openness;
   public float oOpenness;
   public int openCount;
   private int tickInterval;

   public EnderChestBlockEntity() {
      super(BlockEntityType.ENDER_CHEST);
   }

   public void tick() {
      if(++this.tickInterval % 20 * 4 == 0) {
         this.level.blockEvent(this.worldPosition, Blocks.ENDER_CHEST, 1, this.openCount);
      }

      this.oOpenness = this.openness;
      int var1 = this.worldPosition.getX();
      int var2 = this.worldPosition.getY();
      int var3 = this.worldPosition.getZ();
      float var4 = 0.1F;
      if(this.openCount > 0 && this.openness == 0.0F) {
         double var5 = (double)var1 + 0.5D;
         double var7 = (double)var3 + 0.5D;
         this.level.playSound((Player)null, var5, (double)var2 + 0.5D, var7, SoundEvents.ENDER_CHEST_OPEN, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
      }

      if(this.openCount == 0 && this.openness > 0.0F || this.openCount > 0 && this.openness < 1.0F) {
         float var5 = this.openness;
         if(this.openCount > 0) {
            this.openness += 0.1F;
         } else {
            this.openness -= 0.1F;
         }

         if(this.openness > 1.0F) {
            this.openness = 1.0F;
         }

         float var6 = 0.5F;
         if(this.openness < 0.5F && var5 >= 0.5F) {
            double var7 = (double)var1 + 0.5D;
            double var9 = (double)var3 + 0.5D;
            this.level.playSound((Player)null, var7, (double)var2 + 0.5D, var9, SoundEvents.ENDER_CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
         }

         if(this.openness < 0.0F) {
            this.openness = 0.0F;
         }
      }

   }

   public boolean triggerEvent(int var1, int openCount) {
      if(var1 == 1) {
         this.openCount = openCount;
         return true;
      } else {
         return super.triggerEvent(var1, openCount);
      }
   }

   public void setRemoved() {
      this.clearCache();
      super.setRemoved();
   }

   public void startOpen() {
      ++this.openCount;
      this.level.blockEvent(this.worldPosition, Blocks.ENDER_CHEST, 1, this.openCount);
   }

   public void stopOpen() {
      --this.openCount;
      this.level.blockEvent(this.worldPosition, Blocks.ENDER_CHEST, 1, this.openCount);
   }

   public boolean stillValid(Player player) {
      return this.level.getBlockEntity(this.worldPosition) != this?false:player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
   }

   public float getOpenNess(float f) {
      return Mth.lerp(f, this.oOpenness, this.openness);
   }
}
