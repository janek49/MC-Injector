package net.minecraft.client.gui.screens.advancements;

import com.fox2code.repacker.ClientJarOnly;

@ClientJarOnly
public enum AdvancementWidgetType {
   OBTAINED(0),
   UNOBTAINED(1);

   private final int y;

   private AdvancementWidgetType(int y) {
      this.y = y;
   }

   public int getIndex() {
      return this.y;
   }
}
