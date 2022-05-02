package net.minecraft.world.level;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.storage.LevelData;

public final class LevelSettings {
   private final long seed;
   private final GameType gameType;
   private final boolean generateMapFeatures;
   private final boolean hardcore;
   private final LevelType levelType;
   private boolean allowCommands;
   private boolean startingBonusItems;
   private JsonElement levelTypeOptions;

   public LevelSettings(long seed, GameType gameType, boolean generateMapFeatures, boolean hardcore, LevelType levelType) {
      this.levelTypeOptions = new JsonObject();
      this.seed = seed;
      this.gameType = gameType;
      this.generateMapFeatures = generateMapFeatures;
      this.hardcore = hardcore;
      this.levelType = levelType;
   }

   public LevelSettings(LevelData levelData) {
      this(levelData.getSeed(), levelData.getGameType(), levelData.isGenerateMapFeatures(), levelData.isHardcore(), levelData.getGeneratorType());
   }

   public LevelSettings enableStartingBonusItems() {
      this.startingBonusItems = true;
      return this;
   }

   public LevelSettings enableSinglePlayerCommands() {
      this.allowCommands = true;
      return this;
   }

   public LevelSettings setLevelTypeOptions(JsonElement levelTypeOptions) {
      this.levelTypeOptions = levelTypeOptions;
      return this;
   }

   public boolean hasStartingBonusItems() {
      return this.startingBonusItems;
   }

   public long getSeed() {
      return this.seed;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public boolean isGenerateMapFeatures() {
      return this.generateMapFeatures;
   }

   public LevelType getLevelType() {
      return this.levelType;
   }

   public boolean getAllowCommands() {
      return this.allowCommands;
   }

   public JsonElement getLevelTypeOptions() {
      return this.levelTypeOptions;
   }
}
