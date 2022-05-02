package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.util.ProgressListener;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;

@ClientJarOnly
public class RealmsAnvilLevelStorageSource {
   private final LevelStorageSource levelStorageSource;

   public RealmsAnvilLevelStorageSource(LevelStorageSource levelStorageSource) {
      this.levelStorageSource = levelStorageSource;
   }

   public String getName() {
      return this.levelStorageSource.getName();
   }

   public boolean levelExists(String string) {
      return this.levelStorageSource.levelExists(string);
   }

   public boolean convertLevel(String string, ProgressListener progressListener) {
      return this.levelStorageSource.convertLevel(string, progressListener);
   }

   public boolean requiresConversion(String string) {
      return this.levelStorageSource.requiresConversion(string);
   }

   public boolean isNewLevelIdAcceptable(String string) {
      return this.levelStorageSource.isNewLevelIdAcceptable(string);
   }

   public boolean deleteLevel(String string) {
      return this.levelStorageSource.deleteLevel(string);
   }

   public void renameLevel(String var1, String var2) {
      this.levelStorageSource.renameLevel(var1, var2);
   }

   public List getLevelList() throws LevelStorageException {
      List<RealmsLevelSummary> list = Lists.newArrayList();

      for(LevelSummary var3 : this.levelStorageSource.getLevelList()) {
         list.add(new RealmsLevelSummary(var3));
      }

      return list;
   }
}
