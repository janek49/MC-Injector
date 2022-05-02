package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraft.util.WeighedRandom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseSpawner {
   private static final Logger LOGGER = LogManager.getLogger();
   private int spawnDelay = 20;
   private final List spawnPotentials = Lists.newArrayList();
   private SpawnData nextSpawnData = new SpawnData();
   private double spin;
   private double oSpin;
   private int minSpawnDelay = 200;
   private int maxSpawnDelay = 800;
   private int spawnCount = 4;
   private Entity displayEntity;
   private int maxNearbyEntities = 6;
   private int requiredPlayerRange = 16;
   private int spawnRange = 4;

   @Nullable
   private ResourceLocation getEntityId() {
      String var1 = this.nextSpawnData.getTag().getString("id");

      try {
         return StringUtil.isNullOrEmpty(var1)?null:new ResourceLocation(var1);
      } catch (ResourceLocationException var4) {
         BlockPos var3 = this.getPos();
         LOGGER.warn("Invalid entity id \'{}\' at spawner {}:[{},{},{}]", var1, this.getLevel().dimension.getType(), Integer.valueOf(var3.getX()), Integer.valueOf(var3.getY()), Integer.valueOf(var3.getZ()));
         return null;
      }
   }

   public void setEntityId(EntityType entityId) {
      this.nextSpawnData.getTag().putString("id", Registry.ENTITY_TYPE.getKey(entityId).toString());
   }

   private boolean isNearPlayer() {
      BlockPos var1 = this.getPos();
      return this.getLevel().hasNearbyAlivePlayer((double)var1.getX() + 0.5D, (double)var1.getY() + 0.5D, (double)var1.getZ() + 0.5D, (double)this.requiredPlayerRange);
   }

   public void tick() {
      if(!this.isNearPlayer()) {
         this.oSpin = this.spin;
      } else {
         Level var1 = this.getLevel();
         BlockPos var2 = this.getPos();
         if(var1.isClientSide) {
            double var3 = (double)((float)var2.getX() + var1.random.nextFloat());
            double var5 = (double)((float)var2.getY() + var1.random.nextFloat());
            double var7 = (double)((float)var2.getZ() + var1.random.nextFloat());
            var1.addParticle(ParticleTypes.SMOKE, var3, var5, var7, 0.0D, 0.0D, 0.0D);
            var1.addParticle(ParticleTypes.FLAME, var3, var5, var7, 0.0D, 0.0D, 0.0D);
            if(this.spawnDelay > 0) {
               --this.spawnDelay;
            }

            this.oSpin = this.spin;
            this.spin = (this.spin + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0D;
         } else {
            if(this.spawnDelay == -1) {
               this.delay();
            }

            if(this.spawnDelay > 0) {
               --this.spawnDelay;
               return;
            }

            boolean var3 = false;

            for(int var4 = 0; var4 < this.spawnCount; ++var4) {
               CompoundTag var5 = this.nextSpawnData.getTag();
               Optional<EntityType<?>> var6 = EntityType.by(var5);
               if(!var6.isPresent()) {
                  this.delay();
                  return;
               }

               ListTag var7 = var5.getList("Pos", 6);
               int var8 = var7.size();
               double var9 = var8 >= 1?var7.getDouble(0):(double)var2.getX() + (var1.random.nextDouble() - var1.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               double var11 = var8 >= 2?var7.getDouble(1):(double)(var2.getY() + var1.random.nextInt(3) - 1);
               double var13 = var8 >= 3?var7.getDouble(2):(double)var2.getZ() + (var1.random.nextDouble() - var1.random.nextDouble()) * (double)this.spawnRange + 0.5D;
               if(var1.noCollision(((EntityType)var6.get()).getAABB(var9, var11, var13)) && SpawnPlacements.checkSpawnRules((EntityType)var6.get(), var1.getLevel(), MobSpawnType.SPAWNER, new BlockPos(var9, var11, var13), var1.getRandom())) {
                  Entity var15 = EntityType.loadEntityRecursive(var5, var1, (var6) -> {
                     var6.moveTo(var9, var11, var13, var6.yRot, var6.xRot);
                     return var6;
                  });
                  if(var15 == null) {
                     this.delay();
                     return;
                  }

                  int var16 = var1.getEntitiesOfClass(var15.getClass(), (new AABB((double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), (double)(var2.getX() + 1), (double)(var2.getY() + 1), (double)(var2.getZ() + 1))).inflate((double)this.spawnRange)).size();
                  if(var16 >= this.maxNearbyEntities) {
                     this.delay();
                     return;
                  }

                  var15.moveTo(var15.x, var15.y, var15.z, var1.random.nextFloat() * 360.0F, 0.0F);
                  if(var15 instanceof Mob) {
                     Mob var17 = (Mob)var15;
                     if(!var17.checkSpawnRules(var1, MobSpawnType.SPAWNER) || !var17.checkSpawnObstruction(var1)) {
                        continue;
                     }

                     if(this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8)) {
                        ((Mob)var15).finalizeSpawn(var1, var1.getCurrentDifficultyAt(new BlockPos(var15)), MobSpawnType.SPAWNER, (SpawnGroupData)null, (CompoundTag)null);
                     }
                  }

                  this.addWithPassengers(var15);
                  var1.levelEvent(2004, var2, 0);
                  if(var15 instanceof Mob) {
                     ((Mob)var15).spawnAnim();
                  }

                  var3 = true;
               }
            }

            if(var3) {
               this.delay();
            }
         }

      }
   }

   private void addWithPassengers(Entity entity) {
      if(this.getLevel().addFreshEntity(entity)) {
         for(Entity var3 : entity.getPassengers()) {
            this.addWithPassengers(var3);
         }

      }
   }

   private void delay() {
      if(this.maxSpawnDelay <= this.minSpawnDelay) {
         this.spawnDelay = this.minSpawnDelay;
      } else {
         int var10003 = this.maxSpawnDelay - this.minSpawnDelay;
         this.spawnDelay = this.minSpawnDelay + this.getLevel().random.nextInt(var10003);
      }

      if(!this.spawnPotentials.isEmpty()) {
         this.setNextSpawnData((SpawnData)WeighedRandom.getRandomItem(this.getLevel().random, this.spawnPotentials));
      }

      this.broadcastEvent(1);
   }

   public void load(CompoundTag compoundTag) {
      this.spawnDelay = compoundTag.getShort("Delay");
      this.spawnPotentials.clear();
      if(compoundTag.contains("SpawnPotentials", 9)) {
         ListTag var2 = compoundTag.getList("SpawnPotentials", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            this.spawnPotentials.add(new SpawnData(var2.getCompound(var3)));
         }
      }

      if(compoundTag.contains("SpawnData", 10)) {
         this.setNextSpawnData(new SpawnData(1, compoundTag.getCompound("SpawnData")));
      } else if(!this.spawnPotentials.isEmpty()) {
         this.setNextSpawnData((SpawnData)WeighedRandom.getRandomItem(this.getLevel().random, this.spawnPotentials));
      }

      if(compoundTag.contains("MinSpawnDelay", 99)) {
         this.minSpawnDelay = compoundTag.getShort("MinSpawnDelay");
         this.maxSpawnDelay = compoundTag.getShort("MaxSpawnDelay");
         this.spawnCount = compoundTag.getShort("SpawnCount");
      }

      if(compoundTag.contains("MaxNearbyEntities", 99)) {
         this.maxNearbyEntities = compoundTag.getShort("MaxNearbyEntities");
         this.requiredPlayerRange = compoundTag.getShort("RequiredPlayerRange");
      }

      if(compoundTag.contains("SpawnRange", 99)) {
         this.spawnRange = compoundTag.getShort("SpawnRange");
      }

      if(this.getLevel() != null) {
         this.displayEntity = null;
      }

   }

   public CompoundTag save(CompoundTag compoundTag) {
      ResourceLocation var2 = this.getEntityId();
      if(var2 == null) {
         return compoundTag;
      } else {
         compoundTag.putShort("Delay", (short)this.spawnDelay);
         compoundTag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
         compoundTag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
         compoundTag.putShort("SpawnCount", (short)this.spawnCount);
         compoundTag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
         compoundTag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
         compoundTag.putShort("SpawnRange", (short)this.spawnRange);
         compoundTag.put("SpawnData", this.nextSpawnData.getTag().copy());
         ListTag var3 = new ListTag();
         if(this.spawnPotentials.isEmpty()) {
            var3.add(this.nextSpawnData.save());
         } else {
            for(SpawnData var5 : this.spawnPotentials) {
               var3.add(var5.save());
            }
         }

         compoundTag.put("SpawnPotentials", var3);
         return compoundTag;
      }
   }

   public Entity getOrCreateDisplayEntity() {
      if(this.displayEntity == null) {
         this.displayEntity = EntityType.loadEntityRecursive(this.nextSpawnData.getTag(), this.getLevel(), Function.identity());
         if(this.nextSpawnData.getTag().size() == 1 && this.nextSpawnData.getTag().contains("id", 8) && this.displayEntity instanceof Mob) {
            ((Mob)this.displayEntity).finalizeSpawn(this.getLevel(), this.getLevel().getCurrentDifficultyAt(new BlockPos(this.displayEntity)), MobSpawnType.SPAWNER, (SpawnGroupData)null, (CompoundTag)null);
         }
      }

      return this.displayEntity;
   }

   public boolean onEventTriggered(int i) {
      if(i == 1 && this.getLevel().isClientSide) {
         this.spawnDelay = this.minSpawnDelay;
         return true;
      } else {
         return false;
      }
   }

   public void setNextSpawnData(SpawnData nextSpawnData) {
      this.nextSpawnData = nextSpawnData;
   }

   public abstract void broadcastEvent(int var1);

   public abstract Level getLevel();

   public abstract BlockPos getPos();

   public double getSpin() {
      return this.spin;
   }

   public double getoSpin() {
      return this.oSpin;
   }
}
