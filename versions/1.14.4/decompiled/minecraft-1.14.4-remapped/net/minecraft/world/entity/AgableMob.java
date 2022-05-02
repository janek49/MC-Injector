package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;

public abstract class AgableMob extends PathfinderMob {
   private static final EntityDataAccessor DATA_BABY_ID = SynchedEntityData.defineId(AgableMob.class, EntityDataSerializers.BOOLEAN);
   protected int age;
   protected int forcedAge;
   protected int forcedAgeTimer;

   protected AgableMob(EntityType entityType, Level level) {
      super(entityType, level);
   }

   @Nullable
   public abstract AgableMob getBreedOffspring(AgableMob var1);

   protected void onOffspringSpawnedFromEgg(Player player, AgableMob agableMob) {
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      Item var4 = var3.getItem();
      if(var4 instanceof SpawnEggItem && ((SpawnEggItem)var4).spawnsEntity(var3.getTag(), this.getType())) {
         if(!this.level.isClientSide) {
            AgableMob var5 = this.getBreedOffspring(this);
            if(var5 != null) {
               var5.setAge(-24000);
               var5.moveTo(this.x, this.y, this.z, 0.0F, 0.0F);
               this.level.addFreshEntity(var5);
               if(var3.hasCustomHoverName()) {
                  var5.setCustomName(var3.getHoverName());
               }

               this.onOffspringSpawnedFromEgg(player, var5);
               if(!player.abilities.instabuild) {
                  var3.shrink(1);
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_BABY_ID, Boolean.valueOf(false));
   }

   public int getAge() {
      return this.level.isClientSide?(((Boolean)this.entityData.get(DATA_BABY_ID)).booleanValue()?-1:1):this.age;
   }

   public void ageUp(int var1, boolean var2) {
      int var3 = this.getAge();
      int var4 = var3;
      var3 = var3 + var1 * 20;
      if(var3 > 0) {
         var3 = 0;
      }

      int var5 = var3 - var4;
      this.setAge(var3);
      if(var2) {
         this.forcedAge += var5;
         if(this.forcedAgeTimer == 0) {
            this.forcedAgeTimer = 40;
         }
      }

      if(this.getAge() == 0) {
         this.setAge(this.forcedAge);
      }

   }

   public void ageUp(int i) {
      this.ageUp(i, false);
   }

   public void setAge(int age) {
      int var2 = this.age;
      this.age = age;
      if(var2 < 0 && age >= 0 || var2 >= 0 && age < 0) {
         this.entityData.set(DATA_BABY_ID, Boolean.valueOf(age < 0));
         this.ageBoundaryReached();
      }

   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putInt("Age", this.getAge());
      compoundTag.putInt("ForcedAge", this.forcedAge);
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setAge(compoundTag.getInt("Age"));
      this.forcedAge = compoundTag.getInt("ForcedAge");
   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      if(DATA_BABY_ID.equals(entityDataAccessor)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(entityDataAccessor);
   }

   public void aiStep() {
      super.aiStep();
      if(this.level.isClientSide) {
         if(this.forcedAgeTimer > 0) {
            if(this.forcedAgeTimer % 4 == 0) {
               this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.x + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), this.y + 0.5D + (double)(this.random.nextFloat() * this.getBbHeight()), this.z + (double)(this.random.nextFloat() * this.getBbWidth() * 2.0F) - (double)this.getBbWidth(), 0.0D, 0.0D, 0.0D);
            }

            --this.forcedAgeTimer;
         }
      } else if(this.isAlive()) {
         int var1 = this.getAge();
         if(var1 < 0) {
            ++var1;
            this.setAge(var1);
         } else if(var1 > 0) {
            --var1;
            this.setAge(var1);
         }
      }

   }

   protected void ageBoundaryReached() {
   }

   public boolean isBaby() {
      return this.getAge() < 0;
   }
}
