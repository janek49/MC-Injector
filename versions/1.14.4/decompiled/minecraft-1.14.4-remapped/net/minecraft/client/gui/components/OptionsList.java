package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

@ClientJarOnly
public class OptionsList extends ContainerObjectSelectionList {
   public OptionsList(Minecraft minecraft, int var2, int var3, int var4, int var5, int var6) {
      super(minecraft, var2, var3, var4, var5, var6);
      this.centerListVertically = false;
   }

   public int addBig(Option option) {
      return this.addEntry(OptionsList.Entry.big(this.minecraft.options, this.width, option));
   }

   public void addSmall(Option var1, @Nullable Option var2) {
      this.addEntry(OptionsList.Entry.small(this.minecraft.options, this.width, var1, var2));
   }

   public void addSmall(Option[] options) {
      for(int var2 = 0; var2 < options.length; var2 += 2) {
         this.addSmall(options[var2], var2 < options.length - 1?options[var2 + 1]:null);
      }

   }

   public int getRowWidth() {
      return 400;
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 32;
   }

   @ClientJarOnly
   public static class Entry extends ContainerObjectSelectionList.Entry {
      private final List children;

      private Entry(List children) {
         this.children = children;
      }

      public static OptionsList.Entry big(Options options, int var1, Option option) {
         return new OptionsList.Entry(ImmutableList.of(option.createButton(options, var1 / 2 - 155, 0, 310)));
      }

      public static OptionsList.Entry small(Options options, int var1, Option var2, @Nullable Option var3) {
         AbstractWidget var4 = var2.createButton(options, var1 / 2 - 155, 0, 150);
         return var3 == null?new OptionsList.Entry(ImmutableList.of(var4)):new OptionsList.Entry(ImmutableList.of(var4, var3.createButton(options, var1 / 2 - 155 + 160, 0, 150)));
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         this.children.forEach((abstractWidget) -> {
            abstractWidget.y = var2;
            abstractWidget.render(var6, var7, var9);
         });
      }

      public List children() {
         return this.children;
      }
   }
}
