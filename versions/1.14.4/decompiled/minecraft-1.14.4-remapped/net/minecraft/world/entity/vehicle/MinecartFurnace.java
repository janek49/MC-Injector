package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class MinecartFurnace extends AbstractMinecart {
   private static final EntityDataAccessor DATA_ID_FUEL = SynchedEntityData.defineId(MinecartFurnace.class, EntityDataSerializers.BOOLEAN);
   private int fuel;
   public double xPush;
   public double zPush;
   private static final Ingredient INGREDIENT = Ingredient.of(new ItemLike[]{Items.COAL, Items.CHARCOAL});

   public MinecartFurnace(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public MinecartFurnace(Level level, double var2, double var4, double var6) {
      super(EntityType.FURNACE_MINECART, level, var2, var4, var6);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.FURNACE;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_FUEL, Boolean.valueOf(false));
   }

   public void tick() {
      super.tick();
      if(this.fuel > 0) {
         --this.fuel;
      }

      if(this.fuel <= 0) {
         this.xPush = 0.0D;
         this.zPush = 0.0D;
      }

      this.setHasFuel(this.fuel > 0);
      if(this.hasFuel() && this.random.nextInt(4) == 0) {
         this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.x, this.y + 0.8D, this.z, 0.0D, 0.0D, 0.0D);
      }

   }

   protected double getMaxSpeed() {
      return 0.2D;
   }

   public void destroy(DamageSource damageSource) {
      super.destroy(damageSource);
      if(!damageSource.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.spawnAtLocation(Blocks.FURNACE);
      }

   }

   protected void moveAlongTrack(BlockPos blockPos, BlockState blockState) {
      super.moveAlongTrack(blockPos, blockState);
      double var3 = this.xPush * this.xPush + this.zPush * this.zPush;
      Vec3 var5 = this.getDeltaMovement();
      if(var3 > 1.0E-4D && getHorizontalDistanceSqr(var5) > 0.001D) {
         var3 = (double)Mth.sqrt(var3);
         this.xPush /= var3;
         this.zPush /= var3;
         if(this.xPush * var5.x + this.zPush * var5.z < 0.0D) {
            this.xPush = 0.0D;
            this.zPush = 0.0D;
         } else {
            double var6 = var3 / this.getMaxSpeed();
            this.xPush *= var6;
            this.zPush *= var6;
         }
      }

   }

   protected void applyNaturalSlowdown() {
      double var1 = this.xPush * this.xPush + this.zPush * this.zPush;
      if(var1 > 1.0E-7D) {
         var1 = (double)Mth.sqrt(var1);
         this.xPush /= var1;
         this.zPush /= var1;
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.8D, 0.0D, 0.8D).add(this.xPush, 0.0D, this.zPush));
      } else {
         this.setDeltaMovement(this.getDeltaMovement().multiply(0.98D, 0.0D, 0.98D));
      }

      super.applyNaturalSlowdown();
   }

   public boolean interact(Player player, InteractionHand interactionHand) {
      ItemStack var3 = player.getItemInHand(interactionHand);
      if(INGREDIENT.test(var3) && this.fuel + 3600 <= 32000) {
         if(!player.abilities.instabuild) {
            var3.shrink(1);
         }

         this.fuel += 3600;
      }

      this.xPush = this.x - player.x;
      this.zPush = this.z - player.z;
      return true;
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      super.addAdditionalSaveData(compoundTag);
      compoundTag.putDouble("PushX", this.xPush);
      compoundTag.putDouble("PushZ", this.zPush);
      compoundTag.putShort("Fuel", (short)this.fuel);
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      super.readAdditionalSaveData(compoundTag);
      this.xPush = compoundTag.getDouble("PushX");
      this.zPush = compoundTag.getDouble("PushZ");
      this.fuel = compoundTag.getShort("Fuel");
   }

   protected boolean hasFuel() {
      return ((Boolean)this.entityData.get(DATA_ID_FUEL)).booleanValue();
   }

   protected void setHasFuel(boolean hasFuel) {
      this.entityData.set(DATA_ID_FUEL, Boolean.valueOf(hasFuel));
   }

   public BlockState getDefaultDisplayBlockState() {
      return (BlockState)((BlockState)Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH)).setValue(FurnaceBlock.LIT, Boolean.valueOf(this.hasFuel()));
   }
}
