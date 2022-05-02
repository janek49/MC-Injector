package com.mojang.realmsclient.dto;

import com.fox2code.repacker.ClientJarOnly;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraft.realms.RealmsScreen;

@ClientJarOnly
public class RealmsWorldOptions extends ValueObject {
   public Boolean pvp;
   public Boolean spawnAnimals;
   public Boolean spawnMonsters;
   public Boolean spawnNPCs;
   public Integer spawnProtection;
   public Boolean commandBlocks;
   public Boolean forceGameMode;
   public Integer difficulty;
   public Integer gameMode;
   public String slotName;
   public long templateId;
   public String templateImage;
   public boolean adventureMap;
   public boolean empty;
   private static final boolean forceGameModeDefault = false;
   private static final boolean pvpDefault = true;
   private static final boolean spawnAnimalsDefault = true;
   private static final boolean spawnMonstersDefault = true;
   private static final boolean spawnNPCsDefault = true;
   private static final int spawnProtectionDefault = 0;
   private static final boolean commandBlocksDefault = false;
   private static final int difficultyDefault = 2;
   private static final int gameModeDefault = 0;
   private static final String slotNameDefault = "";
   private static final long templateIdDefault = -1L;
   private static final String templateImageDefault = null;
   private static final boolean adventureMapDefault = false;

   public RealmsWorldOptions(Boolean pvp, Boolean spawnAnimals, Boolean spawnMonsters, Boolean spawnNPCs, Integer spawnProtection, Boolean commandBlocks, Integer difficulty, Integer gameMode, Boolean forceGameMode, String slotName) {
      this.pvp = pvp;
      this.spawnAnimals = spawnAnimals;
      this.spawnMonsters = spawnMonsters;
      this.spawnNPCs = spawnNPCs;
      this.spawnProtection = spawnProtection;
      this.commandBlocks = commandBlocks;
      this.difficulty = difficulty;
      this.gameMode = gameMode;
      this.forceGameMode = forceGameMode;
      this.slotName = slotName;
   }

   public static RealmsWorldOptions getDefaults() {
      return new RealmsWorldOptions(Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(true), Integer.valueOf(0), Boolean.valueOf(false), Integer.valueOf(2), Integer.valueOf(0), Boolean.valueOf(false), "");
   }

   public static RealmsWorldOptions getEmptyDefaults() {
      RealmsWorldOptions realmsWorldOptions = new RealmsWorldOptions(Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(true), Boolean.valueOf(true), Integer.valueOf(0), Boolean.valueOf(false), Integer.valueOf(2), Integer.valueOf(0), Boolean.valueOf(false), "");
      realmsWorldOptions.setEmpty(true);
      return realmsWorldOptions;
   }

   public void setEmpty(boolean empty) {
      this.empty = empty;
   }

   public static RealmsWorldOptions parse(JsonObject jsonObject) {
      RealmsWorldOptions realmsWorldOptions = new RealmsWorldOptions(Boolean.valueOf(JsonUtils.getBooleanOr("pvp", jsonObject, true)), Boolean.valueOf(JsonUtils.getBooleanOr("spawnAnimals", jsonObject, true)), Boolean.valueOf(JsonUtils.getBooleanOr("spawnMonsters", jsonObject, true)), Boolean.valueOf(JsonUtils.getBooleanOr("spawnNPCs", jsonObject, true)), Integer.valueOf(JsonUtils.getIntOr("spawnProtection", jsonObject, 0)), Boolean.valueOf(JsonUtils.getBooleanOr("commandBlocks", jsonObject, false)), Integer.valueOf(JsonUtils.getIntOr("difficulty", jsonObject, 2)), Integer.valueOf(JsonUtils.getIntOr("gameMode", jsonObject, 0)), Boolean.valueOf(JsonUtils.getBooleanOr("forceGameMode", jsonObject, false)), JsonUtils.getStringOr("slotName", jsonObject, ""));
      realmsWorldOptions.templateId = JsonUtils.getLongOr("worldTemplateId", jsonObject, -1L);
      realmsWorldOptions.templateImage = JsonUtils.getStringOr("worldTemplateImage", jsonObject, templateImageDefault);
      realmsWorldOptions.adventureMap = JsonUtils.getBooleanOr("adventureMap", jsonObject, false);
      return realmsWorldOptions;
   }

   public String getSlotName(int i) {
      return this.slotName != null && !this.slotName.isEmpty()?this.slotName:(this.empty?RealmsScreen.getLocalizedString("mco.configure.world.slot.empty"):this.getDefaultSlotName(i));
   }

   public String getDefaultSlotName(int i) {
      return RealmsScreen.getLocalizedString("mco.configure.world.slot", new Object[]{Integer.valueOf(i)});
   }

   public String toJson() {
      JsonObject var1 = new JsonObject();
      if(!this.pvp.booleanValue()) {
         var1.addProperty("pvp", this.pvp);
      }

      if(!this.spawnAnimals.booleanValue()) {
         var1.addProperty("spawnAnimals", this.spawnAnimals);
      }

      if(!this.spawnMonsters.booleanValue()) {
         var1.addProperty("spawnMonsters", this.spawnMonsters);
      }

      if(!this.spawnNPCs.booleanValue()) {
         var1.addProperty("spawnNPCs", this.spawnNPCs);
      }

      if(this.spawnProtection.intValue() != 0) {
         var1.addProperty("spawnProtection", this.spawnProtection);
      }

      if(this.commandBlocks.booleanValue()) {
         var1.addProperty("commandBlocks", this.commandBlocks);
      }

      if(this.difficulty.intValue() != 2) {
         var1.addProperty("difficulty", this.difficulty);
      }

      if(this.gameMode.intValue() != 0) {
         var1.addProperty("gameMode", this.gameMode);
      }

      if(this.forceGameMode.booleanValue()) {
         var1.addProperty("forceGameMode", this.forceGameMode);
      }

      if(this.slotName != null && !this.slotName.equals("")) {
         var1.addProperty("slotName", this.slotName);
      }

      return var1.toString();
   }

   public RealmsWorldOptions clone() {
      return new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.slotName);
   }
}
