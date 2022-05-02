package net.minecraft.client.gui.screens.worldselection;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonElement;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.FileUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.CreateBuffetWorldScreen;
import net.minecraft.client.gui.screens.CreateFlatWorldScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.storage.LevelData;
import org.apache.commons.lang3.StringUtils;

@ClientJarOnly
public class CreateWorldScreen extends Screen {
   private final Screen lastScreen;
   private EditBox nameEdit;
   private EditBox seedEdit;
   private String resultFolder;
   private String gameModeName = "survival";
   private String oldGameModeName;
   private boolean features = true;
   private boolean commands;
   private boolean commandsChanged;
   private boolean bonusItems;
   private boolean hardCore;
   private boolean done;
   private boolean displayOptions;
   private Button createButton;
   private Button modeButton;
   private Button moreOptionsButton;
   private Button featuresButton;
   private Button bonusItemsButton;
   private Button typeButton;
   private Button commandsButton;
   private Button customizeTypeButton;
   private String gameModeHelp1;
   private String gameModeHelp2;
   private String initSeed;
   private String initName;
   private int levelTypeIndex;
   public CompoundTag levelTypeOptions = new CompoundTag();

   public CreateWorldScreen(Screen lastScreen) {
      super(new TranslatableComponent("selectWorld.create", new Object[0]));
      this.lastScreen = lastScreen;
      this.initSeed = "";
      this.initName = I18n.get("selectWorld.newWorld", new Object[0]);
   }

   public void tick() {
      this.nameEdit.tick();
      this.seedEdit.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, I18n.get("selectWorld.enterName", new Object[0]));
      this.nameEdit.setValue(this.initName);
      this.nameEdit.setResponder((initName) -> {
         this.initName = initName;
         this.createButton.active = !this.nameEdit.getValue().isEmpty();
         this.updateResultFolder();
      });
      this.children.add(this.nameEdit);
      this.modeButton = (Button)this.addButton(new Button(this.width / 2 - 75, 115, 150, 20, I18n.get("selectWorld.gameMode", new Object[0]), (button) -> {
         if("survival".equals(this.gameModeName)) {
            if(!this.commandsChanged) {
               this.commands = false;
            }

            this.hardCore = false;
            this.gameModeName = "hardcore";
            this.hardCore = true;
            this.commandsButton.active = false;
            this.bonusItemsButton.active = false;
            this.updateSelectionStrings();
         } else if("hardcore".equals(this.gameModeName)) {
            if(!this.commandsChanged) {
               this.commands = true;
            }

            this.hardCore = false;
            this.gameModeName = "creative";
            this.updateSelectionStrings();
            this.hardCore = false;
            this.commandsButton.active = true;
            this.bonusItemsButton.active = true;
         } else {
            if(!this.commandsChanged) {
               this.commands = false;
            }

            this.gameModeName = "survival";
            this.updateSelectionStrings();
            this.commandsButton.active = true;
            this.bonusItemsButton.active = true;
            this.hardCore = false;
         }

         this.updateSelectionStrings();
      }));
      this.seedEdit = new EditBox(this.font, this.width / 2 - 100, 60, 200, 20, I18n.get("selectWorld.enterSeed", new Object[0]));
      this.seedEdit.setValue(this.initSeed);
      this.seedEdit.setResponder((string) -> {
         this.initSeed = this.seedEdit.getValue();
      });
      this.children.add(this.seedEdit);
      this.featuresButton = (Button)this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, I18n.get("selectWorld.mapFeatures", new Object[0]), (button) -> {
         this.features = !this.features;
         this.updateSelectionStrings();
      }));
      this.featuresButton.visible = false;
      this.typeButton = (Button)this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, I18n.get("selectWorld.mapType", new Object[0]), (button) -> {
         ++this.levelTypeIndex;
         if(this.levelTypeIndex >= LevelType.LEVEL_TYPES.length) {
            this.levelTypeIndex = 0;
         }

         while(!this.isValidLevelType()) {
            ++this.levelTypeIndex;
            if(this.levelTypeIndex >= LevelType.LEVEL_TYPES.length) {
               this.levelTypeIndex = 0;
            }
         }

         this.levelTypeOptions = new CompoundTag();
         this.updateSelectionStrings();
         this.setDisplayOptions(this.displayOptions);
      }));
      this.typeButton.visible = false;
      this.customizeTypeButton = (Button)this.addButton(new Button(this.width / 2 + 5, 120, 150, 20, I18n.get("selectWorld.customizeType", new Object[0]), (button) -> {
         if(LevelType.LEVEL_TYPES[this.levelTypeIndex] == LevelType.FLAT) {
            this.minecraft.setScreen(new CreateFlatWorldScreen(this, this.levelTypeOptions));
         }

         if(LevelType.LEVEL_TYPES[this.levelTypeIndex] == LevelType.BUFFET) {
            this.minecraft.setScreen(new CreateBuffetWorldScreen(this, this.levelTypeOptions));
         }

      }));
      this.customizeTypeButton.visible = false;
      this.commandsButton = (Button)this.addButton(new Button(this.width / 2 - 155, 151, 150, 20, I18n.get("selectWorld.allowCommands", new Object[0]), (button) -> {
         this.commandsChanged = true;
         this.commands = !this.commands;
         this.updateSelectionStrings();
      }));
      this.commandsButton.visible = false;
      this.bonusItemsButton = (Button)this.addButton(new Button(this.width / 2 + 5, 151, 150, 20, I18n.get("selectWorld.bonusItems", new Object[0]), (button) -> {
         this.bonusItems = !this.bonusItems;
         this.updateSelectionStrings();
      }));
      this.bonusItemsButton.visible = false;
      this.moreOptionsButton = (Button)this.addButton(new Button(this.width / 2 - 75, 187, 150, 20, I18n.get("selectWorld.moreWorldOptions", new Object[0]), (button) -> {
         this.toggleDisplayOptions();
      }));
      this.createButton = (Button)this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("selectWorld.create", new Object[0]), (button) -> {
         this.onCreate();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel", new Object[0]), (button) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.setDisplayOptions(this.displayOptions);
      this.setInitialFocus(this.nameEdit);
      this.updateResultFolder();
      this.updateSelectionStrings();
   }

   private void updateResultFolder() {
      this.resultFolder = this.nameEdit.getValue().trim();
      if(this.resultFolder.length() == 0) {
         this.resultFolder = "World";
      }

      try {
         this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
      } catch (Exception var4) {
         this.resultFolder = "World";

         try {
            this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
         } catch (Exception var3) {
            throw new RuntimeException("Could not create save folder", var3);
         }
      }

   }

   private void updateSelectionStrings() {
      this.modeButton.setMessage(I18n.get("selectWorld.gameMode", new Object[0]) + ": " + I18n.get("selectWorld.gameMode." + this.gameModeName, new Object[0]));
      this.gameModeHelp1 = I18n.get("selectWorld.gameMode." + this.gameModeName + ".line1", new Object[0]);
      this.gameModeHelp2 = I18n.get("selectWorld.gameMode." + this.gameModeName + ".line2", new Object[0]);
      this.featuresButton.setMessage(I18n.get("selectWorld.mapFeatures", new Object[0]) + ' ' + I18n.get(this.features?"options.on":"options.off", new Object[0]));
      this.bonusItemsButton.setMessage(I18n.get("selectWorld.bonusItems", new Object[0]) + ' ' + I18n.get(this.bonusItems && !this.hardCore?"options.on":"options.off", new Object[0]));
      this.typeButton.setMessage(I18n.get("selectWorld.mapType", new Object[0]) + ' ' + I18n.get(LevelType.LEVEL_TYPES[this.levelTypeIndex].getDescriptionId(), new Object[0]));
      this.commandsButton.setMessage(I18n.get("selectWorld.allowCommands", new Object[0]) + ' ' + I18n.get(this.commands && !this.hardCore?"options.on":"options.off", new Object[0]));
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onCreate() {
      this.minecraft.setScreen((Screen)null);
      if(!this.done) {
         this.done = true;
         long var1 = (new Random()).nextLong();
         String var3 = this.seedEdit.getValue();
         if(!StringUtils.isEmpty(var3)) {
            try {
               long var4 = Long.parseLong(var3);
               if(var4 != 0L) {
                  var1 = var4;
               }
            } catch (NumberFormatException var6) {
               var1 = (long)var3.hashCode();
            }
         }

         LevelSettings var4 = new LevelSettings(var1, GameType.byName(this.gameModeName), this.features, this.hardCore, LevelType.LEVEL_TYPES[this.levelTypeIndex]);
         var4.setLevelTypeOptions((JsonElement)Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, this.levelTypeOptions));
         if(this.bonusItems && !this.hardCore) {
            var4.enableStartingBonusItems();
         }

         if(this.commands && !this.hardCore) {
            var4.enableSinglePlayerCommands();
         }

         this.minecraft.selectLevel(this.resultFolder, this.nameEdit.getValue().trim(), var4);
      }
   }

   private boolean isValidLevelType() {
      LevelType var1 = LevelType.LEVEL_TYPES[this.levelTypeIndex];
      return var1 != null && var1.isSelectable()?(var1 == LevelType.DEBUG_ALL_BLOCK_STATES?hasShiftDown():true):false;
   }

   private void toggleDisplayOptions() {
      this.setDisplayOptions(!this.displayOptions);
   }

   private void setDisplayOptions(boolean displayOptions) {
      this.displayOptions = displayOptions;
      if(LevelType.LEVEL_TYPES[this.levelTypeIndex] == LevelType.DEBUG_ALL_BLOCK_STATES) {
         this.modeButton.visible = !this.displayOptions;
         this.modeButton.active = false;
         if(this.oldGameModeName == null) {
            this.oldGameModeName = this.gameModeName;
         }

         this.gameModeName = "spectator";
         this.featuresButton.visible = false;
         this.bonusItemsButton.visible = false;
         this.typeButton.visible = this.displayOptions;
         this.commandsButton.visible = false;
         this.customizeTypeButton.visible = false;
      } else {
         this.modeButton.visible = !this.displayOptions;
         this.modeButton.active = true;
         if(this.oldGameModeName != null) {
            this.gameModeName = this.oldGameModeName;
            this.oldGameModeName = null;
         }

         this.featuresButton.visible = this.displayOptions && LevelType.LEVEL_TYPES[this.levelTypeIndex] != LevelType.CUSTOMIZED;
         this.bonusItemsButton.visible = this.displayOptions;
         this.typeButton.visible = this.displayOptions;
         this.commandsButton.visible = this.displayOptions;
         this.customizeTypeButton.visible = this.displayOptions && LevelType.LEVEL_TYPES[this.levelTypeIndex].hasCustomOptions();
      }

      this.updateSelectionStrings();
      this.seedEdit.setVisible(this.displayOptions);
      this.nameEdit.setVisible(!this.displayOptions);
      if(this.displayOptions) {
         this.moreOptionsButton.setMessage(I18n.get("gui.done", new Object[0]));
      } else {
         this.moreOptionsButton.setMessage(I18n.get("selectWorld.moreWorldOptions", new Object[0]));
      }

   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(super.keyPressed(var1, var2, var3)) {
         return true;
      } else if(var1 != 257 && var1 != 335) {
         return false;
      } else {
         this.onCreate();
         return true;
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 20, -1);
      if(this.displayOptions) {
         this.drawString(this.font, I18n.get("selectWorld.enterSeed", new Object[0]), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.font, I18n.get("selectWorld.seedInfo", new Object[0]), this.width / 2 - 100, 85, -6250336);
         if(this.featuresButton.visible) {
            this.drawString(this.font, I18n.get("selectWorld.mapFeatures.info", new Object[0]), this.width / 2 - 150, 122, -6250336);
         }

         if(this.commandsButton.visible) {
            this.drawString(this.font, I18n.get("selectWorld.allowCommands.info", new Object[0]), this.width / 2 - 150, 172, -6250336);
         }

         this.seedEdit.render(var1, var2, var3);
         if(LevelType.LEVEL_TYPES[this.levelTypeIndex].hasHelpText()) {
            this.font.drawWordWrap(I18n.get(LevelType.LEVEL_TYPES[this.levelTypeIndex].getHelpTextId(), new Object[0]), this.typeButton.x + 2, this.typeButton.y + 22, this.typeButton.getWidth(), 10526880);
         }
      } else {
         this.drawString(this.font, I18n.get("selectWorld.enterName", new Object[0]), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.font, I18n.get("selectWorld.resultFolder", new Object[0]) + " " + this.resultFolder, this.width / 2 - 100, 85, -6250336);
         this.nameEdit.render(var1, var2, var3);
         this.drawCenteredString(this.font, this.gameModeHelp1, this.width / 2, 137, -6250336);
         this.drawCenteredString(this.font, this.gameModeHelp2, this.width / 2, 149, -6250336);
      }

      super.render(var1, var2, var3);
   }

   public void copyFromWorld(LevelData levelData) {
      this.initName = levelData.getLevelName();
      this.initSeed = levelData.getSeed() + "";
      LevelType var2 = levelData.getGeneratorType() == LevelType.CUSTOMIZED?LevelType.NORMAL:levelData.getGeneratorType();
      this.levelTypeIndex = var2.getId();
      this.levelTypeOptions = levelData.getGeneratorOptions();
      this.features = levelData.isGenerateMapFeatures();
      this.commands = levelData.getAllowCommands();
      if(levelData.isHardcore()) {
         this.gameModeName = "hardcore";
      } else if(levelData.getGameType().isSurvival()) {
         this.gameModeName = "survival";
      } else if(levelData.getGameType().isCreative()) {
         this.gameModeName = "creative";
      }

   }
}
