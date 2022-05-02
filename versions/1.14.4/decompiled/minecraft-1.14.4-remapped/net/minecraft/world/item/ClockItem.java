package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ClockItem extends Item {
   public ClockItem(Item.Properties item$Properties) {
      super(item$Properties);
      this.addProperty(new ResourceLocation("time"), new ItemPropertyFunction() {
         private double rotation;
         private double rota;
         private long lastUpdateTick;

         public float call(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity livingEntity) {
            boolean var4 = livingEntity != null;
            Entity var5 = (Entity)(var4?livingEntity:itemStack.getFrame());
            if(level == null && var5 != null) {
               level = var5.level;
            }

            if(level == null) {
               return 0.0F;
            } else {
               double var6;
               if(level.dimension.isNaturalDimension()) {
                  var6 = (double)level.getTimeOfDay(1.0F);
               } else {
                  var6 = Math.random();
               }

               var6 = this.wobble(level, var6);
               return (float)var6;
            }
         }

         private double wobble(Level level, double var2) {
            if(level.getGameTime() != this.lastUpdateTick) {
               this.lastUpdateTick = level.getGameTime();
               double var4 = var2 - this.rotation;
               var4 = Mth.positiveModulo(var4 + 0.5D, 1.0D) - 0.5D;
               this.rota += var4 * 0.1D;
               this.rota *= 0.9D;
               this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0D);
            }

            return this.rotation;
         }
      });
   }
}
