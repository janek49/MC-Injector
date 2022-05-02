package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.realms.RealmsGuiEventListener;

@ClientJarOnly
public class RealmsEditBox extends RealmsGuiEventListener {
   private final EditBox editBox;

   public RealmsEditBox(int var1, int var2, int var3, int var4, int var5, String string) {
      this.editBox = new EditBox(Minecraft.getInstance().font, var2, var3, var4, var5, (EditBox)null, string);
   }

   public String getValue() {
      return this.editBox.getValue();
   }

   public void tick() {
      this.editBox.tick();
   }

   public void setValue(String value) {
      this.editBox.setValue(value);
   }

   public boolean charTyped(char var1, int var2) {
      return this.editBox.charTyped(var1, var2);
   }

   public GuiEventListener getProxy() {
      return this.editBox;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.editBox.keyPressed(var1, var2, var3);
   }

   public boolean isFocused() {
      return this.editBox.isFocused();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.editBox.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.editBox.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.editBox.mouseDragged(var1, var3, var5, var6, var8);
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.editBox.mouseScrolled(var1, var3, var5);
   }

   public void render(int var1, int var2, float var3) {
      this.editBox.render(var1, var2, var3);
   }

   public void setMaxLength(int maxLength) {
      this.editBox.setMaxLength(maxLength);
   }

   public void setIsEditable(boolean isEditable) {
      this.editBox.setEditable(isEditable);
   }
}
