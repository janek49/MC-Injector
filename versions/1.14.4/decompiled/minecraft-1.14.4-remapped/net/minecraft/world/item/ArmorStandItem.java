package net.minecraft.world.item;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ArmorStandItem extends Item {
   public ArmorStandItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Direction var2 = useOnContext.getClickedFace();
      if(var2 == Direction.DOWN) {
         return InteractionResult.FAIL;
      } else {
         Level var3 = useOnContext.getLevel();
         BlockPlaceContext var4 = new BlockPlaceContext(useOnContext);
         BlockPos var5 = var4.getClickedPos();
         BlockPos var6 = var5.above();
         if(var4.canPlace() && var3.getBlockState(var6).canBeReplaced(var4)) {
            double var7 = (double)var5.getX();
            double var9 = (double)var5.getY();
            double var11 = (double)var5.getZ();
            List<Entity> var13 = var3.getEntities((Entity)null, new AABB(var7, var9, var11, var7 + 1.0D, var9 + 2.0D, var11 + 1.0D));
            if(!var13.isEmpty()) {
               return InteractionResult.FAIL;
            } else {
               ItemStack var14 = useOnContext.getItemInHand();
               if(!var3.isClientSide) {
                  var3.removeBlock(var5, false);
                  var3.removeBlock(var6, false);
                  ArmorStand var15 = new ArmorStand(var3, var7 + 0.5D, var9, var11 + 0.5D);
                  float var16 = (float)Mth.floor((Mth.wrapDegrees(useOnContext.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                  var15.moveTo(var7 + 0.5D, var9, var11 + 0.5D, var16, 0.0F);
                  this.randomizePose(var15, var3.random);
                  EntityType.updateCustomEntityTag(var3, useOnContext.getPlayer(), var15, var14.getTag());
                  var3.addFreshEntity(var15);
                  var3.playSound((Player)null, var15.x, var15.y, var15.z, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
               }

               var14.shrink(1);
               return InteractionResult.SUCCESS;
            }
         } else {
            return InteractionResult.FAIL;
         }
      }
   }

   private void randomizePose(ArmorStand armorStand, Random random) {
      Rotations var3 = armorStand.getHeadPose();
      float var5 = random.nextFloat() * 5.0F;
      float var6 = random.nextFloat() * 20.0F - 10.0F;
      Rotations var4 = new Rotations(var3.getX() + var5, var3.getY() + var6, var3.getZ());
      armorStand.setHeadPose(var4);
      var3 = armorStand.getBodyPose();
      var5 = random.nextFloat() * 10.0F - 5.0F;
      var4 = new Rotations(var3.getX(), var3.getY() + var5, var3.getZ());
      armorStand.setBodyPose(var4);
   }
}
