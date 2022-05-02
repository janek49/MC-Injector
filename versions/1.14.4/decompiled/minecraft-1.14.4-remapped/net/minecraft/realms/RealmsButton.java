package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.RealmsButtonProxy;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public abstract class RealmsButton extends AbstractRealmsButton {
   protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   private final int id;
   private final RealmsButtonProxy proxy;

   public RealmsButton(int var1, int var2, int var3, String string) {
      this(var1, var2, var3, 200, 20, string);
   }

   public RealmsButton(int id, int var2, int var3, int var4, int var5, String string) {
      this.id = id;
      this.proxy = new RealmsButtonProxy(this, var2, var3, string, var4, var5, (button) -> {
         this.onPress();
      });
   }

   public RealmsButtonProxy getProxy() {
      return this.proxy;
   }

   public int id() {
      return this.id;
   }

   public void setMessage(String message) {
      this.proxy.setMessage(message);
   }

   public int getWidth() {
      return this.proxy.getWidth();
   }

   public int getHeight() {
      return this.proxy.getHeight();
   }

   public int y() {
      return this.proxy.y();
   }

   public int x() {
      return this.proxy.x;
   }

   public void renderBg(int var1, int var2) {
   }

   public int getYImage(boolean b) {
      return this.proxy.getSuperYImage(b);
   }

   public abstract void onPress();

   public void onRelease(double var1, double var3) {
   }

   public void renderButton(int var1, int var2, float var3) {
      this.getProxy().superRenderButton(var1, var2, var3);
   }

   public void drawCenteredString(String string, int var2, int var3, int var4) {
      this.getProxy().drawCenteredString(Minecraft.getInstance().font, string, var2, var3, var4);
   }
}
