package net.minecraft.client.gui.screens.worldselection;

import com.fox2code.repacker.ClientJarOnly;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.OptimizeWorldScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.FileUtils;

@ClientJarOnly
public class EditWorldScreen extends Screen {
   private Button renameButton;
   private final BooleanConsumer callback;
   private EditBox nameEdit;
   private final String levelId;

   public EditWorldScreen(BooleanConsumer callback, String levelId) {
      super(new TranslatableComponent("selectWorld.edit.title", new Object[0]));
      this.callback = callback;
      this.levelId = levelId;
   }

   public void tick() {
      this.nameEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      Button var1 = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 24 + 5, 200, 20, I18n.get("selectWorld.edit.resetIcon", new Object[0]), (button) -> {
         LevelStorageSource var2 = this.minecraft.getLevelSource();
         FileUtils.deleteQuietly(var2.getFile(this.levelId, "icon.png"));
         button.active = false;
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 48 + 5, 200, 20, I18n.get("selectWorld.edit.openFolder", new Object[0]), (button) -> {
         LevelStorageSource var2 = this.minecraft.getLevelSource();
         Util.getPlatform().openFile(var2.getFile(this.levelId, "icon.png").getParentFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72 + 5, 200, 20, I18n.get("selectWorld.edit.backup", new Object[0]), (button) -> {
         LevelStorageSource var2 = this.minecraft.getLevelSource();
         makeBackupAndShowToast(var2, this.levelId);
         this.callback.accept(false);
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96 + 5, 200, 20, I18n.get("selectWorld.edit.backupFolder", new Object[0]), (button) -> {
         LevelStorageSource var2 = this.minecraft.getLevelSource();
         Path var3 = var2.getBackupPath();

         try {
            Files.createDirectories(Files.exists(var3, new LinkOption[0])?var3.toRealPath(new LinkOption[0]):var3, new FileAttribute[0]);
         } catch (IOException var5) {
            throw new RuntimeException(var5);
         }

         Util.getPlatform().openFile(var3.toFile());
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 5, 200, 20, I18n.get("selectWorld.edit.optimize", new Object[0]), (button) -> {
         this.minecraft.setScreen(new BackupConfirmScreen(this, (var1, var2) -> {
            if(var1) {
               makeBackupAndShowToast(this.minecraft.getLevelSource(), this.levelId);
            }

            this.minecraft.setScreen(new OptimizeWorldScreen(this.callback, this.levelId, this.minecraft.getLevelSource(), var2));
         }, new TranslatableComponent("optimizeWorld.confirm.title", new Object[0]), new TranslatableComponent("optimizeWorld.confirm.description", new Object[0]), true));
      }));
      this.renameButton = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 144 + 5, 98, 20, I18n.get("selectWorld.edit.save", new Object[0]), (button) -> {
         this.onRename();
      }));
      this.addButton(new Button(this.width / 2 + 2, this.height / 4 + 144 + 5, 98, 20, I18n.get("gui.cancel", new Object[0]), (button) -> {
         this.callback.accept(false);
      }));
      var1.active = this.minecraft.getLevelSource().getFile(this.levelId, "icon.png").isFile();
      LevelStorageSource var2 = this.minecraft.getLevelSource();
      LevelData var3 = var2.getDataTagFor(this.levelId);
      String var4 = var3 == null?"":var3.getLevelName();
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 53, 200, 20, I18n.get("selectWorld.enterName", new Object[0]));
      this.nameEdit.setValue(var4);
      this.nameEdit.setResponder((string) -> {
         this.renameButton.active = !string.trim().isEmpty();
      });
      this.children.add(this.nameEdit);
      this.setInitialFocus(this.nameEdit);
   }

   public void resize(Minecraft minecraft, int var2, int var3) {
      String var4 = this.nameEdit.getValue();
      this.init(minecraft, var2, var3);
      this.nameEdit.setValue(var4);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onRename() {
      LevelStorageSource var1 = this.minecraft.getLevelSource();
      var1.renameLevel(this.levelId, this.nameEdit.getValue().trim());
      this.callback.accept(true);
   }

   public static void makeBackupAndShowToast(LevelStorageSource levelStorageSource, String string) {
      ToastComponent var2 = Minecraft.getInstance().getToasts();
      long var3 = 0L;
      IOException var5 = null;

      try {
         var3 = levelStorageSource.makeWorldBackup(string);
      } catch (IOException var8) {
         var5 = var8;
      }

      Component var6;
      Component var7;
      if(var5 != null) {
         var6 = new TranslatableComponent("selectWorld.edit.backupFailed", new Object[0]);
         var7 = new TextComponent(var5.getMessage());
      } else {
         var6 = new TranslatableComponent("selectWorld.edit.backupCreated", new Object[]{string});
         var7 = new TranslatableComponent("selectWorld.edit.backupSize", new Object[]{Integer.valueOf(Mth.ceil((double)var3 / 1048576.0D))});
      }

      var2.addToast(new SystemToast(SystemToast.SystemToastIds.WORLD_BACKUP, var6, var7));
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, 16777215);
      this.drawString(this.font, I18n.get("selectWorld.enterName", new Object[0]), this.width / 2 - 100, 40, 10526880);
      this.nameEdit.render(var1, var2, var3);
      super.render(var1, var2, var3);
   }
}
