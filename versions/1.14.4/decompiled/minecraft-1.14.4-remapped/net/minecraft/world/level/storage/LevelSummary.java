package net.minecraft.world.level.storage;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.storage.LevelData;

public class LevelSummary implements Comparable {
   private final String levelId;
   private final String levelName;
   private final long lastPlayed;
   private final long sizeOnDisk;
   private final boolean requiresConversion;
   private final GameType gameMode;
   private final boolean hardcore;
   private final boolean hasCheats;
   private final String worldVersionName;
   private final int worldVersion;
   private final boolean snapshot;
   private final LevelType generatorType;

   public LevelSummary(LevelData levelData, String levelId, String levelName, long sizeOnDisk, boolean requiresConversion) {
      this.levelId = levelId;
      this.levelName = levelName;
      this.lastPlayed = levelData.getLastPlayed();
      this.sizeOnDisk = sizeOnDisk;
      this.gameMode = levelData.getGameType();
      this.requiresConversion = requiresConversion;
      this.hardcore = levelData.isHardcore();
      this.hasCheats = levelData.getAllowCommands();
      this.worldVersionName = levelData.getMinecraftVersionName();
      this.worldVersion = levelData.getMinecraftVersion();
      this.snapshot = levelData.isSnapshot();
      this.generatorType = levelData.getGeneratorType();
   }

   public String getLevelId() {
      return this.levelId;
   }

   public String getLevelName() {
      return this.levelName;
   }

   public long getSizeOnDisk() {
      return this.sizeOnDisk;
   }

   public boolean isRequiresConversion() {
      return this.requiresConversion;
   }

   public long getLastPlayed() {
      return this.lastPlayed;
   }

   public int compareTo(LevelSummary levelSummary) {
      return this.lastPlayed < levelSummary.lastPlayed?1:(this.lastPlayed > levelSummary.lastPlayed?-1:this.levelId.compareTo(levelSummary.levelId));
   }

   public GameType getGameMode() {
      return this.gameMode;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public boolean hasCheats() {
      return this.hasCheats;
   }

   public Component getWorldVersionName() {
      return (Component)(StringUtil.isNullOrEmpty(this.worldVersionName)?new TranslatableComponent("selectWorld.versionUnknown", new Object[0]):new TextComponent(this.worldVersionName));
   }

   public boolean markVersionInList() {
      return this.askToOpenWorld() || !SharedConstants.getCurrentVersion().isStable() && !this.snapshot || this.shouldBackup() || this.isOldCustomizedWorld();
   }

   public boolean askToOpenWorld() {
      return this.worldVersion > SharedConstants.getCurrentVersion().getWorldVersion();
   }

   public boolean isOldCustomizedWorld() {
      return this.generatorType == LevelType.CUSTOMIZED && this.worldVersion < 1466;
   }

   public boolean shouldBackup() {
      return this.worldVersion < SharedConstants.getCurrentVersion().getWorldVersion();
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((LevelSummary)var1);
   }
}
