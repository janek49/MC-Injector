package net.minecraft.client.gui.screens.resourcepacks;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.resourcepacks.lists.AvailableResourcePackList;
import net.minecraft.client.gui.screens.resourcepacks.lists.ResourcePackList;
import net.minecraft.client.gui.screens.resourcepacks.lists.SelectedResourcePackList;
import net.minecraft.client.resources.UnopenedResourcePack;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.repository.PackRepository;

@ClientJarOnly
public class ResourcePackSelectScreen extends Screen {
   private final Screen parentScreen;
   private AvailableResourcePackList availableResourcePackList;
   private SelectedResourcePackList selectedResourcePackList;
   private boolean changed;

   public ResourcePackSelectScreen(Screen parentScreen) {
      super(new TranslatableComponent("resourcePack.title", new Object[0]));
      this.parentScreen = parentScreen;
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 154, this.height - 48, 150, 20, I18n.get("resourcePack.openFolder", new Object[0]), (button) -> {
         Util.getPlatform().openFile(this.minecraft.getResourcePackDirectory());
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height - 48, 150, 20, I18n.get("gui.done", new Object[0]), (button) -> {
         if(this.changed) {
            List<UnopenedResourcePack> var2 = Lists.newArrayList();

            for(ResourcePackList.ResourcePackEntry var4 : this.selectedResourcePackList.children()) {
               var2.add(var4.getResourcePack());
            }

            Collections.reverse(var2);
            this.minecraft.getResourcePackRepository().setSelected(var2);
            this.minecraft.options.resourcePacks.clear();
            this.minecraft.options.incompatibleResourcePacks.clear();

            for(UnopenedResourcePack var4 : var2) {
               if(!var4.isFixedPosition()) {
                  this.minecraft.options.resourcePacks.add(var4.getId());
                  if(!var4.getCompatibility().isCompatible()) {
                     this.minecraft.options.incompatibleResourcePacks.add(var4.getId());
                  }
               }
            }

            this.minecraft.options.save();
            this.minecraft.setScreen(this.parentScreen);
            this.minecraft.reloadResourcePacks();
         } else {
            this.minecraft.setScreen(this.parentScreen);
         }

      }));
      AvailableResourcePackList var1 = this.availableResourcePackList;
      SelectedResourcePackList var2 = this.selectedResourcePackList;
      this.availableResourcePackList = new AvailableResourcePackList(this.minecraft, 200, this.height);
      this.availableResourcePackList.setLeftPos(this.width / 2 - 4 - 200);
      if(var1 != null) {
         this.availableResourcePackList.children().addAll(var1.children());
      }

      this.children.add(this.availableResourcePackList);
      this.selectedResourcePackList = new SelectedResourcePackList(this.minecraft, 200, this.height);
      this.selectedResourcePackList.setLeftPos(this.width / 2 + 4);
      if(var2 != null) {
         this.selectedResourcePackList.children().addAll(var2.children());
      }

      this.children.add(this.selectedResourcePackList);
      if(!this.changed) {
         this.availableResourcePackList.children().clear();
         this.selectedResourcePackList.children().clear();
         PackRepository<UnopenedResourcePack> var3 = this.minecraft.getResourcePackRepository();
         var3.reload();
         List<UnopenedResourcePack> var4 = Lists.newArrayList(var3.getAvailable());
         var4.removeAll(var3.getSelected());

         for(UnopenedResourcePack var6 : var4) {
            this.availableResourcePackList.addResourcePackEntry(new ResourcePackList.ResourcePackEntry(this.availableResourcePackList, this, var6));
         }

         for(UnopenedResourcePack var6 : Lists.reverse(Lists.newArrayList(var3.getSelected()))) {
            this.selectedResourcePackList.addResourcePackEntry(new ResourcePackList.ResourcePackEntry(this.selectedResourcePackList, this, var6));
         }
      }

   }

   public void select(ResourcePackList.ResourcePackEntry resourcePackList$ResourcePackEntry) {
      this.availableResourcePackList.children().remove(resourcePackList$ResourcePackEntry);
      resourcePackList$ResourcePackEntry.addToList(this.selectedResourcePackList);
      this.setChanged();
   }

   public void deselect(ResourcePackList.ResourcePackEntry resourcePackList$ResourcePackEntry) {
      this.selectedResourcePackList.children().remove(resourcePackList$ResourcePackEntry);
      this.availableResourcePackList.addResourcePackEntry(resourcePackList$ResourcePackEntry);
      this.setChanged();
   }

   public boolean isSelected(ResourcePackList.ResourcePackEntry resourcePackList$ResourcePackEntry) {
      return this.selectedResourcePackList.children().contains(resourcePackList$ResourcePackEntry);
   }

   public void render(int var1, int var2, float var3) {
      this.renderDirtBackground(0);
      this.availableResourcePackList.render(var1, var2, var3);
      this.selectedResourcePackList.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.font, I18n.get("resourcePack.folderInfo", new Object[0]), this.width / 2 - 77, this.height - 26, 8421504);
      super.render(var1, var2, var3);
   }

   public void setChanged() {
      this.changed = true;
   }
}
