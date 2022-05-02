package net.minecraft.world.effect;

import com.google.common.collect.ComparisonChain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MobEffectInstance implements Comparable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MobEffect effect;
   private int duration;
   private int amplifier;
   private boolean splash;
   private boolean ambient;
   private boolean noCounter;
   private boolean visible;
   private boolean showIcon;

   public MobEffectInstance(MobEffect mobEffect) {
      this(mobEffect, 0, 0);
   }

   public MobEffectInstance(MobEffect mobEffect, int var2) {
      this(mobEffect, var2, 0);
   }

   public MobEffectInstance(MobEffect mobEffect, int var2, int var3) {
      this(mobEffect, var2, var3, false, true);
   }

   public MobEffectInstance(MobEffect mobEffect, int var2, int var3, boolean var4, boolean var5) {
      this(mobEffect, var2, var3, var4, var5, var5);
   }

   public MobEffectInstance(MobEffect effect, int duration, int amplifier, boolean ambient, boolean visible, boolean showIcon) {
      this.effect = effect;
      this.duration = duration;
      this.amplifier = amplifier;
      this.ambient = ambient;
      this.visible = visible;
      this.showIcon = showIcon;
   }

   public MobEffectInstance(MobEffectInstance mobEffectInstance) {
      this.effect = mobEffectInstance.effect;
      this.duration = mobEffectInstance.duration;
      this.amplifier = mobEffectInstance.amplifier;
      this.ambient = mobEffectInstance.ambient;
      this.visible = mobEffectInstance.visible;
      this.showIcon = mobEffectInstance.showIcon;
   }

   public boolean update(MobEffectInstance mobEffectInstance) {
      if(this.effect != mobEffectInstance.effect) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      boolean var2 = false;
      if(mobEffectInstance.amplifier > this.amplifier) {
         this.amplifier = mobEffectInstance.amplifier;
         this.duration = mobEffectInstance.duration;
         var2 = true;
      } else if(mobEffectInstance.amplifier == this.amplifier && this.duration < mobEffectInstance.duration) {
         this.duration = mobEffectInstance.duration;
         var2 = true;
      }

      if(!mobEffectInstance.ambient && this.ambient || var2) {
         this.ambient = mobEffectInstance.ambient;
         var2 = true;
      }

      if(mobEffectInstance.visible != this.visible) {
         this.visible = mobEffectInstance.visible;
         var2 = true;
      }

      if(mobEffectInstance.showIcon != this.showIcon) {
         this.showIcon = mobEffectInstance.showIcon;
         var2 = true;
      }

      return var2;
   }

   public MobEffect getEffect() {
      return this.effect;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public boolean isAmbient() {
      return this.ambient;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public boolean showIcon() {
      return this.showIcon;
   }

   public boolean tick(LivingEntity livingEntity) {
      if(this.duration > 0) {
         if(this.effect.isDurationEffectTick(this.duration, this.amplifier)) {
            this.applyEffect(livingEntity);
         }

         this.tickDownDuration();
      }

      return this.duration > 0;
   }

   private int tickDownDuration() {
      return --this.duration;
   }

   public void applyEffect(LivingEntity livingEntity) {
      if(this.duration > 0) {
         this.effect.applyEffectTick(livingEntity, this.amplifier);
      }

   }

   public String getDescriptionId() {
      return this.effect.getDescriptionId();
   }

   public String toString() {
      String string;
      if(this.amplifier > 0) {
         string = this.getDescriptionId() + " x " + (this.amplifier + 1) + ", Duration: " + this.duration;
      } else {
         string = this.getDescriptionId() + ", Duration: " + this.duration;
      }

      if(this.splash) {
         string = string + ", Splash: true";
      }

      if(!this.visible) {
         string = string + ", Particles: false";
      }

      if(!this.showIcon) {
         string = string + ", Show Icon: false";
      }

      return string;
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof MobEffectInstance)) {
         return false;
      } else {
         MobEffectInstance var2 = (MobEffectInstance)object;
         return this.duration == var2.duration && this.amplifier == var2.amplifier && this.splash == var2.splash && this.ambient == var2.ambient && this.effect.equals(var2.effect);
      }
   }

   public int hashCode() {
      int var1 = this.effect.hashCode();
      var1 = 31 * var1 + this.duration;
      var1 = 31 * var1 + this.amplifier;
      var1 = 31 * var1 + (this.splash?1:0);
      var1 = 31 * var1 + (this.ambient?1:0);
      return var1;
   }

   public CompoundTag save(CompoundTag compoundTag) {
      compoundTag.putByte("Id", (byte)MobEffect.getId(this.getEffect()));
      compoundTag.putByte("Amplifier", (byte)this.getAmplifier());
      compoundTag.putInt("Duration", this.getDuration());
      compoundTag.putBoolean("Ambient", this.isAmbient());
      compoundTag.putBoolean("ShowParticles", this.isVisible());
      compoundTag.putBoolean("ShowIcon", this.showIcon());
      return compoundTag;
   }

   public static MobEffectInstance load(CompoundTag compoundTag) {
      int var1 = compoundTag.getByte("Id");
      MobEffect var2 = MobEffect.byId(var1);
      if(var2 == null) {
         return null;
      } else {
         int var3 = compoundTag.getByte("Amplifier");
         int var4 = compoundTag.getInt("Duration");
         boolean var5 = compoundTag.getBoolean("Ambient");
         boolean var6 = true;
         if(compoundTag.contains("ShowParticles", 1)) {
            var6 = compoundTag.getBoolean("ShowParticles");
         }

         boolean var7 = var6;
         if(compoundTag.contains("ShowIcon", 1)) {
            var7 = compoundTag.getBoolean("ShowIcon");
         }

         return new MobEffectInstance(var2, var4, var3 < 0?0:var3, var5, var6, var7);
      }
   }

   public void setNoCounter(boolean noCounter) {
      this.noCounter = noCounter;
   }

   public boolean isNoCounter() {
      return this.noCounter;
   }

   public int compareTo(MobEffectInstance mobEffectInstance) {
      int var2 = 32147;
      return (this.getDuration() <= 32147 || mobEffectInstance.getDuration() <= 32147) && (!this.isAmbient() || !mobEffectInstance.isAmbient())?ComparisonChain.start().compare(Boolean.valueOf(this.isAmbient()), Boolean.valueOf(mobEffectInstance.isAmbient())).compare(this.getDuration(), mobEffectInstance.getDuration()).compare(this.getEffect().getColor(), mobEffectInstance.getEffect().getColor()).result():ComparisonChain.start().compare(Boolean.valueOf(this.isAmbient()), Boolean.valueOf(mobEffectInstance.isAmbient())).compare(this.getEffect().getColor(), mobEffectInstance.getEffect().getColor()).result();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((MobEffectInstance)var1);
   }
}
