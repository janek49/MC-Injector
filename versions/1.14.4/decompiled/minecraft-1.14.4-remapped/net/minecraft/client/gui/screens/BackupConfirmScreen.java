package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

@ClientJarOnly
public class BackupConfirmScreen extends Screen {
   private final Screen lastScreen;
   protected final BackupConfirmScreen.Listener listener;
   private final Component description;
   private final boolean promptForCacheErase;
   private final List lines = Lists.newArrayList();
   private final String eraseCacheText;
   private final String backupButton;
   private final String continueButton;
   private final String cancelButton;
   private Checkbox eraseCache;

   public BackupConfirmScreen(Screen lastScreen, BackupConfirmScreen.Listener listener, Component var3, Component description, boolean promptForCacheErase) {
      super(var3);
      this.lastScreen = lastScreen;
      this.listener = listener;
      this.description = description;
      this.promptForCacheErase = promptForCacheErase;
      this.eraseCacheText = I18n.get("selectWorld.backupEraseCache", new Object[0]);
      this.backupButton = I18n.get("selectWorld.backupJoinConfirmButton", new Object[0]);
      this.continueButton = I18n.get("selectWorld.backupJoinSkipButton", new Object[0]);
      this.cancelButton = I18n.get("gui.cancel", new Object[0]);
   }

   protected void init() {
      super.init();
      this.lines.clear();
      this.lines.addAll(this.font.split(this.description.getColoredString(), this.width - 50));
      int var10000 = this.lines.size() + 1;
      this.font.getClass();
      int var1 = var10000 * 9;
      this.addButton(new Button(this.width / 2 - 155, 100 + var1, 150, 20, this.backupButton, (button) -> {
         this.listener.proceed(true, this.eraseCache.selected());
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, 100 + var1, 150, 20, this.continueButton, (button) -> {
         this.listener.proceed(false, this.eraseCache.selected());
      }));
      this.addButton(new Button(this.width / 2 - 155 + 80, 124 + var1, 150, 20, this.cancelButton, (button) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.eraseCache = new Checkbox(this.width / 2 - 155 + 80, 76 + var1, 150, 20, this.eraseCacheText, false);
      if(this.promptForCacheErase) {
         this.addButton(this.eraseCache);
      }

   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 50, 16777215);
      int var4 = 70;

      for(String var6 : this.lines) {
         this.drawCenteredString(this.font, var6, this.width / 2, var4, 16777215);
         this.font.getClass();
         var4 += 9;
      }

      super.render(var1, var2, var3);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @ClientJarOnly
   public interface Listener {
      void proceed(boolean var1, boolean var2);
   }
}
