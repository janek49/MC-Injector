package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;

@ClientJarOnly
public abstract class AbstractButton extends AbstractWidget {
   public AbstractButton(int var1, int var2, int var3, int var4, String string) {
      super(var1, var2, var3, var4, string);
   }

   public abstract void onPress();

   public void onClick(double var1, double var3) {
      this.onPress();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(this.active && this.visible) {
         if(var1 != 257 && var1 != 32 && var1 != 335) {
            return false;
         } else {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress();
            return true;
         }
      } else {
         return false;
      }
   }
}
