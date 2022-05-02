package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.timers.TimerCallbacks;
import net.minecraft.world.level.timers.TimerQueue;

public class LevelData {
   private String minecraftVersionName;
   private int minecraftVersion;
   private boolean snapshot;
   public static final Difficulty DEFAULT_DIFFICULTY = Difficulty.NORMAL;
   private long seed;
   private LevelType generator = LevelType.NORMAL;
   private CompoundTag generatorOptions = new CompoundTag();
   @Nullable
   private String legacyCustomOptions;
   private int xSpawn;
   private int ySpawn;
   private int zSpawn;
   private long gameTime;
   private long dayTime;
   private long lastPlayed;
   private long sizeOnDisk;
   @Nullable
   private final DataFixer fixerUpper;
   private final int playerDataVersion;
   private boolean upgradedPlayerTag;
   private CompoundTag loadedPlayerTag;
   private String levelName;
   private int version;
   private int clearWeatherTime;
   private boolean raining;
   private int rainTime;
   private boolean thundering;
   private int thunderTime;
   private GameType gameType;
   private boolean generateMapFeatures;
   private boolean hardcore;
   private boolean allowCommands;
   private boolean initialized;
   private Difficulty difficulty;
   private boolean difficultyLocked;
   private double borderX;
   private double borderZ;
   private double borderSize = 6.0E7D;
   private long borderSizeLerpTime;
   private double borderSizeLerpTarget;
   private double borderSafeZone = 5.0D;
   private double borderDamagePerBlock = 0.2D;
   private int borderWarningBlocks = 5;
   private int borderWarningTime = 15;
   private final Set disabledDataPacks = Sets.newHashSet();
   private final Set enabledDataPacks = Sets.newLinkedHashSet();
   private final Map dimensionData = Maps.newIdentityHashMap();
   private CompoundTag customBossEvents;
   private int wanderingTraderSpawnDelay;
   private int wanderingTraderSpawnChance;
   private UUID wanderingTraderId;
   private final GameRules gameRules = new GameRules();
   private final TimerQueue scheduledEvents = new TimerQueue(TimerCallbacks.SERVER_CALLBACKS);

   protected LevelData() {
      this.fixerUpper = null;
      this.playerDataVersion = SharedConstants.getCurrentVersion().getWorldVersion();
      this.setGeneratorOptions(new CompoundTag());
   }

   public LevelData(CompoundTag var1, DataFixer fixerUpper, int playerDataVersion, @Nullable CompoundTag loadedPlayerTag) {
      this.fixerUpper = fixerUpper;
      if(var1.contains("Version", 10)) {
         CompoundTag var5 = var1.getCompound("Version");
         this.minecraftVersionName = var5.getString("Name");
         this.minecraftVersion = var5.getInt("Id");
         this.snapshot = var5.getBoolean("Snapshot");
      }

      this.seed = var1.getLong("RandomSeed");
      if(var1.contains("generatorName", 8)) {
         String var5 = var1.getString("generatorName");
         this.generator = LevelType.getLevelType(var5);
         if(this.generator == null) {
            this.generator = LevelType.NORMAL;
         } else if(this.generator == LevelType.CUSTOMIZED) {
            this.legacyCustomOptions = var1.getString("generatorOptions");
         } else if(this.generator.hasReplacement()) {
            int var6 = 0;
            if(var1.contains("generatorVersion", 99)) {
               var6 = var1.getInt("generatorVersion");
            }

            this.generator = this.generator.getReplacementForVersion(var6);
         }

         this.setGeneratorOptions(var1.getCompound("generatorOptions"));
      }

      this.gameType = GameType.byId(var1.getInt("GameType"));
      if(var1.contains("legacy_custom_options", 8)) {
         this.legacyCustomOptions = var1.getString("legacy_custom_options");
      }

      if(var1.contains("MapFeatures", 99)) {
         this.generateMapFeatures = var1.getBoolean("MapFeatures");
      } else {
         this.generateMapFeatures = true;
      }

      this.xSpawn = var1.getInt("SpawnX");
      this.ySpawn = var1.getInt("SpawnY");
      this.zSpawn = var1.getInt("SpawnZ");
      this.gameTime = var1.getLong("Time");
      if(var1.contains("DayTime", 99)) {
         this.dayTime = var1.getLong("DayTime");
      } else {
         this.dayTime = this.gameTime;
      }

      this.lastPlayed = var1.getLong("LastPlayed");
      this.sizeOnDisk = var1.getLong("SizeOnDisk");
      this.levelName = var1.getString("LevelName");
      this.version = var1.getInt("version");
      this.clearWeatherTime = var1.getInt("clearWeatherTime");
      this.rainTime = var1.getInt("rainTime");
      this.raining = var1.getBoolean("raining");
      this.thunderTime = var1.getInt("thunderTime");
      this.thundering = var1.getBoolean("thundering");
      this.hardcore = var1.getBoolean("hardcore");
      if(var1.contains("initialized", 99)) {
         this.initialized = var1.getBoolean("initialized");
      } else {
         this.initialized = true;
      }

      if(var1.contains("allowCommands", 99)) {
         this.allowCommands = var1.getBoolean("allowCommands");
      } else {
         this.allowCommands = this.gameType == GameType.CREATIVE;
      }

      this.playerDataVersion = playerDataVersion;
      if(loadedPlayerTag != null) {
         this.loadedPlayerTag = loadedPlayerTag;
      }

      if(var1.contains("GameRules", 10)) {
         this.gameRules.loadFromTag(var1.getCompound("GameRules"));
      }

      if(var1.contains("Difficulty", 99)) {
         this.difficulty = Difficulty.byId(var1.getByte("Difficulty"));
      }

      if(var1.contains("DifficultyLocked", 1)) {
         this.difficultyLocked = var1.getBoolean("DifficultyLocked");
      }

      if(var1.contains("BorderCenterX", 99)) {
         this.borderX = var1.getDouble("BorderCenterX");
      }

      if(var1.contains("BorderCenterZ", 99)) {
         this.borderZ = var1.getDouble("BorderCenterZ");
      }

      if(var1.contains("BorderSize", 99)) {
         this.borderSize = var1.getDouble("BorderSize");
      }

      if(var1.contains("BorderSizeLerpTime", 99)) {
         this.borderSizeLerpTime = var1.getLong("BorderSizeLerpTime");
      }

      if(var1.contains("BorderSizeLerpTarget", 99)) {
         this.borderSizeLerpTarget = var1.getDouble("BorderSizeLerpTarget");
      }

      if(var1.contains("BorderSafeZone", 99)) {
         this.borderSafeZone = var1.getDouble("BorderSafeZone");
      }

      if(var1.contains("BorderDamagePerBlock", 99)) {
         this.borderDamagePerBlock = var1.getDouble("BorderDamagePerBlock");
      }

      if(var1.contains("BorderWarningBlocks", 99)) {
         this.borderWarningBlocks = var1.getInt("BorderWarningBlocks");
      }

      if(var1.contains("BorderWarningTime", 99)) {
         this.borderWarningTime = var1.getInt("BorderWarningTime");
      }

      if(var1.contains("DimensionData", 10)) {
         CompoundTag var5 = var1.getCompound("DimensionData");

         for(String var7 : var5.getAllKeys()) {
            this.dimensionData.put(DimensionType.getById(Integer.parseInt(var7)), var5.getCompound(var7));
         }
      }

      if(var1.contains("DataPacks", 10)) {
         CompoundTag var5 = var1.getCompound("DataPacks");
         ListTag var6 = var5.getList("Disabled", 8);

         for(int var7 = 0; var7 < var6.size(); ++var7) {
            this.disabledDataPacks.add(var6.getString(var7));
         }

         ListTag var7 = var5.getList("Enabled", 8);

         for(int var8 = 0; var8 < var7.size(); ++var8) {
            this.enabledDataPacks.add(var7.getString(var8));
         }
      }

      if(var1.contains("CustomBossEvents", 10)) {
         this.customBossEvents = var1.getCompound("CustomBossEvents");
      }

      if(var1.contains("ScheduledEvents", 9)) {
         this.scheduledEvents.load(var1.getList("ScheduledEvents", 10));
      }

      if(var1.contains("WanderingTraderSpawnDelay", 99)) {
         this.wanderingTraderSpawnDelay = var1.getInt("WanderingTraderSpawnDelay");
      }

      if(var1.contains("WanderingTraderSpawnChance", 99)) {
         this.wanderingTraderSpawnChance = var1.getInt("WanderingTraderSpawnChance");
      }

      if(var1.contains("WanderingTraderId", 8)) {
         this.wanderingTraderId = UUID.fromString(var1.getString("WanderingTraderId"));
      }

   }

   public LevelData(LevelSettings levelSettings, String levelName) {
      this.fixerUpper = null;
      this.playerDataVersion = SharedConstants.getCurrentVersion().getWorldVersion();
      this.setLevelSettings(levelSettings);
      this.levelName = levelName;
      this.difficulty = DEFAULT_DIFFICULTY;
      this.initialized = false;
   }

   public void setLevelSettings(LevelSettings levelSettings) {
      this.seed = levelSettings.getSeed();
      this.gameType = levelSettings.getGameType();
      this.generateMapFeatures = levelSettings.isGenerateMapFeatures();
      this.hardcore = levelSettings.isHardcore();
      this.generator = levelSettings.getLevelType();
      this.setGeneratorOptions((CompoundTag)Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, levelSettings.getLevelTypeOptions()));
      this.allowCommands = levelSettings.getAllowCommands();
   }

   public CompoundTag createTag(@Nullable CompoundTag compoundTag) {
      this.updatePlayerTag();
      if(compoundTag == null) {
         compoundTag = this.loadedPlayerTag;
      }

      CompoundTag var2 = new CompoundTag();
      this.setTagData(var2, compoundTag);
      return var2;
   }

   private void setTagData(CompoundTag var1, CompoundTag var2) {
      CompoundTag var3 = new CompoundTag();
      var3.putString("Name", SharedConstants.getCurrentVersion().getName());
      var3.putInt("Id", SharedConstants.getCurrentVersion().getWorldVersion());
      var3.putBoolean("Snapshot", !SharedConstants.getCurrentVersion().isStable());
      var1.put("Version", var3);
      var1.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      var1.putLong("RandomSeed", this.seed);
      var1.putString("generatorName", this.generator.getSerialization());
      var1.putInt("generatorVersion", this.generator.getVersion());
      if(!this.generatorOptions.isEmpty()) {
         var1.put("generatorOptions", this.generatorOptions);
      }

      if(this.legacyCustomOptions != null) {
         var1.putString("legacy_custom_options", this.legacyCustomOptions);
      }

      var1.putInt("GameType", this.gameType.getId());
      var1.putBoolean("MapFeatures", this.generateMapFeatures);
      var1.putInt("SpawnX", this.xSpawn);
      var1.putInt("SpawnY", this.ySpawn);
      var1.putInt("SpawnZ", this.zSpawn);
      var1.putLong("Time", this.gameTime);
      var1.putLong("DayTime", this.dayTime);
      var1.putLong("SizeOnDisk", this.sizeOnDisk);
      var1.putLong("LastPlayed", Util.getEpochMillis());
      var1.putString("LevelName", this.levelName);
      var1.putInt("version", this.version);
      var1.putInt("clearWeatherTime", this.clearWeatherTime);
      var1.putInt("rainTime", this.rainTime);
      var1.putBoolean("raining", this.raining);
      var1.putInt("thunderTime", this.thunderTime);
      var1.putBoolean("thundering", this.thundering);
      var1.putBoolean("hardcore", this.hardcore);
      var1.putBoolean("allowCommands", this.allowCommands);
      var1.putBoolean("initialized", this.initialized);
      var1.putDouble("BorderCenterX", this.borderX);
      var1.putDouble("BorderCenterZ", this.borderZ);
      var1.putDouble("BorderSize", this.borderSize);
      var1.putLong("BorderSizeLerpTime", this.borderSizeLerpTime);
      var1.putDouble("BorderSafeZone", this.borderSafeZone);
      var1.putDouble("BorderDamagePerBlock", this.borderDamagePerBlock);
      var1.putDouble("BorderSizeLerpTarget", this.borderSizeLerpTarget);
      var1.putDouble("BorderWarningBlocks", (double)this.borderWarningBlocks);
      var1.putDouble("BorderWarningTime", (double)this.borderWarningTime);
      if(this.difficulty != null) {
         var1.putByte("Difficulty", (byte)this.difficulty.getId());
      }

      var1.putBoolean("DifficultyLocked", this.difficultyLocked);
      var1.put("GameRules", this.gameRules.createTag());
      CompoundTag var4 = new CompoundTag();

      for(Entry<DimensionType, CompoundTag> var6 : this.dimensionData.entrySet()) {
         var4.put(String.valueOf(((DimensionType)var6.getKey()).getId()), (Tag)var6.getValue());
      }

      var1.put("DimensionData", var4);
      if(var2 != null) {
         var1.put("Player", var2);
      }

      CompoundTag var5 = new CompoundTag();
      ListTag var6 = new ListTag();

      for(String var8 : this.enabledDataPacks) {
         var6.add(new StringTag(var8));
      }

      var5.put("Enabled", var6);
      ListTag var7 = new ListTag();

      for(String var9 : this.disabledDataPacks) {
         var7.add(new StringTag(var9));
      }

      var5.put("Disabled", var7);
      var1.put("DataPacks", var5);
      if(this.customBossEvents != null) {
         var1.put("CustomBossEvents", this.customBossEvents);
      }

      var1.put("ScheduledEvents", this.scheduledEvents.store());
      var1.putInt("WanderingTraderSpawnDelay", this.wanderingTraderSpawnDelay);
      var1.putInt("WanderingTraderSpawnChance", this.wanderingTraderSpawnChance);
      if(this.wanderingTraderId != null) {
         var1.putString("WanderingTraderId", this.wanderingTraderId.toString());
      }

   }

   public long getSeed() {
      return this.seed;
   }

   public int getXSpawn() {
      return this.xSpawn;
   }

   public int getYSpawn() {
      return this.ySpawn;
   }

   public int getZSpawn() {
      return this.zSpawn;
   }

   public long getGameTime() {
      return this.gameTime;
   }

   public long getDayTime() {
      return this.dayTime;
   }

   private void updatePlayerTag() {
      if(!this.upgradedPlayerTag && this.loadedPlayerTag != null) {
         if(this.playerDataVersion < SharedConstants.getCurrentVersion().getWorldVersion()) {
            if(this.fixerUpper == null) {
               throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
            }

            this.loadedPlayerTag = NbtUtils.update(this.fixerUpper, DataFixTypes.PLAYER, this.loadedPlayerTag, this.playerDataVersion);
         }

         this.upgradedPlayerTag = true;
      }
   }

   public CompoundTag getLoadedPlayerTag() {
      this.updatePlayerTag();
      return this.loadedPlayerTag;
   }

   public void setXSpawn(int xSpawn) {
      this.xSpawn = xSpawn;
   }

   public void setYSpawn(int ySpawn) {
      this.ySpawn = ySpawn;
   }

   public void setZSpawn(int zSpawn) {
      this.zSpawn = zSpawn;
   }

   public void setGameTime(long gameTime) {
      this.gameTime = gameTime;
   }

   public void setDayTime(long dayTime) {
      this.dayTime = dayTime;
   }

   public void setSpawn(BlockPos spawn) {
      this.xSpawn = spawn.getX();
      this.ySpawn = spawn.getY();
      this.zSpawn = spawn.getZ();
   }

   public String getLevelName() {
      return this.levelName;
   }

   public void setLevelName(String levelName) {
      this.levelName = levelName;
   }

   public int getVersion() {
      return this.version;
   }

   public void setVersion(int version) {
      this.version = version;
   }

   public long getLastPlayed() {
      return this.lastPlayed;
   }

   public int getClearWeatherTime() {
      return this.clearWeatherTime;
   }

   public void setClearWeatherTime(int clearWeatherTime) {
      this.clearWeatherTime = clearWeatherTime;
   }

   public boolean isThundering() {
      return this.thundering;
   }

   public void setThundering(boolean thundering) {
      this.thundering = thundering;
   }

   public int getThunderTime() {
      return this.thunderTime;
   }

   public void setThunderTime(int thunderTime) {
      this.thunderTime = thunderTime;
   }

   public boolean isRaining() {
      return this.raining;
   }

   public void setRaining(boolean raining) {
      this.raining = raining;
   }

   public int getRainTime() {
      return this.rainTime;
   }

   public void setRainTime(int rainTime) {
      this.rainTime = rainTime;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public boolean isGenerateMapFeatures() {
      return this.generateMapFeatures;
   }

   public void setGenerateMapFeatures(boolean generateMapFeatures) {
      this.generateMapFeatures = generateMapFeatures;
   }

   public void setGameType(GameType gameType) {
      this.gameType = gameType;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public void setHardcore(boolean hardcore) {
      this.hardcore = hardcore;
   }

   public LevelType getGeneratorType() {
      return this.generator;
   }

   public void setGenerator(LevelType generator) {
      this.generator = generator;
   }

   public CompoundTag getGeneratorOptions() {
      return this.generatorOptions;
   }

   public void setGeneratorOptions(CompoundTag generatorOptions) {
      this.generatorOptions = generatorOptions;
   }

   public boolean getAllowCommands() {
      return this.allowCommands;
   }

   public void setAllowCommands(boolean allowCommands) {
      this.allowCommands = allowCommands;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(boolean initialized) {
      this.initialized = initialized;
   }

   public GameRules getGameRules() {
      return this.gameRules;
   }

   public double getBorderX() {
      return this.borderX;
   }

   public double getBorderZ() {
      return this.borderZ;
   }

   public double getBorderSize() {
      return this.borderSize;
   }

   public void setBorderSize(double borderSize) {
      this.borderSize = borderSize;
   }

   public long getBorderSizeLerpTime() {
      return this.borderSizeLerpTime;
   }

   public void setBorderSizeLerpTime(long borderSizeLerpTime) {
      this.borderSizeLerpTime = borderSizeLerpTime;
   }

   public double getBorderSizeLerpTarget() {
      return this.borderSizeLerpTarget;
   }

   public void setBorderSizeLerpTarget(double borderSizeLerpTarget) {
      this.borderSizeLerpTarget = borderSizeLerpTarget;
   }

   public void setBorderZ(double borderZ) {
      this.borderZ = borderZ;
   }

   public void setBorderX(double borderX) {
      this.borderX = borderX;
   }

   public double getBorderSafeZone() {
      return this.borderSafeZone;
   }

   public void setBorderSafeZone(double borderSafeZone) {
      this.borderSafeZone = borderSafeZone;
   }

   public double getBorderDamagePerBlock() {
      return this.borderDamagePerBlock;
   }

   public void setBorderDamagePerBlock(double borderDamagePerBlock) {
      this.borderDamagePerBlock = borderDamagePerBlock;
   }

   public int getBorderWarningBlocks() {
      return this.borderWarningBlocks;
   }

   public int getBorderWarningTime() {
      return this.borderWarningTime;
   }

   public void setBorderWarningBlocks(int borderWarningBlocks) {
      this.borderWarningBlocks = borderWarningBlocks;
   }

   public void setBorderWarningTime(int borderWarningTime) {
      this.borderWarningTime = borderWarningTime;
   }

   public Difficulty getDifficulty() {
      return this.difficulty;
   }

   public void setDifficulty(Difficulty difficulty) {
      this.difficulty = difficulty;
   }

   public boolean isDifficultyLocked() {
      return this.difficultyLocked;
   }

   public void setDifficultyLocked(boolean difficultyLocked) {
      this.difficultyLocked = difficultyLocked;
   }

   public TimerQueue getScheduledEvents() {
      return this.scheduledEvents;
   }

   public void fillCrashReportCategory(CrashReportCategory crashReportCategory) {
      crashReportCategory.setDetail("Level name", () -> {
         return this.levelName;
      });
      crashReportCategory.setDetail("Level seed", () -> {
         return String.valueOf(this.seed);
      });
      crashReportCategory.setDetail("Level generator", () -> {
         return String.format("ID %02d - %s, ver %d. Features enabled: %b", new Object[]{Integer.valueOf(this.generator.getId()), this.generator.getName(), Integer.valueOf(this.generator.getVersion()), Boolean.valueOf(this.generateMapFeatures)});
      });
      crashReportCategory.setDetail("Level generator options", () -> {
         return this.generatorOptions.toString();
      });
      crashReportCategory.setDetail("Level spawn location", () -> {
         return CrashReportCategory.formatLocation(this.xSpawn, this.ySpawn, this.zSpawn);
      });
      crashReportCategory.setDetail("Level time", () -> {
         return String.format("%d game time, %d day time", new Object[]{Long.valueOf(this.gameTime), Long.valueOf(this.dayTime)});
      });
      crashReportCategory.setDetail("Level storage version", () -> {
         String string = "Unknown?";

         try {
            switch(this.version) {
            case 19132:
               string = "McRegion";
               break;
            case 19133:
               string = "Anvil";
            }
         } catch (Throwable var3) {
            ;
         }

         return String.format("0x%05X - %s", new Object[]{Integer.valueOf(this.version), string});
      });
      crashReportCategory.setDetail("Level weather", () -> {
         return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", new Object[]{Integer.valueOf(this.rainTime), Boolean.valueOf(this.raining), Integer.valueOf(this.thunderTime), Boolean.valueOf(this.thundering)});
      });
      crashReportCategory.setDetail("Level game mode", () -> {
         return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", new Object[]{this.gameType.getName(), Integer.valueOf(this.gameType.getId()), Boolean.valueOf(this.hardcore), Boolean.valueOf(this.allowCommands)});
      });
   }

   public CompoundTag getDimensionData(DimensionType dimensionType) {
      CompoundTag compoundTag = (CompoundTag)this.dimensionData.get(dimensionType);
      return compoundTag == null?new CompoundTag():compoundTag;
   }

   public void setDimensionData(DimensionType dimensionType, CompoundTag compoundTag) {
      this.dimensionData.put(dimensionType, compoundTag);
   }

   public int getMinecraftVersion() {
      return this.minecraftVersion;
   }

   public boolean isSnapshot() {
      return this.snapshot;
   }

   public String getMinecraftVersionName() {
      return this.minecraftVersionName;
   }

   public Set getDisabledDataPacks() {
      return this.disabledDataPacks;
   }

   public Set getEnabledDataPacks() {
      return this.enabledDataPacks;
   }

   @Nullable
   public CompoundTag getCustomBossEvents() {
      return this.customBossEvents;
   }

   public void setCustomBossEvents(@Nullable CompoundTag customBossEvents) {
      this.customBossEvents = customBossEvents;
   }

   public int getWanderingTraderSpawnDelay() {
      return this.wanderingTraderSpawnDelay;
   }

   public void setWanderingTraderSpawnDelay(int wanderingTraderSpawnDelay) {
      this.wanderingTraderSpawnDelay = wanderingTraderSpawnDelay;
   }

   public int getWanderingTraderSpawnChance() {
      return this.wanderingTraderSpawnChance;
   }

   public void setWanderingTraderSpawnChance(int wanderingTraderSpawnChance) {
      this.wanderingTraderSpawnChance = wanderingTraderSpawnChance;
   }

   public void setWanderingTraderId(UUID wanderingTraderId) {
      this.wanderingTraderId = wanderingTraderId;
   }
}
