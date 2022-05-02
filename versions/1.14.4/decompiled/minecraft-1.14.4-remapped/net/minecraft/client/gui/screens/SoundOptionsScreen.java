package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.components.VolumeSlider;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundSource;

@ClientJarOnly
public class SoundOptionsScreen extends Screen {
   private final Screen lastScreen;
   private final Options options;

   public SoundOptionsScreen(Screen lastScreen, Options options) {
      super(new TranslatableComponent("options.sounds.title", new Object[0]));
      this.lastScreen = lastScreen;
      this.options = options;
   }

   protected void init() {
      int var1 = 0;
      this.addButton(new VolumeSlider(this.minecraft, this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), SoundSource.MASTER, 310));
      var1 = var1 + 2;

      for(SoundSource var5 : SoundSource.values()) {
         if(var5 != SoundSource.MASTER) {
            this.addButton(new VolumeSlider(this.minecraft, this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), var5, 150));
            ++var1;
         }
      }

      int var10003 = this.width / 2 - 75;
      int var10004 = this.height / 6 - 12;
      ++var1;
      this.addButton(new OptionButton(var10003, var10004 + 24 * (var1 >> 1), 150, 20, Option.SHOW_SUBTITLES, Option.SHOW_SUBTITLES.getMessage(this.options), (button) -> {
         Option.SHOW_SUBTITLES.toggle(this.minecraft.options);
         button.setMessage(Option.SHOW_SUBTITLES.getMessage(this.minecraft.options));
         this.minecraft.options.save();
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.get("gui.done", new Object[0]), (button) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 15, 16777215);
      super.render(var1, var2, var3);
   }
}
