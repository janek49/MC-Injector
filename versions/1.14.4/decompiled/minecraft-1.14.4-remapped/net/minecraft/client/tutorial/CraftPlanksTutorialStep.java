package net.minecraft.client.tutorial;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.tutorial.TutorialStepInstance;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

@ClientJarOnly
public class CraftPlanksTutorialStep implements TutorialStepInstance {
   private static final Component CRAFT_TITLE = new TranslatableComponent("tutorial.craft_planks.title", new Object[0]);
   private static final Component CRAFT_DESCRIPTION = new TranslatableComponent("tutorial.craft_planks.description", new Object[0]);
   private final Tutorial tutorial;
   private TutorialToast toast;
   private int timeWaiting;

   public CraftPlanksTutorialStep(Tutorial tutorial) {
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
               if(var1.inventory.contains(ItemTags.PLANKS)) {
                  this.tutorial.setStep(TutorialSteps.NONE);
                  return;
               }

               if(hasCraftedPlanksPreviously(var1, ItemTags.PLANKS)) {
                  this.tutorial.setStep(TutorialSteps.NONE);
                  return;
               }
            }
         }

         if(this.timeWaiting >= 1200 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Icons.WOODEN_PLANKS, CRAFT_TITLE, CRAFT_DESCRIPTION, false);
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

   public void onGetItem(ItemStack itemStack) {
      Item var2 = itemStack.getItem();
      if(ItemTags.PLANKS.contains(var2)) {
         this.tutorial.setStep(TutorialSteps.NONE);
      }

   }

   public static boolean hasCraftedPlanksPreviously(LocalPlayer localPlayer, Tag tag) {
      for(Item var3 : tag.getValues()) {
         if(localPlayer.getStats().getValue(Stats.ITEM_CRAFTED.get(var3)) > 0) {
            return true;
         }
      }

      return false;
   }
}
