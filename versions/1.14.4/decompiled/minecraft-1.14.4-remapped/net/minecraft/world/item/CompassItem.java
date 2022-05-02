package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemPropertyFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class CompassItem extends Item {
   public CompassItem(Item.Properties item$Properties) {
      super(item$Properties);
      this.addProperty(new ResourceLocation("angle"), new ItemPropertyFunction() {
         private double rotation;
         private double rota;
         private long lastUpdateTick;

         public float call(ItemStack itemStack, @Nullable Level level, @Nullable LivingEntity livingEntity) {
            if(livingEntity == null && !itemStack.isFramed()) {
               return 0.0F;
            } else {
               boolean var4 = livingEntity != null;
               Entity var5 = (Entity)(var4?livingEntity:itemStack.getFrame());
               if(level == null) {
                  level = var5.level;
               }

               double var6;
               if(level.dimension.isNaturalDimension()) {
                  double var8 = var4?(double)var5.yRot:this.getFrameRotation((ItemFrame)var5);
                  var8 = Mth.positiveModulo(var8 / 360.0D, 1.0D);
                  double var10 = this.getSpawnToAngle(level, var5) / 6.2831854820251465D;
                  var6 = 0.5D - (var8 - 0.25D - var10);
               } else {
                  var6 = Math.random();
               }

               if(var4) {
                  var6 = this.wobble(level, var6);
               }

               return Mth.positiveModulo((float)var6, 1.0F);
            }
         }

         private double wobble(Level level, double var2) {
            if(level.getGameTime() != this.lastUpdateTick) {
               this.lastUpdateTick = level.getGameTime();
               double var4 = var2 - this.rotation;
               var4 = Mth.positiveModulo(var4 + 0.5D, 1.0D) - 0.5D;
               this.rota += var4 * 0.1D;
               this.rota *= 0.8D;
               this.rotation = Mth.positiveModulo(this.rotation + this.rota, 1.0D);
            }

            return this.rotation;
         }

         private double getFrameRotation(ItemFrame itemFrame) {
            return (double)Mth.wrapDegrees(180 + itemFrame.getDirection().get2DDataValue() * 90);
         }

         private double getSpawnToAngle(LevelAccessor levelAccessor, Entity entity) {
            BlockPos var3 = levelAccessor.getSharedSpawnPos();
            return Math.atan2((double)var3.getZ() - entity.z, (double)var3.getX() - entity.x);
         }
      });
   }
}
