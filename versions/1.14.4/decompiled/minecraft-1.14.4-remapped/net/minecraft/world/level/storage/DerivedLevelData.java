package net.minecraft.world.level.storage;

import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.timers.TimerQueue;

public class DerivedLevelData extends LevelData {
   private final LevelData wrapped;

   public DerivedLevelData(LevelData wrapped) {
      this.wrapped = wrapped;
   }

   public CompoundTag createTag(@Nullable CompoundTag compoundTag) {
      return this.wrapped.createTag(compoundTag);
   }

   public long getSeed() {
      return this.wrapped.getSeed();
   }

   public int getXSpawn() {
      return this.wrapped.getXSpawn();
   }

   public int getYSpawn() {
      return this.wrapped.getYSpawn();
   }

   public int getZSpawn() {
      return this.wrapped.getZSpawn();
   }

   public long getGameTime() {
      return this.wrapped.getGameTime();
   }

   public long getDayTime() {
      return this.wrapped.getDayTime();
   }

   public CompoundTag getLoadedPlayerTag() {
      return this.wrapped.getLoadedPlayerTag();
   }

   public String getLevelName() {
      return this.wrapped.getLevelName();
   }

   public int getVersion() {
      return this.wrapped.getVersion();
   }

   public long getLastPlayed() {
      return this.wrapped.getLastPlayed();
   }

   public boolean isThundering() {
      return this.wrapped.isThundering();
   }

   public int getThunderTime() {
      return this.wrapped.getThunderTime();
   }

   public boolean isRaining() {
      return this.wrapped.isRaining();
   }

   public int getRainTime() {
      return this.wrapped.getRainTime();
   }

   public GameType getGameType() {
      return this.wrapped.getGameType();
   }

   public void setXSpawn(int xSpawn) {
   }

   public void setYSpawn(int ySpawn) {
   }

   public void setZSpawn(int zSpawn) {
   }

   public void setGameTime(long gameTime) {
   }

   public void setDayTime(long dayTime) {
   }

   public void setSpawn(BlockPos spawn) {
   }

   public void setLevelName(String levelName) {
   }

   public void setVersion(int version) {
   }

   public void setThundering(boolean thundering) {
   }

   public void setThunderTime(int thunderTime) {
   }

   public void setRaining(boolean raining) {
   }

   public void setRainTime(int rainTime) {
   }

   public boolean isGenerateMapFeatures() {
      return this.wrapped.isGenerateMapFeatures();
   }

   public boolean isHardcore() {
      return this.wrapped.isHardcore();
   }

   public LevelType getGeneratorType() {
      return this.wrapped.getGeneratorType();
   }

   public void setGenerator(LevelType generator) {
   }

   public boolean getAllowCommands() {
      return this.wrapped.getAllowCommands();
   }

   public void setAllowCommands(boolean allowCommands) {
   }

   public boolean isInitialized() {
      return this.wrapped.isInitialized();
   }

   public void setInitialized(boolean initialized) {
   }

   public GameRules getGameRules() {
      return this.wrapped.getGameRules();
   }

   public Difficulty getDifficulty() {
      return this.wrapped.getDifficulty();
   }

   public void setDifficulty(Difficulty difficulty) {
   }

   public boolean isDifficultyLocked() {
      return this.wrapped.isDifficultyLocked();
   }

   public void setDifficultyLocked(boolean difficultyLocked) {
   }

   public TimerQueue getScheduledEvents() {
      return this.wrapped.getScheduledEvents();
   }

   public void setDimensionData(DimensionType dimensionType, CompoundTag compoundTag) {
      this.wrapped.setDimensionData(dimensionType, compoundTag);
   }

   public CompoundTag getDimensionData(DimensionType dimensionType) {
      return this.wrapped.getDimensionData(dimensionType);
   }

   public void fillCrashReportCategory(CrashReportCategory crashReportCategory) {
      crashReportCategory.setDetail("Derived", (Object)Boolean.valueOf(true));
      this.wrapped.fillCrashReportCategory(crashReportCategory);
   }
}
