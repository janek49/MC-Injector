package net.minecraft.client.server;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportDetail;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedPlayerList;
import net.minecraft.client.server.LanServerPinger;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.Crypt;
import net.minecraft.util.profiling.GameProfiler;
import net.minecraft.world.Difficulty;
import net.minecraft.world.Snooper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final LevelSettings settings;
   private boolean paused;
   private int publishedPort = -1;
   private LanServerPinger lanPinger;
   private UUID uuid;

   public IntegratedServer(Minecraft minecraft, String var2, String levelName, LevelSettings settings, YggdrasilAuthenticationService yggdrasilAuthenticationService, MinecraftSessionService minecraftSessionService, GameProfileRepository gameProfileRepository, GameProfileCache gameProfileCache, ChunkProgressListenerFactory chunkProgressListenerFactory) {
      super(new File(minecraft.gameDirectory, "saves"), minecraft.getProxy(), minecraft.getFixerUpper(), new Commands(false), yggdrasilAuthenticationService, minecraftSessionService, gameProfileRepository, gameProfileCache, chunkProgressListenerFactory, var2);
      this.setSingleplayerName(minecraft.getUser().getName());
      this.setLevelName(levelName);
      this.setDemo(minecraft.isDemo());
      this.setBonusChest(settings.hasStartingBonusItems());
      this.setMaxBuildHeight(256);
      this.setPlayerList(new IntegratedPlayerList(this));
      this.minecraft = minecraft;
      this.settings = this.isDemo()?MinecraftServer.DEMO_SETTINGS:settings;
   }

   public void loadLevel(String var1, String var2, long var3, LevelType levelType, JsonElement jsonElement) {
      this.ensureLevelConversion(var1);
      LevelStorage var7 = this.getStorageSource().selectLevel(var1, this);
      this.detectBundledResources(this.getLevelIdName(), var7);
      LevelData var8 = var7.prepareLevel();
      if(var8 == null) {
         var8 = new LevelData(this.settings, var2);
      } else {
         var8.setLevelName(var2);
      }

      this.loadDataPacks(var7.getFolder(), var8);
      ChunkProgressListener var9 = this.progressListenerFactory.create(11);
      this.createLevels(var7, var8, this.settings, var9);
      if(this.getLevel(DimensionType.OVERWORLD).getLevelData().getDifficulty() == null) {
         this.setDifficulty(this.minecraft.options.difficulty, true);
      }

      this.prepareLevels(var9);
   }

   public boolean initServer() throws IOException {
      LOGGER.info("Starting integrated minecraft server version " + SharedConstants.getCurrentVersion().getName());
      this.setUsesAuthentication(true);
      this.setAnimals(true);
      this.setNpcsEnabled(true);
      this.setPvpAllowed(true);
      this.setFlightAllowed(true);
      LOGGER.info("Generating keypair");
      this.setKeyPair(Crypt.generateKeyPair());
      this.loadLevel(this.getLevelIdName(), this.getLevelName(), this.settings.getSeed(), this.settings.getLevelType(), this.settings.getLevelTypeOptions());
      this.setMotd(this.getSingleplayerName() + " - " + this.getLevel(DimensionType.OVERWORLD).getLevelData().getLevelName());
      return true;
   }

   public void tickServer(BooleanSupplier booleanSupplier) {
      boolean var2 = this.paused;
      this.paused = Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isPaused();
      GameProfiler var3 = this.getProfiler();
      if(!var2 && this.paused) {
         var3.push("autoSave");
         LOGGER.info("Saving and pausing game...");
         this.getPlayerList().saveAll();
         this.saveAllChunks(false, false, false);
         var3.pop();
      }

      if(!this.paused) {
         super.tickServer(booleanSupplier);
         int var4 = Math.max(2, this.minecraft.options.renderDistance + -2);
         if(var4 != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", Integer.valueOf(var4), Integer.valueOf(this.getPlayerList().getViewDistance()));
            this.getPlayerList().setViewDistance(var4);
         }

      }
   }

   public boolean canGenerateStructures() {
      return false;
   }

   public GameType getDefaultGameType() {
      return this.settings.getGameType();
   }

   public Difficulty getDefaultDifficulty() {
      return this.minecraft.level.getLevelData().getDifficulty();
   }

   public boolean isHardcore() {
      return this.settings.isHardcore();
   }

   public boolean shouldRconBroadcast() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return true;
   }

   public File getServerDirectory() {
      return this.minecraft.gameDirectory;
   }

   public boolean isDedicatedServer() {
      return false;
   }

   public boolean isEpollEnabled() {
      return false;
   }

   public void onServerCrash(CrashReport crashReport) {
      this.minecraft.delayCrash(crashReport);
   }

   public CrashReport fillReport(CrashReport crashReport) {
      crashReport = super.fillReport(crashReport);
      crashReport.getSystemDetails().setDetail("Type", (Object)"Integrated Server (map_client.txt)");
      crashReport.getSystemDetails().setDetail("Is Modded", () -> {
         String string = ClientBrandRetriever.getClientModName();
         if(!string.equals("vanilla")) {
            return "Definitely; Client brand changed to \'" + string + "\'";
         } else {
            string = this.getServerModName();
            return !"vanilla".equals(string)?"Definitely; Server brand changed to \'" + string + "\'":(Minecraft.class.getSigners() == null?"Very likely; Jar signature invalidated":"Probably not. Jar signature remains and both client + server brands are untouched.");
         }
      });
      return crashReport;
   }

   public void populateSnooper(Snooper snooper) {
      super.populateSnooper(snooper);
      snooper.setDynamicData("snooper_partner", this.minecraft.getSnooper().getToken());
   }

   public boolean publishServer(GameType gameType, boolean var2, int publishedPort) {
      try {
         this.getConnection().startTcpServerListener((InetAddress)null, publishedPort);
         LOGGER.info("Started serving on {}", Integer.valueOf(publishedPort));
         this.publishedPort = publishedPort;
         this.lanPinger = new LanServerPinger(this.getMotd(), publishedPort + "");
         this.lanPinger.start();
         this.getPlayerList().setOverrideGameMode(gameType);
         this.getPlayerList().setAllowCheatsForAllPlayers(var2);
         int var4 = this.getProfilePermissions(this.minecraft.player.getGameProfile());
         this.minecraft.player.setPermissionLevel(var4);

         for(ServerPlayer var6 : this.getPlayerList().getPlayers()) {
            this.getCommands().sendCommands(var6);
         }

         return true;
      } catch (IOException var7) {
         return false;
      }
   }

   public void stopServer() {
      super.stopServer();
      if(this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public void halt(boolean b) {
      this.executeBlocking(() -> {
         for(ServerPlayer var3 : Lists.newArrayList(this.getPlayerList().getPlayers())) {
            if(!var3.getUUID().equals(this.uuid)) {
               this.getPlayerList().remove(var3);
            }
         }

      });
      super.halt(b);
      if(this.lanPinger != null) {
         this.lanPinger.interrupt();
         this.lanPinger = null;
      }

   }

   public boolean isPublished() {
      return this.publishedPort > -1;
   }

   public int getPort() {
      return this.publishedPort;
   }

   public void setDefaultGameMode(GameType defaultGameMode) {
      super.setDefaultGameMode(defaultGameMode);
      this.getPlayerList().setOverrideGameMode(defaultGameMode);
   }

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public int getOperatorUserPermissionLevel() {
      return 2;
   }

   public int getFunctionCompilationLevel() {
      return 2;
   }

   public void setUUID(UUID uUID) {
      this.uuid = uUID;
   }

   public boolean isSingleplayerOwner(GameProfile gameProfile) {
      return gameProfile.getName().equalsIgnoreCase(this.getSingleplayerName());
   }
}
