package net.minecraft.world.entity.animal;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.EatBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class Sheep extends Animal {
   private static final EntityDataAccessor DATA_WOOL_ID = SynchedEntityData.defineId(Sheep.class, EntityDataSerializers.BYTE);
   private static final Map ITEM_BY_DYE = (Map)Util.make(Maps.newEnumMap(DyeColor.class), (enumMap) -> {
      enumMap.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
      enumMap.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
      enumMap.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
      enumMap.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
      enumMap.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
      enumMap.put(DyeColor.LIME, Blocks.LIME_WOOL);
      enumMap.put(DyeColor.PINK, Blocks.PINK_WOOL);
      enumMap.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
      enumMap.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
      enumMap.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
      enumMap.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
      enumMap.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
      enumMap.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
      enumMap.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
      enumMap.put(DyeColor.RED, Blocks.RED_WOOL);
      enumMap.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
   });
   private static final Map COLORARRAY_BY_COLOR = Maps.newEnumMap((Map)Arrays.stream(DyeColor.values()).collect(Collectors.toMap((dyeColor) -> {
      return dyeColor;
   }, Sheep::createSheepColor)));
   private int eatAnimationTick;
   private EatBlockGoal eatBlockGoal;

   private static float[] createSheepColor(DyeColor dyeColor) {
      if(dyeColor == DyeColor.WHITE) {
         return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
      } else {
         float[] floats = dyeColor.getTextureDiffuseColors();
         float var2 = 0.75F;
         return new float[]{floats[0] * 0.75F, floats[1] * 0.75F, floats[2] * 0.75F};
      }
   }

   public static float[] getColorArray(DyeColor dyeColor) {
      return (float[])COLORARRAY_BY_COLOR.get(dyeColor);
   }

   public Sheep(EntityType entityType, Level level) {
      super(entityType, level);
   }

   protected void registerGoals() {
      this.eatBlockGoal = new EatBlockGoal(this);
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(new ItemLike[]{Items.WHEAT}), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(5, this.eatBlockGoal);
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
   }

   protected void customServerAiStep() {
      this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
      super.customServerAiStep();
   }

   public void aiStep() {
      if(this.level.isClientSide) {
         this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      }

      super.aiStep();
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_WOOL_ID, Byte.valueOf((byte)0));
   }

   public ResourceLocation getDefaultLootTable() {
      if(this.isSheared()) {
         return this.getType().getDefaultLootTable();
      } else {
         switch(this.getColor()) {
         case WHITE:
         default:
            return BuiltInLootTables.SHEEP_WHITE;
         case ORANGE:
            return BuiltInLootTables.SHEEP_ORANGE;
         case MAGENTA:
            return BuiltInLootTables.SHEEP_MAGENTA;
         case LIGHT_BLUE:
            return BuiltInLootTables.SHEEP_LIGHT_BLUE;
         case YELLOW:
            return BuiltInLootTables.SHEEP_YELLOW;
         case LIME:
            return BuiltInLootTables.SHEEP_LIME;
         case PINK:
            return BuiltInLootTables.SHEEP_PINK;
         case GRAY:
            return BuiltInLootTables.SHEEP_GRAY;
         case LIGHT_GRAY:
            return BuiltInLootTables.SHEEP_LIGHT_GRAY;
         case CYAN:
            return BuiltInLootTables.SHEEP_CYAN;
         case PURPLE:
            return BuiltInLootTables.SHEEP_PURPLE;
         case BLUE:
            return BuiltInLootTables.SHEEP_BLUE;
         case BROWN:
            return BuiltInLootTables.SHEEP_BROWN;
         case GREEN:
            return BuiltInLootTables.SHEEP_GREEN;
         case RED:
            return BuiltInLootTables.SHEEP_RED;
         case BLACK:
            return BuiltInLootTables.SHEEP_BLACK;
         }
      }
   }

   public void handleEntityEvent(byte b) {
      if(b == 10) {
         this.eatAnimationTick = 40;
      } else {
         super.handleEntityEvent(b);
      }

   }

   public float getHeadEatPositionScale(float f) {
      return this.eatAnimationTick <= 0?0.0F:(this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36?1.0F:(this.eatAnimationTick < 4?((float)this.eatAnimationTick - f) / 4.0F:-((float)(this.eatAnimationTick - 40) - f) / 4.0F));
   }

   public float getHeadEatAngleScale(float f) {
      if(this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
         float var2 = ((float)(this.eatAnimationTick - 4) - f) / 32.0F;
         return 0.62831855F + 0.21991149F * Mth.sin(var2 * 28.7F);
      } else {
         return this.eatAnimationTick > 0?0.62831855F:this.xRot * 0.017453292F;
      }
   }

   public boolean mobInteract(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      if(var3.getItem() == Items.SHEARS && !this.isSheared() && !this.isBaby()) {
         this.shear();
         if(!this.level.isClientSide) {
            var3.hurtAndBreak(1, player, (player) -> {
               player.broadcastBreakEvent(interactionHand);
            });
         }
      }

      return super.mobInteract(player, interactionHand);
   }

   public void shear() {
      if(!this.level.isClientSide) {
         this.setSheared(true);
         int var1 = 1 + this.random.nextInt(3);

         for(int var2 = 0; var2 < var1; ++var2) {
            ItemEntity var3 = this.spawnAtLocation((ItemLike)ITEM_BY_DYE.get(this.getColor()), 1);
            if(var3 != null) {
               var3.setDeltaMovement(var3.getDeltaMovement().add((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(this.random.nextFloat() * 0.05F), (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)));
            }
         }
      }

      this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putBoolean("Sheared", this.isSheared());
      compoundTag.putByte("Color", (byte)this.getColor().getId());
   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.setSheared(compoundTag.getBoolean("Sheared"));
      this.setColor(DyeColor.byId(compoundTag.getByte("Color")));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SHEEP_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSource) {
      return SoundEvents.SHEEP_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SHEEP_DEATH;
   }

   protected void playStepSound(BlockPos blockPos, BlockState blockState) {
      this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
   }

   public DyeColor getColor() {
      return DyeColor.byId(((Byte)this.entityData.get(DATA_WOOL_ID)).byteValue() & 15);
   }

   public void setColor(DyeColor color) {
      byte var2 = ((Byte)this.entityData.get(DATA_WOOL_ID)).byteValue();
      this.entityData.set(DATA_WOOL_ID, Byte.valueOf((byte)(var2 & 240 | color.getId() & 15)));
   }

   public boolean isSheared() {
      return (((Byte)this.entityData.get(DATA_WOOL_ID)).byteValue() & 16) != 0;
   }

   public void setSheared(boolean sheared) {
      byte var2 = ((Byte)this.entityData.get(DATA_WOOL_ID)).byteValue();
      if(sheared) {
         this.entityData.set(DATA_WOOL_ID, Byte.valueOf((byte)(var2 | 16)));
      } else {
         this.entityData.set(DATA_WOOL_ID, Byte.valueOf((byte)(var2 & -17)));
      }

   }

   public static DyeColor getRandomSheepColor(Random random) {
      int var1 = random.nextInt(100);
      return var1 < 5?DyeColor.BLACK:(var1 < 10?DyeColor.GRAY:(var1 < 15?DyeColor.LIGHT_GRAY:(var1 < 18?DyeColor.BROWN:(random.nextInt(500) == 0?DyeColor.PINK:DyeColor.WHITE))));
   }

   public Sheep getBreedOffspring(AgableMob agableMob) {
      Sheep sheep = (Sheep)agableMob;
      Sheep var3 = (Sheep)EntityType.SHEEP.create(this.level);
      var3.setColor(this.getOffspringColor(this, sheep));
      return var3;
   }

   public void ate() {
      this.setSheared(false);
      if(this.isBaby()) {
         this.ageUp(60);
      }

   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor levelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData var4, @Nullable CompoundTag compoundTag) {
      var4 = super.finalizeSpawn(levelAccessor, difficultyInstance, mobSpawnType, var4, compoundTag);
      this.setColor(getRandomSheepColor(levelAccessor.getRandom()));
      return var4;
   }

   private DyeColor getOffspringColor(Animal var1, Animal var2) {
      DyeColor dyeColor = ((Sheep)var1).getColor();
      DyeColor var4 = ((Sheep)var2).getColor();
      CraftingContainer var5 = makeContainer(dyeColor, var4);
      Optional var10000 = this.level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING, var5, this.level).map((craftingRecipe) -> {
         return craftingRecipe.assemble(var5);
      }).map(ItemStack::getItem);
      DyeItem.class.getClass();
      var10000 = var10000.filter(DyeItem.class::isInstance);
      DyeItem.class.getClass();
      return (DyeColor)var10000.map(DyeItem.class::cast).map(DyeItem::getDyeColor).orElseGet(() -> {
         return this.level.random.nextBoolean()?dyeColor:var4;
      });
   }

   private static CraftingContainer makeContainer(DyeColor var0, DyeColor var1) {
      CraftingContainer craftingContainer = new CraftingContainer(new AbstractContainerMenu((MenuType)null, -1) {
         public boolean stillValid(Player player) {
            return false;
         }
      }, 2, 1);
      craftingContainer.setItem(0, new ItemStack(DyeItem.byColor(var0)));
      craftingContainer.setItem(1, new ItemStack(DyeItem.byColor(var1)));
      return craftingContainer;
   }

   protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
      return 0.95F * entityDimensions.height;
   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }
}
