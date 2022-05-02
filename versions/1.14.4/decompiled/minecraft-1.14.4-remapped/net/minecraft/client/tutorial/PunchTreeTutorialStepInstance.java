package net.minecraft.client.tutorial;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.tutorial.FindTreeTutorialStepInstance;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class PunchTreeTutorialStepInstance implements TutorialStepInstance {
   private static final Component TITLE = new TranslatableComponent("tutorial.punch_tree.title", new Object[0]);
   private static final Component DESCRIPTION = new TranslatableComponent("tutorial.punch_tree.description", new Object[]{Tutorial.key("attack")});
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;
   private int resetCount;

   public PunchTreeTutorialStepInstance(Tutorial tutorial) {
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
               if(var1.inventory.contains(ItemTags.LOGS)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }

               if(FindTreeTutorialStepInstance.hasPunchedTreesPreviously(var1)) {
                  this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
                  return;
               }
            }
         }

         if((this.timeWaiting >= 600 || this.resetCount > 3) && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.TREE, TITLE, DESCRIPTION, true);
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

   public void onDestroyBlock(MultiPlayerLevel multiPlayerLevel, BlockPos blockPos, BlockState blockState, float var4) {
      boolean var5 = blockState.is(BlockTags.LOGS);
      if(var5 && var4 > 0.0F) {
         if(this.toast != null) {
            this.toast.updateProgress(var4);
         }

         if(var4 >= 1.0F) {
            this.tutorial.setStep(TutorialSteps.OPEN_INVENTORY);
         }
      } else if(this.toast != null) {
         this.toast.updateProgress(0.0F);
      } else if(var5) {
         ++this.resetCount;
      }

   }

   public void onGetItem(ItemStack itemStack) {
      if(ItemTags.LOGS.contains(itemStack.getItem())) {
         this.tutorial.setStep(TutorialSteps.CRAFT_PLANKS);
      }
   }
}
