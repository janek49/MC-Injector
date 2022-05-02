package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.client.Option;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;

@ClientJarOnly
public class MouseSettingsScreen extends Screen {
   private final Screen lastScreen;
   private OptionsList list;
   private static final Option[] OPTIONS = new Option[]{Option.SENSITIVITY, Option.INVERT_MOUSE, Option.MOUSE_WHEEL_SENSITIVITY, Option.DISCRETE_MOUSE_SCROLL, Option.TOUCHSCREEN};

   public MouseSettingsScreen(Screen lastScreen) {
      super(new TranslatableComponent("options.mouse_settings.title", new Object[0]));
      this.lastScreen = lastScreen;
   }

   protected void init() {
      this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      if(InputConstants.isRawMouseInputSupported()) {
         this.list.addSmall((Option[])Stream.concat(Arrays.stream(OPTIONS), Stream.of(Option.RAW_MOUSE_INPUT)).toArray((i) -> {
            return new Option[i];
         }));
      } else {
         this.list.addSmall(OPTIONS);
      }

      this.children.add(this.list);
      this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, I18n.get("gui.done", new Object[0]), (button) -> {
         this.minecraft.options.save();
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.list.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 5, 16777215);
      super.render(var1, var2, var3);
   }
}
