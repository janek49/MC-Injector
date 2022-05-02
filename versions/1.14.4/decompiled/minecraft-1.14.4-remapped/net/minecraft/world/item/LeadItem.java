package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

public class LeadItem extends Item {
   public LeadItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level var2 = useOnContext.getLevel();
      BlockPos var3 = useOnContext.getClickedPos();
      Block var4 = var2.getBlockState(var3).getBlock();
      if(var4.is(BlockTags.FENCES)) {
         Player var5 = useOnContext.getPlayer();
         if(!var2.isClientSide && var5 != null) {
            bindPlayerMobs(var5, var2, var3);
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   public static boolean bindPlayerMobs(Player player, Level level, BlockPos blockPos) {
      LeashFenceKnotEntity var3 = null;
      boolean var4 = false;
      double var5 = 7.0D;
      int var7 = blockPos.getX();
      int var8 = blockPos.getY();
      int var9 = blockPos.getZ();

      for(Mob var12 : level.getEntitiesOfClass(Mob.class, new AABB((double)var7 - 7.0D, (double)var8 - 7.0D, (double)var9 - 7.0D, (double)var7 + 7.0D, (double)var8 + 7.0D, (double)var9 + 7.0D))) {
         if(var12.getLeashHolder() == player) {
            if(var3 == null) {
               var3 = LeashFenceKnotEntity.getOrCreateKnot(level, blockPos);
            }

            var12.setLeashedTo(var3, true);
            var4 = true;
         }
      }

      return var4;
   }
}
