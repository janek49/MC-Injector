package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

public class MushroomCow extends Cow {
   private static final EntityDataAccessor DATA_TYPE = SynchedEntityData.defineId(MushroomCow.class, EntityDataSerializers.STRING);
   private MobEffect effect;
   private int effectDuration;
   private UUID lastLightningBoltUUID;

   public MushroomCow(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public float getWalkTargetValue(BlockPos blockPos, LevelReader levelReader) {
      return levelReader.getBlockState(blockPos.below()).getBlock() == Blocks.MYCELIUM?10.0F:levelReader.getBrightness(blockPos) - 0.5F;
   }

   public static boolean checkMushroomSpawnRules(EntityType entityType, LevelAccessor levelAccessor, MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
      return levelAccessor.getBlockState(blockPos.below()).getBlock() == Blocks.MYCELIUM && levelAccessor.getRawBrightness(blockPos, 0) > 8;
   }

   public void thunderHit(LightningBolt lightningBolt) {
      UUID var2 = lightningBolt.getUUID();
      if(!var2.equals(this.lastLightningBoltUUID)) {
         this.setMushroomType(this.getMushroomType() == MushroomCow.MushroomType.RED?MushroomCow.MushroomType.BROWN:MushroomCow.MushroomType.RED);
         this.lastLightningBoltUUID = var2;
         this.playSound(SoundEvents.MOOSHROOM_CONVERT, 2.0F, 1.0F);
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TYPE, MushroomCow.MushroomType.RED.type);
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      if(var3.getItem() == Items.BOWL && this.getAge() >= 0 && !player.abilities.instabuild) {
         var3.shrink(1);
         boolean var5 = false;
         ItemStack var4;
         if(this.effect != null) {
            var5 = true;
            var4 = new ItemStack(Items.SUSPICIOUS_STEW);
            SuspiciousStewItem.saveMobEffect(var4, this.effect, this.effectDuration);
            this.effect = null;
            this.effectDuration = 0;
         } else {
            var4 = new ItemStack(Items.MUSHROOM_STEW);
         }

         if(var3.isEmpty()) {
            player.setItemInHand(interactionHand, var4);
         } else if(!player.inventory.add(var4)) {
            player.drop(var4, false);
         }

         SoundEvent var6;
         if(var5) {
            var6 = SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY;
         } else {
            var6 = SoundEvents.MOOSHROOM_MILK;
         }

         this.playSound(var6, 1.0F, 1.0F);
         return true;
      } else if(var3.getItem() == Items.SHEARS && this.getAge() >= 0) {
         this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y + (double)(this.getBbHeight() / 2.0F), this.z, 0.0D, 0.0D, 0.0D);
         if(!this.level.isClientSide) {
            this.remove();
            Cow var4 = (Cow)EntityType.COW.create(this.level);
            var4.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
            var4.setHealth(this.getHealth());
            var4.yBodyRot = this.yBodyRot;
            if(this.hasCustomName()) {
               var4.setCustomName(this.getCustomName());
            }

            this.level.addFreshEntity(var4);

            for(int var5 = 0; var5 < 5; ++var5) {
               this.level.addFreshEntity(new ItemEntity(this.level, this.x, this.y + (double)this.getBbHeight(), this.z, new ItemStack(this.getMushroomType().blockState.getBlock())));
            }

            var3.hurtAndBreak(1, player, (player) -> {
               player.broadcastBreakEvent(interactionHand);
            });
            this.playSound(SoundEvents.MOOSHROOM_SHEAR, 1.0F, 1.0F);
         }

         return true;
      } else {
         if(this.getMushroomType() == MushroomCow.MushroomType.BROWN && var3.getItem().is(ItemTags.SMALL_FLOWERS)) {
            if(this.effect != null) {
               for(int var4 = 0; var4 < 2; ++var4) {
                  this.level.addParticle(ParticleTypes.SMOKE, this.x + (double)(this.random.nextFloat() / 2.0F), this.y + (double)(this.getBbHeight() / 2.0F), this.z + (double)(this.random.nextFloat() / 2.0F), 0.0D, (double)(this.random.nextFloat() / 5.0F), 0.0D);
               }
            } else {
               Pair<MobEffect, Integer> var4 = this.getEffectFromItemStack(var3);
               if(!player.abilities.instabuild) {
                  var3.shrink(1);
               }

               for(int var5 = 0; var5 < 4; ++var5) {
                  this.level.addParticle(ParticleTypes.EFFECT, this.x + (double)(this.random.nextFloat() / 2.0F), this.y + (double)(this.getBbHeight() / 2.0F), this.z + (double)(this.random.nextFloat() / 2.0F), 0.0D, (double)(this.random.nextFloat() / 5.0F), 0.0D);
               }

               this.effect = (MobEffect)var4.getLeft();
               this.effectDuration = ((Integer)var4.getRight()).intValue();
               this.playSound(SoundEvents.MOOSHROOM_EAT, 2.0F, 1.0F);
            }
         }

         return super.mobInteract(player, interactionHand);
      }
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putString("Type", this.getMushroomType().type);
      if(this.effect != null) {
         compoundTag.putByte("EffectId", (byte)MobEffect.getId(this.effect));
         compoundTag.putInt("EffectDuration", this.effectDuration);
      }

   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setMushroomType(MushroomCow.MushroomType.byType(compoundTag.getString("Type")));
      if(compoundTag.contains("EffectId", 1)) {
         this.effect = MobEffect.byId(compoundTag.getByte("EffectId"));
      }

      if(compoundTag.contains("EffectDuration", 3)) {
         this.effectDuration = compoundTag.getInt("EffectDuration");
      }

   }

   private Pair getEffectFromItemStack(ItemStack itemStack) {
      FlowerBlock var2 = (FlowerBlock)((BlockItem)itemStack.getItem()).getBlock();
      return Pair.of(var2.getSuspiciousStewEffect(), Integer.valueOf(var2.getEffectDuration()));
   }

   private void setMushroomType(MushroomCow.MushroomType mushroomType) {
      this.entityData.set(DATA_TYPE, mushroomType.type);
   }

   public MushroomCow.MushroomType getMushroomType() {
      return MushroomCow.MushroomType.byType((String)this.entityData.get(DATA_TYPE));
   }

   public MushroomCow getBreedOffspring(AgableMob agableMob) {
      MushroomCow mushroomCow = (MushroomCow)EntityType.MOOSHROOM.create(this.level);
      mushroomCow.setMushroomType(this.getOffspringType((MushroomCow)agableMob));
      return mushroomCow;
   }

   private MushroomCow.MushroomType getOffspringType(MushroomCow mushroomCow) {
      MushroomCow.MushroomType mushroomCow$MushroomType = this.getMushroomType();
      MushroomCow.MushroomType var3 = mushroomCow.getMushroomType();
      MushroomCow.MushroomType var4;
      if(mushroomCow$MushroomType == var3 && this.random.nextInt(1024) == 0) {
         var4 = mushroomCow$MushroomType == MushroomCow.MushroomType.BROWN?MushroomCow.MushroomType.RED:MushroomCow.MushroomType.BROWN;
      } else {
         var4 = this.random.nextBoolean()?mushroomCow$MushroomType:var3;
      }

      return var4;
   }

   // $FF: synthetic method
   public Cow getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }

   public static enum MushroomType {
      RED("red", Blocks.RED_MUSHROOM.defaultBlockState()),
      BROWN("brown", Blocks.BROWN_MUSHROOM.defaultBlockState());

      private final String type;
      private final BlockState blockState;

      private MushroomType(String type, BlockState blockState) {
         this.type = type;
         this.blockState = blockState;
      }

      public BlockState getBlockState() {
         return this.blockState;
      }

      private static MushroomCow.MushroomType byType(String type) {
         for(MushroomCow.MushroomType var4 : values()) {
            if(var4.type.equals(type)) {
               return var4;
            }
         }

         return RED;
      }
   }
}
