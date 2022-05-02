package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;

public abstract class Animal extends AgableMob {
   private int inLove;
   private UUID loveCause;

   protected Animal(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void customServerAiStep() {
      if(this.getAge() != 0) {
         this.inLove = 0;
      }

      super.customServerAiStep();
   }

   public void aiStep() {
      super.aiStep();
      if(this.getAge() != 0) {
         this.inLove = 0;
      }

      if(this.inLove > 0) {
         --this.inLove;
         if(this.inLove % 10 == 0) {
            double var1 = this.random.nextGaussian() * 0.02D;
            double var3 = this.random.nextGaussian() * 0.02D;
            double var5 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(ParticleTypes.HEART, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), this.y + 0.5D + (double)(this.random.nextFloat() * this.getBbHeight()), this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), var1, var3, var5);
         }
      }

   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(this.isInvulnerableTo(damageSource)) {
         return false;
      } else {
         this.inLove = 0;
         return super.hurt(damageSource, var2);
      }
   }

   public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
      return levelReader.getBlockState(blockPos.below()).getBlock() == Blocks.GRASS_BLOCK?10.0F:levelReader.getBrightness(blockPos) - 0.5F;
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("InLove", this.inLove);
      if(this.loveCause != null) {
         compoundTag.putUUID("LoveCause", this.loveCause);
      }

   }

   public double getRidingHeight() {
      return 0.14D;
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.inLove = compoundTag.getInt("InLove");
      this.loveCause = compoundTag.hasUUID("LoveCause")?compoundTag.getUUID("LoveCause"):null;
   }

   public static boolean checkAnimalSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return levelAccessor.getBlockState(blockPos.below()).getBlock() == Blocks.GRASS_BLOCK && levelAccessor.getRawBrightness(blockPos, 0) > 8;
   }

   public int getAmbientSoundInterval() {
      return 120;
   }

   public boolean removeWhenFarAway(double d) {
      return false;
   }

   protected int getExperienceReward(Player player) {
      return 1 + this.level.random.nextInt(3);
   }

   public boolean isFood(ItemStack itemStack) {
      return itemStack.getItem() == Items.WHEAT;
   }

   public boolean mobInteract(Player inLove, InteractionHand interactionHand) {
      ItemStack var3 = inLove.getItemInHand(interactionHand);
      if(this.isFood(var3)) {
         if(this.getAge() == 0 && this.canFallInLove()) {
            this.usePlayerItem(inLove, var3);
            this.setInLove(inLove);
            return true;
         }

         if(this.isBaby()) {
            this.usePlayerItem(inLove, var3);
            this.ageUp((int)((float)(-this.getAge() / 20) * 0.1F), true);
            return true;
         }
      }

      return super.mobInteract(inLove, interactionHand);
   }

   protected void usePlayerItem(Player player, ItemStack itemStack) {
      if(!player.abilities.instabuild) {
         itemStack.shrink(1);
      }

   }

   public boolean canFallInLove() {
      return this.inLove <= 0;
   }

   public void setInLove(@Nullable Player inLove) {
      this.inLove = 600;
      if(inLove != null) {
         this.loveCause = inLove.getUUID();
      }

      this.level.broadcastEntityEvent(this, (byte)18);
   }

   public void setInLoveTime(int inLoveTime) {
      this.inLove = inLoveTime;
   }

   @Nullable
   public ServerPlayer getLoveCause() {
      if(this.loveCause == null) {
         return null;
      } else {
         Player var1 = this.level.getPlayerByUUID(this.loveCause);
         return var1 instanceof ServerPlayer?(ServerPlayer)var1:null;
      }
   }

   public boolean isInLove() {
      return this.inLove > 0;
   }

   public void resetLove() {
      this.inLove = 0;
   }

   public boolean canMate(Animal animal) {
      return animal == this?false:(animal.getClass() != this.getClass()?false:this.isInLove() && animal.isInLove());
   }

   public void handleEntityEvent(byte b) {
      if(b == 18) {
         for(int var2 = 0; var2 < 7; ++var2) {
            double var3 = this.random.nextGaussian() * 0.02D;
            double var5 = this.random.nextGaussian() * 0.02D;
            double var7 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(ParticleTypes.HEART, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), this.y + 0.5D + (double)(this.random.nextFloat() * this.getBbHeight()), this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), var3, var5, var7);
         }
      } else {
         super.handleEntityEvent(b);
      }

   }
}
