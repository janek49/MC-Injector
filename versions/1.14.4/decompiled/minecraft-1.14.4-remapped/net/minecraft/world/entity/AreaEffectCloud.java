package net.minecraft.world.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AreaEffectCloud extends Entity {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EntityDataAccessor DATA_RADIUS = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor DATA_COLOR = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor DATA_WAITING = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor DATA_PARTICLE = SynchedEntityData.defineId(AreaEffectCloud.class, EntityDataSerializers.PARTICLE);
   private Potion potion;
   private final List effects;
   private final Map victims;
   private int duration;
   private int waitTime;
   private int reapplicationDelay;
   private boolean fixedColor;
   private int durationOnUse;
   private float radiusOnUse;
   private float radiusPerTick;
   private LivingEntity owner;
   private UUID ownerUUID;

   public AreaEffectCloud(EntityType entityType, Level level) {
      super(entityType, level);
      this.potion = Potions.EMPTY;
      this.effects = Lists.newArrayList();
      this.victims = Maps.newHashMap();
      this.duration = 600;
      this.waitTime = 20;
      this.reapplicationDelay = 20;
      this.noPhysics = true;
      this.setRadius(3.0F);
   }

   public AreaEffectCloud(Level level, double var2, double var4, double var6) {
      this(EntityType.AREA_EFFECT_CLOUD, level);
      this.setPos(var2, var4, var6);
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_COLOR, Integer.valueOf(0));
      this.getEntityData().define(DATA_RADIUS, Float.valueOf(0.5F));
      this.getEntityData().define(DATA_WAITING, Boolean.valueOf(false));
      this.getEntityData().define(DATA_PARTICLE, ParticleTypes.ENTITY_EFFECT);
   }

   public void setRadius(float radius) {
      if(!this.level.isClientSide) {
         this.getEntityData().set(DATA_RADIUS, Float.valueOf(radius));
      }

   }

   public void refreshDimensions() {
      double var1 = this.x;
      double var3 = this.y;
      double var5 = this.z;
      super.refreshDimensions();
      this.setPos(var1, var3, var5);
   }

   public float getRadius() {
      return ((Float)this.getEntityData().get(DATA_RADIUS)).floatValue();
   }

   public void setPotion(Potion potion) {
      this.potion = potion;
      if(!this.fixedColor) {
         this.updateColor();
      }

   }

   private void updateColor() {
      if(this.potion == Potions.EMPTY && this.effects.isEmpty()) {
         this.getEntityData().set(DATA_COLOR, Integer.valueOf(0));
      } else {
         this.getEntityData().set(DATA_COLOR, Integer.valueOf(PotionUtils.getColor((Collection)PotionUtils.getAllEffects(this.potion, this.effects))));
      }

   }

   public void addEffect(MobEffectInstance mobEffectInstance) {
      this.effects.add(mobEffectInstance);
      if(!this.fixedColor) {
         this.updateColor();
      }

   }

   public int getColor() {
      return ((Integer)this.getEntityData().get(DATA_COLOR)).intValue();
   }

   public void setFixedColor(int fixedColor) {
      this.fixedColor = true;
      this.getEntityData().set(DATA_COLOR, Integer.valueOf(fixedColor));
   }

   public ParticleOptions getParticle() {
      return (ParticleOptions)this.getEntityData().get(DATA_PARTICLE);
   }

   public void setParticle(ParticleOptions particle) {
      this.getEntityData().set(DATA_PARTICLE, particle);
   }

   protected void setWaiting(boolean waiting) {
      this.getEntityData().set(DATA_WAITING, Boolean.valueOf(waiting));
   }

   public boolean isWaiting() {
      return ((Boolean)this.getEntityData().get(DATA_WAITING)).booleanValue();
   }

   public int getDuration() {
      return this.duration;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   public void tick() {
      super.tick();
      boolean var1 = this.isWaiting();
      float var2 = this.getRadius();
      if(this.level.isClientSide) {
         ParticleOptions var3 = this.getParticle();
         if(var1) {
            if(this.random.nextBoolean()) {
               for(int var4 = 0; var4 < 2; ++var4) {
                  float var5 = this.random.nextFloat() * 6.2831855F;
                  float var6 = Mth.sqrt(this.random.nextFloat()) * 0.2F;
                  float var7 = Mth.cos(var5) * var6;
                  float var8 = Mth.sin(var5) * var6;
                  if(var3.getType() == ParticleTypes.ENTITY_EFFECT) {
                     int var9 = this.random.nextBoolean()?16777215:this.getColor();
                     int var10 = var9 >> 16 & 255;
                     int var11 = var9 >> 8 & 255;
                     int var12 = var9 & 255;
                     this.level.addAlwaysVisibleParticle(var3, this.x + (double)var7, this.y, this.z + (double)var8, (double)((float)var10 / 255.0F), (double)((float)var11 / 255.0F), (double)((float)var12 / 255.0F));
                  } else {
                     this.level.addAlwaysVisibleParticle(var3, this.x + (double)var7, this.y, this.z + (double)var8, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         } else {
            float var4 = 3.1415927F * var2 * var2;

            for(int var5 = 0; (float)var5 < var4; ++var5) {
               float var6 = this.random.nextFloat() * 6.2831855F;
               float var7 = Mth.sqrt(this.random.nextFloat()) * var2;
               float var8 = Mth.cos(var6) * var7;
               float var9 = Mth.sin(var6) * var7;
               if(var3.getType() == ParticleTypes.ENTITY_EFFECT) {
                  int var10 = this.getColor();
                  int var11 = var10 >> 16 & 255;
                  int var12 = var10 >> 8 & 255;
                  int var13 = var10 & 255;
                  this.level.addAlwaysVisibleParticle(var3, this.x + (double)var8, this.y, this.z + (double)var9, (double)((float)var11 / 255.0F), (double)((float)var12 / 255.0F), (double)((float)var13 / 255.0F));
               } else {
                  this.level.addAlwaysVisibleParticle(var3, this.x + (double)var8, this.y, this.z + (double)var9, (0.5D - this.random.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.random.nextDouble()) * 0.15D);
               }
            }
         }
      } else {
         if(this.tickCount >= this.waitTime + this.duration) {
            this.remove();
            return;
         }

         boolean var3 = this.tickCount < this.waitTime;
         if(var1 != var3) {
            this.setWaiting(var3);
         }

         if(var3) {
            return;
         }

         if(this.radiusPerTick != 0.0F) {
            var2 += this.radiusPerTick;
            if(var2 < 0.5F) {
               this.remove();
               return;
            }

            this.setRadius(var2);
         }

         if(this.tickCount % 5 == 0) {
            Iterator<Entry<Entity, Integer>> var4 = this.victims.entrySet().iterator();

            while(var4.hasNext()) {
               Entry<Entity, Integer> var5 = (Entry)var4.next();
               if(this.tickCount >= ((Integer)var5.getValue()).intValue()) {
                  var4.remove();
               }
            }

            var4 = Lists.newArrayList();

            for(MobEffectInstance var6 : this.potion.getEffects()) {
               var4.add(new MobEffectInstance(var6.getEffect(), var6.getDuration() / 4, var6.getAmplifier(), var6.isAmbient(), var6.isVisible()));
            }

            var4.addAll(this.effects);
            if(var4.isEmpty()) {
               this.victims.clear();
            } else {
               List<LivingEntity> var5 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
               if(!var5.isEmpty()) {
                  for(LivingEntity var7 : var5) {
                     if(!this.victims.containsKey(var7) && var7.isAffectedByPotions()) {
                        double var8 = var7.x - this.x;
                        double var10 = var7.z - this.z;
                        double var12 = var8 * var8 + var10 * var10;
                        if(var12 <= (double)(var2 * var2)) {
                           this.victims.put(var7, Integer.valueOf(this.tickCount + this.reapplicationDelay));

                           for(MobEffectInstance var15 : var4) {
                              if(var15.getEffect().isInstantenous()) {
                                 var15.getEffect().applyInstantenousEffect(this, this.getOwner(), var7, var15.getAmplifier(), 0.5D);
                              } else {
                                 var7.addEffect(new MobEffectInstance(var15));
                              }
                           }

                           if(this.radiusOnUse != 0.0F) {
                              var2 += this.radiusOnUse;
                              if(var2 < 0.5F) {
                                 this.remove();
                                 return;
                              }

                              this.setRadius(var2);
                           }

                           if(this.durationOnUse != 0) {
                              this.duration += this.durationOnUse;
                              if(this.duration <= 0) {
                                 this.remove();
                                 return;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void setRadiusOnUse(float radiusOnUse) {
      this.radiusOnUse = radiusOnUse;
   }

   public void setRadiusPerTick(float radiusPerTick) {
      this.radiusPerTick = radiusPerTick;
   }

   public void setWaitTime(int waitTime) {
      this.waitTime = waitTime;
   }

   public void setOwner(@Nullable LivingEntity owner) {
      this.owner = owner;
      this.ownerUUID = owner == null?null:owner.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if(this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
         Entity var1 = ((ServerLevel)this.level).getEntity(this.ownerUUID);
         if(var1 instanceof LivingEntity) {
            this.owner = (LivingEntity)var1;
         }
      }

      return this.owner;
   }

   protected void readAdditionalSaveData(CompoundTag compoundTag) {
      this.tickCount = compoundTag.getInt("Age");
      this.duration = compoundTag.getInt("Duration");
      this.waitTime = compoundTag.getInt("WaitTime");
      this.reapplicationDelay = compoundTag.getInt("ReapplicationDelay");
      this.durationOnUse = compoundTag.getInt("DurationOnUse");
      this.radiusOnUse = compoundTag.getFloat("RadiusOnUse");
      this.radiusPerTick = compoundTag.getFloat("RadiusPerTick");
      this.setRadius(compoundTag.getFloat("Radius"));
      this.ownerUUID = compoundTag.getUUID("OwnerUUID");
      if(compoundTag.contains("Particle", 8)) {
         try {
            this.setParticle(ParticleArgument.readParticle(new StringReader(compoundTag.getString("Particle"))));
         } catch (CommandSyntaxException var5) {
            LOGGER.warn("Couldn\'t load custom particle {}", compoundTag.getString("Particle"), var5);
         }
      }

      if(compoundTag.contains("Color", 99)) {
         this.setFixedColor(compoundTag.getInt("Color"));
      }

      if(compoundTag.contains("Potion", 8)) {
         this.setPotion(PotionUtils.getPotion(compoundTag));
      }

      if(compoundTag.contains("Effects", 9)) {
         ListTag var2 = compoundTag.getList("Effects", 10);
         this.effects.clear();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            MobEffectInstance var4 = MobEffectInstance.load(var2.getCompound(var3));
            if(var4 != null) {
               this.addEffect(var4);
            }
         }
      }

   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putInt("Age", this.tickCount);
      compoundTag.putInt("Duration", this.duration);
      compoundTag.putInt("WaitTime", this.waitTime);
      compoundTag.putInt("ReapplicationDelay", this.reapplicationDelay);
      compoundTag.putInt("DurationOnUse", this.durationOnUse);
      compoundTag.putFloat("RadiusOnUse", this.radiusOnUse);
      compoundTag.putFloat("RadiusPerTick", this.radiusPerTick);
      compoundTag.putFloat("Radius", this.getRadius());
      compoundTag.putString("Particle", this.getParticle().writeToString());
      if(this.ownerUUID != null) {
         compoundTag.putUUID("OwnerUUID", this.ownerUUID);
      }

      if(this.fixedColor) {
         compoundTag.putInt("Color", this.getColor());
      }

      if(this.potion != Potions.EMPTY && this.potion != null) {
         compoundTag.putString("Potion", Registry.POTION.getKey(this.potion).toString());
      }

      if(!this.effects.isEmpty()) {
         ListTag var2 = new ListTag();

         for(MobEffectInstance var4 : this.effects) {
            var2.add(var4.save(new CompoundTag()));
         }

         compoundTag.put("Effects", var2);
      }

   }

   public void onSyncedDataUpdated(EntityDataAccessor entityDataAccessor) {
      if(DATA_RADIUS.equals(entityDataAccessor)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(entityDataAccessor);
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.IGNORE;
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }

   public EntityDimensions getDimensions(Pose pose) {
      return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
   }
}
