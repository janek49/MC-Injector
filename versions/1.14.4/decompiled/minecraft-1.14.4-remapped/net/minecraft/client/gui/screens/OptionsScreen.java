package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.Option;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.OptionButton;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.ChatOptionsScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SkinCustomizationScreen;
import net.minecraft.client.gui.screens.SoundOptionsScreen;
import net.minecraft.client.gui.screens.VideoSettingsScreen;
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.resourcepacks.ResourcePackSelectScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.world.Difficulty;

@ClientJarOnly
public class OptionsScreen extends Screen {
   private static final Option[] OPTION_SCREEN_OPTIONS = new Option[]{Option.FOV};
   private final Screen lastScreen;
   private final Options options;
   private Button difficultyButton;
   private LockIconButton lockButton;
   private Difficulty currentDifficulty;

   public OptionsScreen(Screen lastScreen, Options options) {
      super(new TranslatableComponent("options.title", new Object[0]));
      this.lastScreen = lastScreen;
      this.options = options;
   }

   protected void init() {
      int var1 = 0;

      for(Option var5 : OPTION_SCREEN_OPTIONS) {
         int var6 = this.width / 2 - 155 + var1 % 2 * 160;
         int var7 = this.height / 6 - 12 + 24 * (var1 >> 1);
         this.addButton(var5.createButton(this.minecraft.options, var6, var7, 150));
         ++var1;
      }

      if(this.minecraft.level != null) {
         this.currentDifficulty = this.minecraft.level.getDifficulty();
         this.difficultyButton = (Button)this.addButton(new Button(this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), 150, 20, this.getDifficultyText(this.currentDifficulty), (button) -> {
            this.currentDifficulty = Difficulty.byId(this.currentDifficulty.getId() + 1);
            this.minecraft.getConnection().send((Packet)(new ServerboundChangeDifficultyPacket(this.currentDifficulty)));
            this.difficultyButton.setMessage(this.getDifficultyText(this.currentDifficulty));
         }));
         if(this.minecraft.hasSingleplayerServer() && !this.minecraft.level.getLevelData().isHardcore()) {
            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
            this.lockButton = (LockIconButton)this.addButton(new LockIconButton(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, (button) -> {
               this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, new TranslatableComponent("difficulty.lock.title", new Object[0]), new TranslatableComponent("difficulty.lock.question", new Object[]{new TranslatableComponent("options.difficulty." + this.minecraft.level.getLevelData().getDifficulty().getKey(), new Object[0])})));
            }));
            this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
            this.lockButton.active = !this.lockButton.isLocked();
            this.difficultyButton.active = !this.lockButton.isLocked();
         } else {
            this.difficultyButton.active = false;
         }
      } else {
         this.addButton(new OptionButton(this.width / 2 - 155 + var1 % 2 * 160, this.height / 6 - 12 + 24 * (var1 >> 1), 150, 20, Option.REALMS_NOTIFICATIONS, Option.REALMS_NOTIFICATIONS.getMessage(this.options), (button) -> {
            Option.REALMS_NOTIFICATIONS.toggle(this.options);
            this.options.save();
            button.setMessage(Option.REALMS_NOTIFICATIONS.getMessage(this.options));
         }));
      }

      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.get("options.skinCustomisation", new Object[0]), (button) -> {
         this.minecraft.setScreen(new SkinCustomizationScreen(this));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.get("options.sounds", new Object[0]), (button) -> {
         this.minecraft.setScreen(new SoundOptionsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.get("options.video", new Object[0]), (button) -> {
         this.minecraft.setScreen(new VideoSettingsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.get("options.controls", new Object[0]), (button) -> {
         this.minecraft.setScreen(new ControlsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.get("options.language", new Object[0]), (button) -> {
         this.minecraft.setScreen(new LanguageSelectScreen(this, this.options, this.minecraft.getLanguageManager()));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.get("options.chat.title", new Object[0]), (button) -> {
         this.minecraft.setScreen(new ChatOptionsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.get("options.resourcepack", new Object[0]), (button) -> {
         this.minecraft.setScreen(new ResourcePackSelectScreen(this));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.get("options.accessibility.title", new Object[0]), (button) -> {
         this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.get("gui.done", new Object[0]), (button) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public String getDifficultyText(Difficulty difficulty) {
      return (new TranslatableComponent("options.difficulty", new Object[0])).append(": ").append(difficulty.getDisplayName()).getColoredString();
   }

   private void lockCallback(boolean b) {
      this.minecraft.setScreen(this);
      if(b && this.minecraft.level != null) {
         this.minecraft.getConnection().send((Packet)(new ServerboundLockDifficultyPacket(true)));
         this.lockButton.setLocked(true);
         this.lockButton.active = false;
         this.difficultyButton.active = false;
      }

   }

   public void removed() {
      this.options.save();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 15, 16777215);
      super.render(var1, var2, var3);
   }
}
