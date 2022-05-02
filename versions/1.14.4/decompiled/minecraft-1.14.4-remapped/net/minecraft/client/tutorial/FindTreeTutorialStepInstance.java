package net.minecraft.client.tutorial;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@ClientJarOnly
public class FindTreeTutorialStepInstance implements TutorialStepInstance {
   private static final Set TREE_BLOCKS = Sets.newHashSet(new Block[]{Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES});
   private static final Component TITLE = new TranslatableComponent("tutorial.find_tree.title", new Object[0]);
   private static final Component DESCRIPTION = new TranslatableComponent("tutorial.find_tree.description", new Object[0]);
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;

   public FindTreeTutorialStepInstance(Tutorial tutorial) {
      this.tutorial = tutorial;
   }

   public void tick() {
      ++this.timeWaiting;
      if(this.tutorial.getGameMode() != GameType.SURVIVAL) {
         this.tutorial.setStep(TutorialSteps.NONE);
      } else {
         if(this.timeWaiting == 1) {
            LocalPlayer var1 = this.tutorial.getMinecraft().player;
            if(var1 != null) {
               for(Block var3 : TREE_BLOCKS) {
                  if(var1.inventory.contains(new ItemStack(var3))) {
                     this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                     return;
                  }
               }

               if(hasPunchedTreesPreviously(var1)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }
            }
         }

         if(this.timeWaiting >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, false);
            this.tutorial.getMinecraft().getToasts().addToast(this.toast);
         }

      }
   }

   public void clear() {
      if(this.toast != null) {
         this.toast.hide();
         this.toast = null;
      }

   }

   public void onLookAt(MultiPlayerLevel multiPlayerLevel, HitResult hitResult) {
      if(hitResult.getType() == HitResult.Type.BLOCK) {
         BlockState var3 = multiPlayerLevel.getBlockState(((BlockHitResult)hitResult).getBlockPos());
         if(TREE_BLOCKS.contains(var3.getBlock())) {
            this.tutorial.setStep(TutorialSteps.PUNCH_TREE);
         }
      }

   }

   public void onGetItem(ItemStack itemStack) {
      for(Block var3 : TREE_BLOCKS) {
         if(itemStack.getItem() == var3.asItem()) {
            this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
            return;
         }
      }

   }

   public static boolean hasPunchedTreesPreviously(LocalPlayer localPlayer) {
      for(Block var2 : TREE_BLOCKS) {
         if(localPlayer.getStats().getValue(Stats.BLOCK_MINED.get(var2)) > 0) {
            return true;
         }
      }

      return false;
   }
}
