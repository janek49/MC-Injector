package net.minecraft.world.entity.projectile;

import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EyeOfEnder extends Entity implements ItemSupplier {
   private static final EntityDataAccessor DATA_ITEM_STACK = SynchedEntityData.defineId(EyeOfEnder.class, EntityDataSerializers.ITEM_STACK);
   private double tx;
   private double ty;
   private double tz;
   private int life;
   private boolean surviveAfterDeath;

   public EyeOfEnder(EntityType entityType, Level level) {
      super(entityType, level);
   }

   public EyeOfEnder(Level level, double var2, double var4, double var6) {
      this(EntityType.EYE_OF_ENDER, level);
      this.life = 0;
      this.setPos(var2, var4, var6);
   }

   public void setItem(ItemStack item) {
      if(item.getItem() != Items.ENDER_EYE || item.hasTag()) {
         this.getEntityData().set(DATA_ITEM_STACK, Util.make(item.copy(), (itemStack) -> {
            itemStack.setCount(1);
         }));
      }

   }

   private ItemStack getItemRaw() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
   }

   public ItemStack getItem() {
      ItemStack itemStack = this.getItemRaw();
      return itemStack.isEmpty()?new ItemStack(Items.ENDER_EYE):itemStack;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      double var3 = this.getBoundingBox().getSize() * 4.0D;
      if(Double.isNaN(var3)) {
         var3 = 4.0D;
      }

      var3 = var3 * 64.0D;
      return d < var3 * var3;
   }

   public void signalTo(BlockPos blockPos) {
      double var2 = (double)blockPos.getX();
      int var4 = blockPos.getY();
      double var5 = (double)blockPos.getZ();
      double var7 = var2 - this.x;
      double var9 = var5 - this.z;
      float var11 = Mth.sqrt(var7 * var7 + var9 * var9);
      if(var11 > 12.0F) {
         this.tx = this.x + var7 / (double)var11 * 12.0D;
         this.tz = this.z + var9 / (double)var11 * 12.0D;
         this.ty = this.y + 8.0D;
      } else {
         this.tx = var2;
         this.ty = (double)var4;
         this.tz = var5;
      }

      this.life = 0;
      this.surviveAfterDeath = this.random.nextInt(5) > 0;
   }

   public void lerpMotion(double var1, double var3, double var5) {
      this.setDeltaMovement(var1, var3, var5);
      if(this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float var7 = Mth.sqrt(var1 * var1 + var5 * var5);
         this.yRot = (float)(Mth.atan2(var1, var5) * 57.2957763671875D);
         this.xRot = (float)(Mth.atan2(var3, (double)var7) * 57.2957763671875D);
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
      }

   }

   public void tick() {
      this.xOld = this.x;
      this.yOld = this.y;
      this.zOld = this.z;
      super.tick();
      Vec3 var1 = this.getDeltaMovement();
      this.x += var1.x;
      this.y += var1.y;
      this.z += var1.z;
      float var2 = Mth.sqrt(getHorizontalDistanceSqr(var1));
      this.yRot = (float)(Mth.atan2(var1.x, var1.z) * 57.2957763671875D);

      for(this.xRot = (float)(Mth.atan2(var1.y, (double)var2) * 57.2957763671875D); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
         ;
      }

      while(this.xRot - this.xRotO >= 180.0F) {
         this.xRotO += 360.0F;
      }

      while(this.yRot - this.yRotO < -180.0F) {
         this.yRotO -= 360.0F;
      }

      while(this.yRot - this.yRotO >= 180.0F) {
         this.yRotO += 360.0F;
      }

      this.xRot = Mth.lerp(0.2F, this.xRotO, this.xRot);
      this.yRot = Mth.lerp(0.2F, this.yRotO, this.yRot);
      if(!this.level.isClientSide) {
         double var3 = this.tx - this.x;
         double var5 = this.tz - this.z;
         float var7 = (float)Math.sqrt(var3 * var3 + var5 * var5);
         float var8 = (float)Mth.atan2(var5, var3);
         double var9 = Mth.lerp(0.0025D, (double)var2, (double)var7);
         double var11 = var1.y;
         if(var7 < 1.0F) {
            var9 *= 0.8D;
            var11 *= 0.8D;
         }

         int var13 = this.y < this.ty?1:-1;
         var1 = new Vec3(Math.cos((double)var8) * var9, var11 + ((double)var13 - var11) * 0.014999999664723873D, Math.sin((double)var8) * var9);
         this.setDeltaMovement(var1);
      }

      float var3 = 0.25F;
      if(this.isInWater()) {
         for(int var4 = 0; var4 < 4; ++var4) {
            this.level.addParticle(ParticleTypes.BUBBLE, this.x - var1.x * 0.25D, this.y - var1.y * 0.25D, this.z - var1.z * 0.25D, var1.x, var1.y, var1.z);
         }
      } else {
         this.level.addParticle(ParticleTypes.PORTAL, this.x - var1.x * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, this.y - var1.y * 0.25D - 0.5D, this.z - var1.z * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, var1.x, var1.y, var1.z);
      }

      if(!this.level.isClientSide) {
         this.setPos(this.x, this.y, this.z);
         ++this.life;
         if(this.life > 80 && !this.level.isClientSide) {
            this.playSound(SoundEvents.ENDER_EYE_DEATH, 1.0F, 1.0F);
            this.remove();
            if(this.surviveAfterDeath) {
               this.level.addFreshEntity(new ItemEntity(this.level, this.x, this.y, this.z, this.getItem()));
            } else {
               this.level.levelEvent(2003, new BlockPos(this), 0);
            }
         }
      }

   }

   public void addAdditionalSaveData(CompoundTag compoundTag) {
      ItemStack var2 = this.getItemRaw();
      if(!var2.isEmpty()) {
         compoundTag.put("Item", var2.save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag compoundTag) {
      ItemStack var2 = ItemStack.of(compoundTag.getCompound("Item"));
      this.setItem(var2);
   }

   public float getBrightness() {
      return 1.0F;
   }

   public int getLightColor() {
      return 15728880;
   }

   public boolean isAttackable() {
      return false;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
