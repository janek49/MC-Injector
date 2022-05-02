package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.sounds.SoundSource;

@ClientJarOnly
public class VolumeSlider extends AbstractSliderButton {
   private final SoundSource source;

   public VolumeSlider(Minecraft minecraft, int var2, int var3, SoundSource source, int var5) {
      super(minecraft.options, var2, var3, var5, 20, (double)minecraft.options.getSoundSourceVolume(source));
      this.source = source;
      this.updateMessage();
   }

   protected void updateMessage() {
      String var1 = (float)this.value == (float)this.getYImage(false)?I18n.get("options.off", new Object[0]):(int)((float)this.value * 100.0F) + "%";
      this.setMessage(I18n.get("soundCategory." + this.source.getName(), new Object[0]) + ": " + var1);
   }

   protected void applyValue() {
      this.options.setSoundCategoryVolume(this.source, (float)this.value);
      this.options.save();
   }
}
