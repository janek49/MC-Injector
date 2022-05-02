package net.minecraft.world.level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class Explosion {
   private final boolean fire;
   private final Explosion.BlockInteraction blockInteraction;
   private final Random random;
   private final Level level;
   private final double x;
   private final double y;
   private final double z;
   private final Entity source;
   private final float radius;
   private DamageSource damageSource;
   private final List toBlow;
   private final Map hitPlayers;

   public Explosion(Level level, @Nullable Entity entity, double var3, double var5, double var7, float var9, List list) {
      this(level, entity, var3, var5, var7, var9, false, Explosion.BlockInteraction.DESTROY, list);
   }

   public Explosion(Level level, @Nullable Entity entity, double var3, double var5, double var7, float var9, boolean var10, Explosion.BlockInteraction explosion$BlockInteraction, List list) {
      this(level, entity, var3, var5, var7, var9, var10, explosion$BlockInteraction);
      this.toBlow.addAll(list);
   }

   public Explosion(Level level, @Nullable Entity source, double x, double y, double z, float radius, boolean fire, Explosion.BlockInteraction blockInteraction) {
      this.random = new Random();
      this.toBlow = Lists.newArrayList();
      this.hitPlayers = Maps.newHashMap();
      this.level = level;
      this.source = source;
      this.radius = radius;
      this.x = x;
      this.y = y;
      this.z = z;
      this.fire = fire;
      this.blockInteraction = blockInteraction;
      this.damageSource = DamageSource.explosion(this);
   }

   public static float getSeenPercent(Vec3 vec3, Entity entity) {
      AABB var2 = entity.getBoundingBox();
      double var3 = 1.0D / ((var2.maxX - var2.minX) * 2.0D + 1.0D);
      double var5 = 1.0D / ((var2.maxY - var2.minY) * 2.0D + 1.0D);
      double var7 = 1.0D / ((var2.maxZ - var2.minZ) * 2.0D + 1.0D);
      double var9 = (1.0D - Math.floor(1.0D / var3) * var3) / 2.0D;
      double var11 = (1.0D - Math.floor(1.0D / var7) * var7) / 2.0D;
      if(var3 >= 0.0D && var5 >= 0.0D && var7 >= 0.0D) {
         int var13 = 0;
         int var14 = 0;

         for(float var15 = 0.0F; var15 <= 1.0F; var15 = (float)((double)var15 + var3)) {
            for(float var16 = 0.0F; var16 <= 1.0F; var16 = (float)((double)var16 + var5)) {
               for(float var17 = 0.0F; var17 <= 1.0F; var17 = (float)((double)var17 + var7)) {
                  double var18 = Mth.lerp((double)var15, var2.minX, var2.maxX);
                  double var20 = Mth.lerp((double)var16, var2.minY, var2.maxY);
                  double var22 = Mth.lerp((double)var17, var2.minZ, var2.maxZ);
                  Vec3 var24 = new Vec3(var18 + var9, var20, var22 + var11);
                  if(entity.level.clip(new ClipContext(var24, vec3, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS) {
                     ++var13;
                  }

                  ++var14;
               }
            }
         }

         return (float)var13 / (float)var14;
      } else {
         return 0.0F;
      }
   }

   public void explode() {
      Set<BlockPos> var1 = Sets.newHashSet();
      int var2 = 16;

      for(int var3 = 0; var3 < 16; ++var3) {
         for(int var4 = 0; var4 < 16; ++var4) {
            for(int var5 = 0; var5 < 16; ++var5) {
               if(var3 == 0 || var3 == 15 || var4 == 0 || var4 == 15 || var5 == 0 || var5 == 15) {
                  double var6 = (double)((float)var3 / 15.0F * 2.0F - 1.0F);
                  double var8 = (double)((float)var4 / 15.0F * 2.0F - 1.0F);
                  double var10 = (double)((float)var5 / 15.0F * 2.0F - 1.0F);
                  double var12 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
                  var6 = var6 / var12;
                  var8 = var8 / var12;
                  var10 = var10 / var12;
                  float var14 = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                  double var15 = this.x;
                  double var17 = this.y;
                  double var19 = this.z;

                  for(float var21 = 0.3F; var14 > 0.0F; var14 -= 0.22500001F) {
                     BlockPos var22 = new BlockPos(var15, var17, var19);
                     BlockState var23 = this.level.getBlockState(var22);
                     FluidState var24 = this.level.getFluidState(var22);
                     if(!var23.isAir() || !var24.isEmpty()) {
                        float var25 = Math.max(var23.getBlock().getExplosionResistance(), var24.getExplosionResistance());
                        if(this.source != null) {
                           var25 = this.source.getBlockExplosionResistance(this, this.level, var22, var23, var24, var25);
                        }

                        var14 -= (var25 + 0.3F) * 0.3F;
                     }

                     if(var14 > 0.0F && (this.source == null || this.source.shouldBlockExplode(this, this.level, var22, var23, var14))) {
                        var1.add(var22);
                     }

                     var15 += var6 * 0.30000001192092896D;
                     var17 += var8 * 0.30000001192092896D;
                     var19 += var10 * 0.30000001192092896D;
                  }
               }
            }
         }
      }

      this.toBlow.addAll(var1);
      float var3 = this.radius * 2.0F;
      int var4 = Mth.floor(this.x - (double)var3 - 1.0D);
      int var5 = Mth.floor(this.x + (double)var3 + 1.0D);
      int var6 = Mth.floor(this.y - (double)var3 - 1.0D);
      int var7 = Mth.floor(this.y + (double)var3 + 1.0D);
      int var8 = Mth.floor(this.z - (double)var3 - 1.0D);
      int var9 = Mth.floor(this.z + (double)var3 + 1.0D);
      List<Entity> var10 = this.level.getEntities(this.source, new AABB((double)var4, (double)var6, (double)var8, (double)var5, (double)var7, (double)var9));
      Vec3 var11 = new Vec3(this.x, this.y, this.z);

      for(int var12 = 0; var12 < var10.size(); ++var12) {
         Entity var13 = (Entity)var10.get(var12);
         if(!var13.ignoreExplosion()) {
            double var14 = (double)(Mth.sqrt(var13.distanceToSqr(new Vec3(this.x, this.y, this.z))) / var3);
            if(var14 <= 1.0D) {
               double var16 = var13.x - this.x;
               double var18 = var13.y + (double)var13.getEyeHeight() - this.y;
               double var20 = var13.z - this.z;
               double var22 = (double)Mth.sqrt(var16 * var16 + var18 * var18 + var20 * var20);
               if(var22 != 0.0D) {
                  var16 = var16 / var22;
                  var18 = var18 / var22;
                  var20 = var20 / var22;
                  double var24 = (double)getSeenPercent(var11, var13);
                  double var26 = (1.0D - var14) * var24;
                  var13.hurt(this.getDamageSource(), (float)((int)((var26 * var26 + var26) / 2.0D * 7.0D * (double)var3 + 1.0D)));
                  double var28 = var26;
                  if(var13 instanceof LivingEntity) {
                     var28 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)var13, var26);
                  }

                  var13.setDeltaMovement(var13.getDeltaMovement().add(var16 * var28, var18 * var28, var20 * var28));
                  if(var13 instanceof Player) {
                     Player var30 = (Player)var13;
                     if(!var30.isSpectator() && (!var30.isCreative() || !var30.abilities.flying)) {
                        this.hitPlayers.put(var30, new Vec3(var16 * var26, var18 * var26, var20 * var26));
                     }
                  }
               }
            }
         }
      }

   }

   public void finalizeExplosion(boolean b) {
      this.level.playSound((Player)null, this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F);
      boolean var2 = this.blockInteraction != Explosion.BlockInteraction.NONE;
      if(this.radius >= 2.0F && var2) {
         this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
      } else {
         this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
      }

      if(var2) {
         for(BlockPos var4 : this.toBlow) {
            BlockState var5 = this.level.getBlockState(var4);
            Block var6 = var5.getBlock();
            if(b) {
               double var7 = (double)((float)var4.getX() + this.level.random.nextFloat());
               double var9 = (double)((float)var4.getY() + this.level.random.nextFloat());
               double var11 = (double)((float)var4.getZ() + this.level.random.nextFloat());
               double var13 = var7 - this.x;
               double var15 = var9 - this.y;
               double var17 = var11 - this.z;
               double var19 = (double)Mth.sqrt(var13 * var13 + var15 * var15 + var17 * var17);
               var13 = var13 / var19;
               var15 = var15 / var19;
               var17 = var17 / var19;
               double var21 = 0.5D / (var19 / (double)this.radius + 0.1D);
               var21 = var21 * (double)(this.level.random.nextFloat() * this.level.random.nextFloat() + 0.3F);
               var13 = var13 * var21;
               var15 = var15 * var21;
               var17 = var17 * var21;
               this.level.addParticle(ParticleTypes.POOF, (var7 + this.x) / 2.0D, (var9 + this.y) / 2.0D, (var11 + this.z) / 2.0D, var13, var15, var17);
               this.level.addParticle(ParticleTypes.SMOKE, var7, var9, var11, var13, var15, var17);
            }

            if(!var5.isAir()) {
               if(var6.dropFromExplosion(this) && this.level instanceof ServerLevel) {
                  BlockEntity var7 = var6.isEntityBlock()?this.level.getBlockEntity(var4):null;
                  LootContext.Builder var8 = (new LootContext.Builder((ServerLevel)this.level)).withRandom(this.level.random).withParameter(LootContextParams.BLOCK_POS, var4).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, var7);
                  if(this.blockInteraction == Explosion.BlockInteraction.DESTROY) {
                     var8.withParameter(LootContextParams.EXPLOSION_RADIUS, Float.valueOf(this.radius));
                  }

                  Block.dropResources(var5, var8);
               }

               this.level.setBlock(var4, Blocks.AIR.defaultBlockState(), 3);
               var6.wasExploded(this.level, var4, this);
            }
         }
      }

      if(this.fire) {
         for(BlockPos var4 : this.toBlow) {
            if(this.level.getBlockState(var4).isAir() && this.level.getBlockState(var4.below()).isSolidRender(this.level, var4.below()) && this.random.nextInt(3) == 0) {
               this.level.setBlockAndUpdate(var4, Blocks.FIRE.defaultBlockState());
            }
         }
      }

   }

   public DamageSource getDamageSource() {
      return this.damageSource;
   }

   public void setDamageSource(DamageSource damageSource) {
      this.damageSource = damageSource;
   }

   public Map getHitPlayers() {
      return this.hitPlayers;
   }

   @Nullable
   public LivingEntity getSourceMob() {
      return this.source == null?null:(this.source instanceof PrimedTnt?((PrimedTnt)this.source).getOwner():(this.source instanceof LivingEntity?(LivingEntity)this.source:null));
   }

   public void clearToBlow() {
      this.toBlow.clear();
   }

   public List getToBlow() {
      return this.toBlow;
   }

   public static enum BlockInteraction {
      NONE,
      BREAK,
      DESTROY;
   }
}
