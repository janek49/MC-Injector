package net.minecraft.client.gui.screens.controls;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.resources.language.I18n;
import org.apache.commons.lang3.ArrayUtils;

@ClientJarOnly
public class ControlList extends ContainerObjectSelectionList {
   private final ControlsScreen controlsScreen;
   private int maxNameWidth;

   public ControlList(ControlsScreen controlsScreen, Minecraft minecraft) {
      super(minecraft, controlsScreen.width + 45, controlsScreen.height, 43, controlsScreen.height - 32, 20);
      this.controlsScreen = controlsScreen;
      KeyMapping[] vars3 = (KeyMapping[])ArrayUtils.clone(minecraft.options.keyMappings);
      Arrays.sort(vars3);
      String var4 = null;

      for(KeyMapping var8 : vars3) {
         String var9 = var8.getCategory();
         if(!var9.equals(var4)) {
            var4 = var9;
            this.addEntry(new ControlList.CategoryEntry(var9));
         }

         int var10 = minecraft.font.width(I18n.get(var8.getName(), new Object[0]));
         if(var10 > this.maxNameWidth) {
            this.maxNameWidth = var10;
         }

         this.addEntry(new ControlList.KeyEntry(var8));
      }

   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 15;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 32;
   }

   @ClientJarOnly
   public class CategoryEntry extends ControlList.Entry {
      private final String name;
      private final int width;

      public CategoryEntry(String string) {
         this.name = I18n.get(string, new Object[0]);
         this.width = ControlList.this.minecraft.font.width(this.name);
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         Font var10000 = ControlList.this.minecraft.font;
         String var10001 = this.name;
         float var10002 = (float)(ControlList.this.minecraft.screen.width / 2 - this.width / 2);
         int var10003 = var2 + var5;
         ControlList.this.minecraft.font.getClass();
         var10000.draw(var10001, var10002, (float)(var10003 - 9 - 1), 16777215);
      }

      public boolean changeFocus(boolean b) {
         return false;
      }

      public List children() {
         return Collections.emptyList();
      }
   }

   @ClientJarOnly
   public abstract static class Entry extends ContainerObjectSelectionList.Entry {
   }

   @ClientJarOnly
   public class KeyEntry extends ControlList.Entry {
      private final KeyMapping key;
      private final String name;
      private final Button changeButton;
      private final Button resetButton;

      private KeyEntry(final KeyMapping key) {
         this.key = key;
         this.name = I18n.get(key.getName(), new Object[0]);
         this.changeButton = new Button(0, 0, 75, 20, this.name, (button) -> {
            ControlList.this.controlsScreen.selectedKey = key;
         }) {
            protected String getNarrationMessage() {
               return key.isUnbound()?I18n.get("narrator.controls.unbound", new Object[]{KeyEntry.this.name}):I18n.get("narrator.controls.bound", new Object[]{KeyEntry.this.name, super.getNarrationMessage()});
            }
         };
         this.resetButton = new Button(0, 0, 50, 20, I18n.get("controls.reset", new Object[0]), (button) -> {
            ControlList.this.minecraft.options.setKey(key, key.getDefaultKey());
            KeyMapping.resetMapping();
         }) {
            protected String getNarrationMessage() {
               return I18n.get("narrator.controls.reset", new Object[]{KeyEntry.this.name});
            }
         };
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         boolean var10 = ControlList.this.controlsScreen.selectedKey == this.key;
         Font var10000 = ControlList.this.minecraft.font;
         String var10001 = this.name;
         float var10002 = (float)(var3 + 90 - ControlList.this.maxNameWidth);
         int var10003 = var2 + var5 / 2;
         ControlList.this.minecraft.font.getClass();
         var10000.draw(var10001, var10002, (float)(var10003 - 9 / 2), 16777215);
         this.resetButton.x = var3 + 190;
         this.resetButton.y = var2;
         this.resetButton.active = !this.key.isDefault();
         this.resetButton.render(var6, var7, var9);
         this.changeButton.x = var3 + 105;
         this.changeButton.y = var2;
         this.changeButton.setMessage(this.key.getTranslatedKeyMessage());
         boolean var11 = false;
         if(!this.key.isUnbound()) {
            for(KeyMapping var15 : ControlList.this.minecraft.options.keyMappings) {
               if(var15 != this.key && this.key.same(var15)) {
                  var11 = true;
                  break;
               }
            }
         }

         if(var10) {
            this.changeButton.setMessage(ChatFormatting.WHITE + "> " + ChatFormatting.YELLOW + this.changeButton.getMessage() + ChatFormatting.WHITE + " <");
         } else if(var11) {
            this.changeButton.setMessage(ChatFormatting.RED + this.changeButton.getMessage());
         }

         this.changeButton.render(var6, var7, var9);
      }

      public List children() {
         return ImmutableList.of(this.changeButton, this.resetButton);
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         return this.changeButton.mouseClicked(var1, var3, var5)?true:this.resetButton.mouseClicked(var1, var3, var5);
      }

      public boolean mouseReleased(double var1, double var3, int var5) {
         return this.changeButton.mouseReleased(var1, var3, var5) || this.resetButton.mouseReleased(var1, var3, var5);
      }
   }
}
