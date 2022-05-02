package net.minecraft.world.entity.projectile;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ShulkerBullet extends Entity {
   private LivingEntity owner;
   private Entity finalTarget;
   @Nullable
   private Direction currentMoveDirection;
   private int flightSteps;
   private double targetDeltaX;
   private double targetDeltaY;
   private double targetDeltaZ;
   @Nullable
   private UUID ownerId;
   private BlockPos lastKnownOwnerPos;
   @Nullable
   private UUID targetId;
   private BlockPos lastKnownTargetPos;

   public ShulkerBullet(EntityType entityType, Level level) {
      super(entityType, level);
      this.noPhysics = true;
   }

   public ShulkerBullet(Level level, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(EntityType.SHULKER_BULLET, level);
      this.moveTo(var2, var4, var6, this.yRot, this.xRot);
      this.setDeltaMovement(var8, var10, var12);
   }

   public ShulkerBullet(Level level, LivingEntity owner, Entity finalTarget, Direction.Axis direction$Axis) {
      this(EntityType.SHULKER_BULLET, level);
      this.owner = owner;
      BlockPos var5 = new BlockPos(owner);
      double var6 = (double)var5.getX() + 0.5D;
      double var8 = (double)var5.getY() + 0.5D;
      double var10 = (double)var5.getZ() + 0.5D;
      this.moveTo(var6, var8, var10, this.yRot, this.xRot);
      this.finalTarget = finalTarget;
      this.currentMoveDirection = Direction.UP;
      this.selectNextMoveDirection(direction$Axis);
   }

   public SoundSource getSoundSource() {
      return SoundSource.HOSTILE;
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      if(this.owner != null) {
         BlockPos var2 = new BlockPos(this.owner);
         CompoundTag var3 = NbtUtils.createUUIDTag(this.owner.getUUID());
         var3.putInt("X", var2.getX());
         var3.putInt("Y", var2.getY());
         var3.putInt("Z", var2.getZ());
         compoundTag.put("Owner", var3);
      }

      if(this.finalTarget != null) {
         BlockPos var2 = new BlockPos(this.finalTarget);
         CompoundTag var3 = NbtUtils.createUUIDTag(this.finalTarget.getUUID());
         var3.putInt("X", var2.getX());
         var3.putInt("Y", var2.getY());
         var3.putInt("Z", var2.getZ());
         compoundTag.put("Target", var3);
      }

      if(this.currentMoveDirection != null) {
         compoundTag.putInt("Dir", this.currentMoveDirection.get3DDataValue());
      }

      compoundTag.putInt("Steps", this.flightSteps);
      compoundTag.putDouble("TXD", this.targetDeltaX);
      compoundTag.putDouble("TYD", this.targetDeltaY);
      compoundTag.putDouble("TZD", this.targetDeltaZ);
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      this.flightSteps = compoundTag.getInt("Steps");
      this.targetDeltaX = compoundTag.getDouble("TXD");
      this.targetDeltaY = compoundTag.getDouble("TYD");
      this.targetDeltaZ = compoundTag.getDouble("TZD");
      if(compoundTag.contains("Dir", 99)) {
         this.currentMoveDirection = Direction.from3DDataValue(compoundTag.getInt("Dir"));
      }

      if(compoundTag.contains("Owner", 10)) {
         CompoundTag compoundTag = compoundTag.getCompound("Owner");
         this.ownerId = NbtUtils.loadUUIDTag(compoundTag);
         this.lastKnownOwnerPos = new BlockPos(compoundTag.getInt("X"), compoundTag.getInt("Y"), compoundTag.getInt("Z"));
      }

      if(compoundTag.contains("Target", 10)) {
         CompoundTag compoundTag = compoundTag.getCompound("Target");
         this.targetId = NbtUtils.loadUUIDTag(compoundTag);
         this.lastKnownTargetPos = new BlockPos(compoundTag.getInt("X"), compoundTag.getInt("Y"), compoundTag.getInt("Z"));
      }

   }

   protected void defineSynchedData() {
   }

   private void setMoveDirection(@Nullable Direction moveDirection) {
      this.currentMoveDirection = moveDirection;
   }

   private void selectNextMoveDirection(@Nullable Direction.Axis direction$Axis) {
      double var3 = 0.5D;
      BlockPos var2;
      if(this.finalTarget == null) {
         var2 = (new BlockPos(this)).below();
      } else {
         var3 = (double)this.finalTarget.getBbHeight() * 0.5D;
         var2 = new BlockPos(this.finalTarget.x, this.finalTarget.y + var3, this.finalTarget.z);
      }

      double var5 = (double)var2.getX() + 0.5D;
      double var7 = (double)var2.getY() + var3;
      double var9 = (double)var2.getZ() + 0.5D;
      Direction var11 = null;
      if(!var2.closerThan(this.position(), 2.0D)) {
         BlockPos var12 = new BlockPos(this);
         List<Direction> var13 = Lists.newArrayList();
         if(direction$Axis != Direction.Axis.X) {
            if(var12.getX() < var2.getX() && this.level.isEmptyBlock(var12.east())) {
               var13.add(Direction.EAST);
            } else if(var12.getX() > var2.getX() && this.level.isEmptyBlock(var12.west())) {
               var13.add(Direction.WEST);
            }
         }

         if(direction$Axis != Direction.Axis.Y) {
            if(var12.getY() < var2.getY() && this.level.isEmptyBlock(var12.above())) {
               var13.add(Direction.UP);
            } else if(var12.getY() > var2.getY() && this.level.isEmptyBlock(var12.below())) {
               var13.add(Direction.DOWN);
            }
         }

         if(direction$Axis != Direction.Axis.Z) {
            if(var12.getZ() < var2.getZ() && this.level.isEmptyBlock(var12.south())) {
               var13.add(Direction.SOUTH);
            } else if(var12.getZ() > var2.getZ() && this.level.isEmptyBlock(var12.north())) {
               var13.add(Direction.NORTH);
            }
         }

         var11 = Direction.getRandomFace(this.random);
         if(var13.isEmpty()) {
            for(int var14 = 5; !this.level.isEmptyBlock(var12.relative(var11)) && var14 > 0; --var14) {
               var11 = Direction.getRandomFace(this.random);
            }
         } else {
            var11 = (Direction)var13.get(this.random.nextInt(var13.size()));
         }

         var5 = this.x + (double)var11.getStepX();
         var7 = this.y + (double)var11.getStepY();
         var9 = this.z + (double)var11.getStepZ();
      }

      this.setMoveDirection(var11);
      double var12 = var5 - this.x;
      double var14 = var7 - this.y;
      double var16 = var9 - this.z;
      double var18 = (double)Mth.sqrt(var12 * var12 + var14 * var14 + var16 * var16);
      if(var18 == 0.0D) {
         this.targetDeltaX = 0.0D;
         this.targetDeltaY = 0.0D;
         this.targetDeltaZ = 0.0D;
      } else {
         this.targetDeltaX = var12 / var18 * 0.15D;
         this.targetDeltaY = var14 / var18 * 0.15D;
         this.targetDeltaZ = var16 / var18 * 0.15D;
      }

      this.hasImpulse = true;
      this.flightSteps = 10 + this.random.nextInt(5) * 10;
   }

   public void tick() {
      if(!this.level.isClientSide && this.level.getDifficulty() == Difficulty.PEACEFUL) {
         this.remove();
      } else {
         super.tick();
         if(!this.level.isClientSide) {
            if(this.finalTarget == null && this.targetId != null) {
               for(LivingEntity var3 : this.level.getEntitiesOfClass(LivingEntity.class, new AABB(this.lastKnownTargetPos.offset(-2, -2, -2), this.lastKnownTargetPos.offset(2, 2, 2)))) {
                  if(var3.getUUID().equals(this.targetId)) {
                     this.finalTarget = var3;
                     break;
                  }
               }

               this.targetId = null;
            }

            if(this.owner == null && this.ownerId != null) {
               for(LivingEntity var3 : this.level.getEntitiesOfClass(LivingEntity.class, new AABB(this.lastKnownOwnerPos.offset(-2, -2, -2), this.lastKnownOwnerPos.offset(2, 2, 2)))) {
                  if(var3.getUUID().equals(this.ownerId)) {
                     this.owner = var3;
                     break;
                  }
               }

               this.ownerId = null;
            }

            if(this.finalTarget == null || !this.finalTarget.isAlive() || this.finalTarget instanceof Player && ((Player)this.finalTarget).isSpectator()) {
               if(!this.isNoGravity()) {
                  this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.04D, 0.0D));
               }
            } else {
               this.targetDeltaX = Mth.clamp(this.targetDeltaX * 1.025D, -1.0D, 1.0D);
               this.targetDeltaY = Mth.clamp(this.targetDeltaY * 1.025D, -1.0D, 1.0D);
               this.targetDeltaZ = Mth.clamp(this.targetDeltaZ * 1.025D, -1.0D, 1.0D);
               Vec3 var1 = this.getDeltaMovement();
               this.setDeltaMovement(var1.add((this.targetDeltaX - var1.x) * 0.2D, (this.targetDeltaY - var1.y) * 0.2D, (this.targetDeltaZ - var1.z) * 0.2D));
            }

            HitResult var1 = ProjectileUtil.forwardsRaycast(this, true, false, this.owner, ClipContext.Block.COLLIDER);
            if(var1.getType() != HitResult.Type.MISS) {
               this.onHit(var1);
            }
         }

         Vec3 var1 = this.getDeltaMovement();
         this.setPos(this.x + var1.x, this.y + var1.y, this.z + var1.z);
         ProjectileUtil.rotateTowardsMovement(this, 0.5F);
         if(this.level.isClientSide) {
            this.level.addParticle(ParticleTypes.END_ROD, this.x - var1.x, this.y - var1.y + 0.15D, this.z - var1.z, 0.0D, 0.0D, 0.0D);
         } else if(this.finalTarget != null && !this.finalTarget.removed) {
            if(this.flightSteps > 0) {
               --this.flightSteps;
               if(this.flightSteps == 0) {
                  this.selectNextMoveDirection(this.currentMoveDirection == null?null:this.currentMoveDirection.getAxis());
               }
            }

            if(this.currentMoveDirection != null) {
               BlockPos var2 = new BlockPos(this);
               Direction.Axis var3 = this.currentMoveDirection.getAxis();
               if(this.level.loadedAndEntityCanStandOn(var2.relative(this.currentMoveDirection), this)) {
                  this.selectNextMoveDirection(var3);
               } else {
                  BlockPos var4 = new BlockPos(this.finalTarget);
                  if(var3 == Direction.Axis.X && var2.getX() == var4.getX() || var3 == Direction.Axis.Z && var2.getZ() == var4.getZ() || var3 == Direction.Axis.Y && var2.getY() == var4.getY()) {
                     this.selectNextMoveDirection(var3);
                  }
               }
            }
         }

      }
   }

   public boolean isOnFire() {
      return false;
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      return d < 16384.0D;
   }

   public float getBrightness() {
      return 1.0F;
   }

   public int getLightColor() {
      return 15728880;
   }

   protected void onHit(HitResult hitResult) {
      if(hitResult.getType() == HitResult.Type.ENTITY) {
         Entity var2 = ((EntityHitResult)hitResult).getEntity();
         boolean var3 = var2.hurt(DamageSource.indirectMobAttack(this, this.owner).setProjectile(), 4.0F);
         if(var3) {
            this.doEnchantDamageEffects(this.owner, var2);
            if(var2 instanceof LivingEntity) {
               ((LivingEntity)var2).addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200));
            }
         }
      } else {
         ((ServerLevel)this.level).sendParticles(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 2, 0.2D, 0.2D, 0.2D, 0.0D);
         this.playSound(SoundEvents.SHULKER_BULLET_HIT, 1.0F, 1.0F);
      }

      this.remove();
   }

   public boolean isPickable() {
      return true;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      if(!this.level.isClientSide) {
         this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
         ((ServerLevel)this.level).sendParticles(ParticleTypes.CRIT, this.x, this.y, this.z, 15, 0.2D, 0.2D, 0.2D, 0.0D);
         this.remove();
      }

      return true;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}
