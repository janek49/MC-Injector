package net.minecraft.world.entity.animal.horse;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;

public class Llama extends AbstractChestedHorse implements RangedAttackMob {
   private static final EntityDataAccessor DATA_STRENGTH_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor DATA_SWAG_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor DATA_VARIANT_ID = SynchedEntityData.defineId(Llama.class, EntityDataSerializers.INT);
   private boolean didSpit;
   @Nullable
   private Llama caravanHead;
   @Nullable
   private Llama caravanTail;

   public Llama(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public boolean isTraderLlama() {
      return false;
   }

   private void setStrength(int strength) {
      this.entityData.set(DATA_STRENGTH_ID, Integer.valueOf(Math.max(1, Math.min(5, strength))));
   }

   private void setRandomStrength() {
      int var1 = this.random.nextFloat() < 0.04F?5:3;
      this.setStrength(1 + this.random.nextInt(var1));
   }

   public int getStrength() {
      return ((Integer)this.entityData.get(DATA_STRENGTH_ID)).intValue();
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("Variant", this.getVariant());
      compoundTag.putInt("Strength", this.getStrength());
      if(!this.inventory.getItem(1).isEmpty()) {
         compoundTag.put("DecorItem", this.inventory.getItem(1).save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      this.setStrength(compoundTag.getInt("Strength"));
      super.readAdditionalSaveData(compoundTag);
      this.setVariant(compoundTag.getInt("Variant"));
      if(compoundTag.contains("DecorItem", 10)) {
         this.inventory.setItem(1, ItemStack.of(compoundTag.getCompound("DecorItem")));
      }

      this.updateEquipment();
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2D));
      this.goalSelector.addGoal(2, new LlamaFollowCaravanGoal(this, 2.0999999046325684D));
      this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25D, 40, 20.0F));
      this.goalSelector.addGoal(3, new PanicGoal(this, 1.2D));
      this.goalSelector.addGoal(4, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.0D));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.7D));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new Llama.LlamaHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new Llama.LlamaAttackWolfGoal(this));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(40.0D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_STRENGTH_ID, Integer.valueOf(0));
      this.entityData.define(DATA_SWAG_ID, Integer.valueOf(-1));
      this.entityData.define(DATA_VARIANT_ID, Integer.valueOf(0));
   }

   public int getVariant() {
      return Mth.clamp(((Integer)this.entityData.get(DATA_VARIANT_ID)).intValue(), 0, 3);
   }

   public void setVariant(int variant) {
      this.entityData.set(DATA_VARIANT_ID, Integer.valueOf(variant));
   }

   protected int getInventorySize() {
      return this.hasChest()?2 + 3 * this.getInventoryColumns():super.getInventorySize();
   }

   public void positionRider(Entity entity) {
      if(this.hasPassenger(entity)) {
         float var2 = Mth.cos(this.yBodyRot * 0.017453292F);
         float var3 = Mth.sin(this.yBodyRot * 0.017453292F);
         float var4 = 0.3F;
         entity.setPos(this.x + (double)(0.3F * var3), this.y + this.getRideHeight() + entity.getRidingHeight(), this.z - (double)(0.3F * var2));
      }
   }

   public double getRideHeight() {
      return (double)this.getBbHeight() * 0.67D;
   }

   public boolean canBeControlledByRider() {
      return false;
   }

   protected boolean handleEating(Player inLove, ItemStack itemStack) {
      int var3 = 0;
      int var4 = 0;
      float var5 = 0.0F;
      boolean var6 = false;
      Item var7 = itemStack.getItem();
      if(var7 == Items.WHEAT) {
         var3 = 10;
         var4 = 3;
         var5 = 2.0F;
      } else if(var7 == Blocks.HAY_BLOCK.asItem()) {
         var3 = 90;
         var4 = 6;
         var5 = 10.0F;
         if(this.isTamed() && this.getAge() == 0 && this.canFallInLove()) {
            var6 = true;
            this.setInLove(inLove);
         }
      }

      if(this.getHealth() < this.getMaxHealth() && var5 > 0.0F) {
         this.heal(var5);
         var6 = true;
      }

      if(this.isBaby() && var3 > 0) {
         this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), this.y + 0.5D + (double)(this.random.nextFloat() * this.getBbHeight()), this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), 0.0D, 0.0D, 0.0D);
         if(!this.level.isClientSide) {
            this.ageUp(var3);
         }

         var6 = true;
      }

      if(var4 > 0 && (var6 || !this.isTamed()) && this.getTemper() < this.getMaxTemper()) {
         var6 = true;
         if(!this.level.isClientSide) {
            this.modifyTemper(var4);
         }
      }

      if(var6 && !this.isSilent()) {
         this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.LLAMA_EAT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
      }

      return var6;
   }

   protected boolean isImmobile() {
      return this.getHealth() <= 0.0F || this.isEating();
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      var4 = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
      this.setRandomStrength();
      int var6;
      if(var4 instanceof Llama.LlamaGroupData) {
         var6 = ((Llama.LlamaGroupData)var4).variant;
      } else {
         var6 = this.random.nextInt(4);
         var4 = new Llama.LlamaGroupData(var6);
      }

      this.setVariant(var6);
      return var4;
   }

   protected SoundEvent getAngrySound() {
      return SoundEvents.LLAMA_ANGRY;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.LLAMA_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.LLAMA_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.LLAMA_DEATH;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(SoundEvents.LLAMA_STEP, 0.15F, 1.0F);
   }

   protected void playChestEquipsSound() {
      this.playSound(SoundEvents.LLAMA_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
   }

   public void makeMad() {
      SoundEvent var1 = this.getAngrySound();
      if(var1 != null) {
         this.playSound(var1, this.getSoundVolume(), this.getVoicePitch());
      }

   }

   public int getInventoryColumns() {
      return this.getStrength();
   }

   public boolean wearsArmor() {
      return true;
   }

   public boolean isArmor(ItemStack itemStack) {
      Item var2 = itemStack.getItem();
      return ItemTags.CARPETS.contains(var2);
   }

   public boolean canBeSaddled() {
      return false;
   }

   public void containerChanged(Container container) {
      DyeColor var2 = this.getSwag();
      super.containerChanged(container);
      DyeColor var3 = this.getSwag();
      if(this.tickCount > 20 && var3 != null && var3 != var2) {
         this.playSound(SoundEvents.LLAMA_SWAG, 0.5F, 1.0F);
      }

   }

   protected void updateEquipment() {
      if(!this.level.isClientSide) {
         super.updateEquipment();
         this.setSwag(getDyeColor(this.inventory.getItem(1)));
      }
   }

   private void setSwag(@Nullable DyeColor swag) {
      this.entityData.set(DATA_SWAG_ID, Integer.valueOf(swag == null?-1:swag.getId()));
   }

   @Nullable
   private static DyeColor getDyeColor(ItemStack itemStack) {
      Block var1 = Block.byItem(itemStack.getItem());
      return var1 instanceof WoolCarpetBlock?((WoolCarpetBlock)var1).getColor():null;
   }

   @Nullable
   public DyeColor getSwag() {
      int var1 = ((Integer)this.entityData.get(DATA_SWAG_ID)).intValue();
      return var1 == -1?null:DyeColor.byId(var1);
   }

   public int getMaxTemper() {
      return 30;
   }

   public boolean canMate(Animal animal) {
      return animal != this && animal instanceof Llama && this.canParent() && ((Llama)animal).canParent();
   }

   public Llama getBreedOffspring(AgableMob agableMob) {
      Llama llama = this.makeBabyLlama();
      this.setOffspringAttributes(agableMob, llama);
      Llama var3 = (Llama)agableMob;
      int var4 = this.random.nextInt(Math.max(this.getStrength(), var3.getStrength())) + 1;
      if(this.random.nextFloat() < 0.03F) {
         ++var4;
      }

      llama.setStrength(var4);
      llama.setVariant(this.random.nextBoolean()?this.getVariant():var3.getVariant());
      return llama;
   }

   protected Llama makeBabyLlama() {
      return (Llama)EntityType.LLAMA.create(this.level);
   }

   private void spit(LivingEntity livingEntity) {
      LlamaSpit var2 = new LlamaSpit(this.level, this);
      double var3 = livingEntity.x - this.x;
      double var5 = livingEntity.getBoundingBox().minY + (double)(livingEntity.getBbHeight() / 3.0F) - var2.y;
      double var7 = livingEntity.z - this.z;
      float var9 = Mth.sqrt(var3 * var3 + var7 * var7) * 0.2F;
      var2.shoot(var3, var5 + (double)var9, var7, 1.5F, 10.0F);
      this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
      this.level.addFreshEntity(var2);
      this.didSpit = true;
   }

   private void setDidSpit(boolean didSpit) {
      this.didSpit = didSpit;
   }

   public void causeFallDamage(float var1, float var2) {
      int var3 = Mth.ceil((var1 * 0.5F - 3.0F) * var2);
      if(var3 > 0) {
         if(var1 >= 6.0F) {
            this.hurt(DamageSource.FALL, (float)var3);
            if(this.isVehicle()) {
               for(Entity var5 : this.getIndirectPassengers()) {
                  var5.hurt(DamageSource.FALL, (float)var3);
               }
            }
         }

         BlockState var4 = this.level.getBlockState(new BlockPos(this.x, this.y - 0.2D - (double)this.yRotO, this.z));
         if(!var4.isAir() && !this.isSilent()) {
            SoundType var5 = var4.getSoundType();
            this.level.playSound((Player)null, this.x, this.y, this.z, var5.getStepSound(), this.getSoundSource(), var5.getVolume() * 0.5F, var5.getPitch() * 0.75F);
         }

      }
   }

   public void leaveCaravan() {
      if(this.caravanHead != null) {
         this.caravanHead.caravanTail = null;
      }

      this.caravanHead = null;
   }

   public void joinCaravan(Llama caravanHead) {
      this.caravanHead = caravanHead;
      this.caravanHead.caravanTail = this;
   }

   public boolean hasCaravanTail() {
      return this.caravanTail != null;
   }

   public boolean inCaravan() {
      return this.caravanHead != null;
   }

   @Nullable
   public Llama getCaravanHead() {
      return this.caravanHead;
   }

   protected double followLeashSpeed() {
      return 2.0D;
   }

   protected void followMommy() {
      if(!this.inCaravan() && this.isBaby()) {
         super.followMommy();
      }

   }

   public boolean canEatGrass() {
      return false;
   }

   public void performRangedAttack(LivingEntity livingEntity, float var2) {
      this.spit(livingEntity);
   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }

   static class LlamaAttackWolfGoal extends NearestAttackableTargetGoal {
      public LlamaAttackWolfGoal(Llama llama) {
         super(llama, Wolf.class, 16, false, true, (livingEntity) -> {
            return !((Wolf)livingEntity).isTame();
         });
      }

      protected double getFollowDistance() {
         return super.getFollowDistance() * 0.25D;
      }
   }

   static class LlamaGroupData implements SpawnGroupData {
      public final int variant;

      private LlamaGroupData(int variant) {
         this.variant = variant;
      }
   }

   static class LlamaHurtByTargetGoal extends HurtByTargetGoal {
      public LlamaHurtByTargetGoal(Llama llama) {
         super(llama, new Class[0]);
      }

      public boolean canContinueToUse() {
         if(this.mob instanceof Llama) {
            Llama var1 = (Llama)this.mob;
            if(var1.didSpit) {
               var1.setDidSpit(false);
               return false;
            }
         }

         return super.canContinueToUse();
      }
   }
}
