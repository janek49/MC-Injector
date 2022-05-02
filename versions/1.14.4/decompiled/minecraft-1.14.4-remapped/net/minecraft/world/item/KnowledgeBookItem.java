package net.minecraft.world.item;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KnowledgeBookItem extends Item {
   private static final Logger LOGGER = LogManager.getLogger();

   public KnowledgeBookItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      CompoundTag var5 = var4.getTag();
      if(!player.abilities.instabuild) {
         player.setItemInHand(interactionHand, ItemStack.EMPTY);
      }

      if(var5 != null && var5.contains("Recipes", 9)) {
         if(!level.isClientSide) {
            ListTag var6 = var5.getList("Recipes", 8);
            List<Recipe<?>> var7 = Lists.newArrayList();
            RecipeManager var8 = level.getServer().getRecipeManager();

            for(int var9 = 0; var9 < var6.size(); ++var9) {
               String var10 = var6.getString(var9);
               Optional<? extends Recipe<?>> var11 = var8.byKey(new ResourceLocation(var10));
               if(!var11.isPresent()) {
                  LOGGER.error("Invalid recipe: {}", var10);
                  return new InteractionResultHolder(InteractionResult.FAIL, var4);
               }

               var7.add(var11.get());
            }

            player.awardRecipes(var7);
            player.awardStat(Stats.ITEM_USED.get(this));
         }

         return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
      } else {
         LOGGER.error("Tag not valid: {}", var5);
         return new InteractionResultHolder(InteractionResult.FAIL, var4);
      }
   }
}
