package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.dimension.end.TheEndDimension;
import net.minecraft.world.phys.AABB;

public class EndCrystalItem extends Item {
   public EndCrystalItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level var2 = useOnContext.getLevel();
      BlockPos var3 = useOnContext.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if(var4.getBlock() != Blocks.OBSIDIAN && var4.getBlock() != Blocks.BEDROCK) {
         return InteractionResult.FAIL;
      } else {
         BlockPos var5 = var3.above();
         if(!var2.isEmptyBlock(var5)) {
            return InteractionResult.FAIL;
         } else {
            double var6 = (double)var5.getX();
            double var8 = (double)var5.getY();
            double var10 = (double)var5.getZ();
            List<Entity> var12 = var2.getEntities((Entity)null, new AABB(var6, var8, var10, var6 + 1.0D, var8 + 2.0D, var10 + 1.0D));
            if(!var12.isEmpty()) {
               return InteractionResult.FAIL;
            } else {
               if(!var2.isClientSide) {
                  EndCrystal var13 = new EndCrystal(var2, var6 + 0.5D, var8, var10 + 0.5D);
                  var13.setShowBottom(false);
                  var2.addFreshEntity(var13);
                  if(var2.dimension instanceof TheEndDimension) {
                     EndDragonFight var14 = ((TheEndDimension)var2.dimension).getDragonFight();
                     var14.tryRespawn();
                  }
               }

               useOnContext.getItemInHand().shrink(1);
               return InteractionResult.SUCCESS;
            }
         }
      }
   }

   public boolean isFoil(ItemStack itemStack) {
      return true;
   }
}
