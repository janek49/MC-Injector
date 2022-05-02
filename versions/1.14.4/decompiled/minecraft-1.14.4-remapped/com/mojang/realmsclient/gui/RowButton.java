package com.mojang.realmsclient.gui;

import com.fox2code.repacker.ClientJarOnly;
import java.util.List;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.RealmsObjectSelectionList;

@ClientJarOnly
public abstract class RowButton {
   public final int width;
   public final int height;
   public final int xOffset;
   public final int yOffset;

   public RowButton(int width, int height, int xOffset, int yOffset) {
      this.width = width;
      this.height = height;
      this.xOffset = xOffset;
      this.yOffset = yOffset;
   }

   public void drawForRowAt(int var1, int var2, int var3, int var4) {
      int var5 = var1 + this.xOffset;
      int var6 = var2 + this.yOffset;
      boolean var7 = false;
      if(var3 >= var5 && var3 <= var5 + this.width && var4 >= var6 && var4 <= var6 + this.height) {
         var7 = true;
      }

      this.draw(var5, var6, var7);
   }

   protected abstract void draw(int var1, int var2, boolean var3);

   public int getRight() {
      return this.xOffset + this.width;
   }

   public int getBottom() {
      return this.yOffset + this.height;
   }

   public abstract void onClick(int var1);

   public static void drawButtonsInRow(List list, RealmsObjectSelectionList realmsObjectSelectionList, int var2, int var3, int var4, int var5) {
      for(RowButton var7 : list) {
         if(realmsObjectSelectionList.getRowWidth() > var7.getRight()) {
            var7.drawForRowAt(var2, var3, var4, var5);
         }
      }

   }

   public static void rowButtonMouseClicked(RealmsObjectSelectionList realmsObjectSelectionList, RealmListEntry realmListEntry, List list, int var3, double var4, double var6) {
      if(var3 == 0) {
         int var8 = realmsObjectSelectionList.children().indexOf(realmListEntry);
         if(var8 > -1) {
            realmsObjectSelectionList.selectItem(var8);
            int var9 = realmsObjectSelectionList.getRowLeft();
            int var10 = realmsObjectSelectionList.getRowTop(var8);
            int var11 = (int)(var4 - (double)var9);
            int var12 = (int)(var6 - (double)var10);

            for(RowButton var14 : list) {
               if(var11 >= var14.xOffset && var11 <= var14.getRight() && var12 >= var14.yOffset && var12 <= var14.getBottom()) {
                  var14.onClick(var8);
               }
            }
         }
      }

   }
}
